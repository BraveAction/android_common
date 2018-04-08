package org.yang.common.net;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Gxy on 2017/3/31
 */
public final class RetrofitClient {
    public static final long DEFAULT_TIMEOUT = 6000;
    public static final long DISK_CACHE_MAXSIZ = 10 * 1024 * 1024;
    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型
    private static final String KEY_STORE_PASSWORD = "yuerbao";//证书密码（应该是客户端证书密码）
    private static final String KEY_STORE_TRUST_PASSWORD = "yuerbao";//授信证书密码（应该是服务端证书密码）
    private static RetrofitClient ourInstance;
    private static Retrofit mRetrofit;
    private static String mBaseUrl = "";
    private static Context mContext;
    private static List<TokenInterceptor> mInterceptors;
    private static Class mServicesClass;
    private static Object mServices;
    private static Gson mGson;
    private static OkHttpClient mOkHttpClient;

    private RetrofitClient() {
        mRetrofit = buildRetrofit();
        mServices = mRetrofit.create(mServicesClass);
    }

    /**
     * @param context
     * @param baseUrl
     * @param servicesClass //服务类{@link Retrofit#create(Class)}
     * @param interceptors  //用于添加特定拦截器()
     */
    public static void init(Context context, String baseUrl, Class servicesClass, List<TokenInterceptor> interceptors) {
        mContext = context;
        mGson = new Gson();
        mBaseUrl = baseUrl;
        mServicesClass = servicesClass;
        mInterceptors = interceptors;
    }

    /**
     * @param context
     * @param baseUrl
     * @param servicesClass //服务类{@link Retrofit#create(Class)}
     */
    public static void init(Context context, String baseUrl, Class servicesClass) {
        mContext = context;
        mGson = new Gson();
        mBaseUrl = baseUrl;
        mServicesClass = servicesClass;
    }

    public static RetrofitClient getInstance() {
        if (ourInstance == null) {
            ourInstance = new RetrofitClient();
        }
        return ourInstance;
    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private static Retrofit buildRetrofit() {

        OkHttpClient.Builder builder = getOKhttpBuilder();

        mOkHttpClient = builder.build();

        if (mInterceptors != null) {
            addOkHttpInterceptors(builder, mInterceptors);
        }

        return getRetrofitBuilder(builder.build()).baseUrl(mBaseUrl).build();
    }

    private static Retrofit buildDefaultRetrofit(String baseUrl) {

        mOkHttpClient = getOKhttpBuilder().build();

        return getRetrofitBuilder(mOkHttpClient).baseUrl(baseUrl).build();
    }

    private static OkHttpClient.Builder getOKhttpBuilder() {
        ClearableCookieJar cookieJar =
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));

//         SSLSocketFactory sslSocketFactory = getSocketFactory(mContext);

//        X509TrustManager trustManager;
//        SSLSocketFactory sslSocketFactory;
//        try {
//            trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, new TrustManager[]{trustManager}, null);
//            sslSocketFactory = sslContext.getSocketFactory();
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }

        //设置SSLContext
        SSLContext sslcontext = null;
        X509TrustManager trustManager = getX509TrustManager();
        try {
            sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{trustManager}, null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //XXX 校验服务器域名
//                HostnameVerifier hostnameVerifier1 = HttpsURLConnection.getDefaultHostnameVerifier();
//                Boolean result = hostnameVerifier1.verify("jiexian.biz", session);
//                return result;
                return true;
            }
        };


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
//                .addNetworkInterceptor(new CacheInterceptor())        //get请求缓存
                .hostnameVerifier(hostnameVerifier)
//                .sslSocketFactory(sslSocketFactory, getX509TrustManager(sslSocketFactory))        //https双向认证
                .sslSocketFactory(sslcontext.getSocketFactory(), trustManager)
                .cache(new Cache(mContext.getCacheDir(), DISK_CACHE_MAXSIZ))
                .cookieJar(cookieJar)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);


        return builder;
    }


    private static Retrofit.Builder getRetrofitBuilder(OkHttpClient okHttpClient) {
        Converter.Factory gsonConverterFactory = GsonConverterFactory.create(mGson);
        return new Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory);
//                .addConverterFactory(new JsonStringConverterFactory(gsonConverterFactory));
    }

    private static X509TrustManager getX509TrustManager(SSLSocketFactory sslSocketFactory) {
        return Platform.get().trustManager(sslSocketFactory);
    }

    /**
     * @return
     */
    private static X509Certificate getX509Certificate() {
        X509Certificate serverCert = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream trust_input = mContext.getAssets().open("server01.cer");
            serverCert = (X509Certificate) certificateFactory.generateCertificate(trust_input);

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serverCert;
    }

    private static X509TrustManager getX509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
                if (x509Certificates == null) {
                    throw new IllegalArgumentException("check server x509Certificates is null");
                }
                if (x509Certificates.length < 0) {
                    throw new IllegalArgumentException("check server x509Certificates is empty");
                }

                for (X509Certificate cert : x509Certificates) {
                    cert.checkValidity();
                    try {
                        X509Certificate finalServerCert = getX509Certificate();
                        cert.verify(finalServerCert.getPublicKey());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
    }

    /**
     * 源有双向认证
     *
     * @param context
     * @return
     */
    private static SSLSocketFactory getSocketFactory(Context context) {

        InputStream trust_input = null;//服务器授信证书
        InputStream client_input = null;
        try {
            trust_input = context.getAssets().open("test_serverkey.bks");
            client_input = context.getAssets().open("test_clientkey.p12");//客户端证书

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            trustStore.load(trust_input, KEY_STORE_TRUST_PASSWORD.toCharArray());
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            keyStore.load(client_input, KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (trust_input != null) {
                    trust_input.close();
                }
                if (client_input != null) {
                    client_input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加拦截器
     *
     * @param builder
     * @param interceptors
     */
    private static void addOkHttpInterceptors(OkHttpClient.Builder builder, List<TokenInterceptor> interceptors) {
        for (Interceptor interceptor : interceptors) {
            builder.addInterceptor(interceptor);
        }
    }

    /**
     * 获取指定api接口服务
     *
     * @return
     */
    public static Object createService(Class service, String baseUrl) {
        Retrofit retrofit = buildDefaultRetrofit(baseUrl);
        return retrofit.create(service);
    }

    /**
     * 获取药商接口服务
     *
     * @return
     */
    public Object getServices() {
        return mServices;
    }


//    public static void getData(String url, Map parameters, final Subscriber<Object> objectSubscriber) {
//        Flowable flowable = mServices.executeGet(url, parameters);
//        flowable.compose(new FlowableTransformerOnBackground())
//                .map(new Mapper(objectSubscriber))
//                .safeSubscribe(objectSubscriber);
//    }
//
//    public static void postData(String url, final Subscriber<Object> objectSubscriber, Object parameter) {
//        Flowable flowable = mServices.executePost(url, parameter);
//        flowable.compose(new FlowableTransformerOnBackground())
//                .map(new Mapper(objectSubscriber))
//                .safeSubscribe(objectSubscriber);
//    }
}
