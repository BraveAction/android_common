package org.yang.common.glide;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import org.yang.common.net.RetrofitClient;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Gxy on 2017/5/21
 */

public abstract class OkHttpGlideModule implements GlideModule {
    public static String DOWNLOADDIRECTORY_PATH;

    /**
     * 获取app文件保存目录
     *
     * @return
     */
    @NonNull
    protected abstract String getAppFileDir();

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        Log.d("OkHttpGlideModule", "applyOptions");
        //设置图片的显示格式ARGB_8888(指图片大小为32bit)
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        //设置磁盘缓存目录（和创建的缓存目录相同）
        File storageDirectory = Environment.getExternalStorageDirectory();
//        context.getPackageName()
        DOWNLOADDIRECTORY_PATH = storageDirectory + File.separator + getAppFileDir() + File.separator + "imageCache";
        //设置缓存的大小为100M
        final int cacheSize = 100 * 1000 * 1000;
//        builder.setDiskCache(new DiskLruCacheFactory(DOWNLOADDIRECTORY_PATH, cacheSize));

        File cacheDir = new File(DOWNLOADDIRECTORY_PATH);
        if (cacheDir != null) {
            Log.i("OkHttpsGlideModule", cacheDir.getAbsolutePath());
            if (!cacheDir.mkdirs() && (!cacheDir.exists() || !cacheDir.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                cacheDir = Glide.getPhotoCacheDir(context);
                DOWNLOADDIRECTORY_PATH = cacheDir.getAbsolutePath();
            }
        } else {
            Log.d("OkHttpsGlideModule", "没有目录");
        }

        final File directory = cacheDir;
        builder.setDiskCache(
                new DiskCache.Factory() {
                    @Override
                    public DiskCache build() {
                        return DiskLruCacheWrapper.get(directory, cacheSize);
                    }
                });
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        Log.d("OkHttpGlideModule", "registerComponents");
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(RetrofitClient.getOkHttpClient()));
    }
}
