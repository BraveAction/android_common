package org.yang.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

//import org.yuerbao.common.externalStorage.ExternalStorageUtil;
//import org.yuerbao.common.externalStorage.Setting;

/**
 * 图片处理工具类
 */
public class BitmapUtil {

    private static String TAG = "BitmapUtil";
    private static int DECIMALS = 2;//获取图片大小保存的小数点位数
    private static float MINSIZE = 50;//图片质量压缩的最小值
    private static float CRITICALSIZE = 1024;
    private static int QUALITY_LEVEL_LOW = 20;
    private static int QUALITY_LEVEL_MID = 50;
    private static int QUALITY_LEVEL_HEIGH = 100;


    /**
     * 缩放本地图片
     *
     * @param filepath 本地图片的地址
     * @param margin   外边距
     * @param padding  内边距
     * @return bitmap   缩放后的位图
     */
    public static Bitmap resizeImage(Context mContext, String filepath, int margin, int padding) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
        DisplayMetrics dp = mContext.getResources().getDisplayMetrics();
        int width = (dp.widthPixels - margin - padding) / 4;
        bitmap = Bitmap.createScaledBitmap(bitmap, width, width, true);
        return bitmap;
    }


//    /**
//     * 将图片转换为base64字符串
//     *
//     * @param imgFilePath //     * @param matrixScale 像素缩放比例
//     * @param quality     缩放质量
//     * @return
//     */
//    public static String getImageString(String imgFilePath, int quality) {
//
//        String imageString = null;
//        Bitmap mBitmap = BitmapFactory.decodeFile(imgFilePath);
//        if (mBitmap != null) {
//            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
//            Matrix matrix = new Matrix();
//            int width = mBitmap.getWidth();
//            if (width < 600) {
//                matrix.postScale(1, 1);
//            } else if (width == 600) {
//                matrix.postScale(1, 1);
//            } else if (width > 600) {
//                float mMatrixScale = (600f / width);
//                matrix.postScale(mMatrixScale, mMatrixScale);
//            }
//            Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
//            boolean flags = newBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
//            mBitmap.recycle();
//            newBitmap.recycle();
//
//            try {
//                if (flags) {
//                    byte[] bytes = Base64.encode(out.toByteArray(), Base64.NO_WRAP);
//                    imageString = new String(bytes, "GBK");
//                }
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return imageString;
//    }


    /**
     * 将图片转换为base64字符串
     *
     * @param imgFilePath    文件真实地址
     * @param maxNumOfPixels
     * @return 6-09 14:33:29.770   06-09 14:34:15.030
     */
    public static String getImageString(String imgFilePath, int maxNumOfPixels) {

        String imageBase64String = null;
        Bitmap mBitmap = compressBitmapToShow(imgFilePath, -1, maxNumOfPixels);
        if (mBitmap != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            boolean isSuccess = mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            if (isSuccess) {
                byte[] bytes = Base64.encode(out.toByteArray(), Base64.NO_WRAP);
                try {
                    imageBase64String = new String(bytes, "GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (mBitmap != null && mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageBase64String;
    }

    /**
     * 从文件中获取图片
     *
     * @param filePath
     * @param width
     * @param scaleable
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, int width, boolean scaleable) {
        if (!TextUtils.isEmpty(filePath)) {
            BitmapFactory.Options opts = null;
            if (width > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(filePath, opts);
                // 计算图片缩放比例
//                Log.d("ChatMessageAdapter", opts.outWidth + "");
//                Log.d("ChatMessageAdapter", opts.outHeight + "");

                float scaleHeight;
                if (scaleable) {
                    float scale = (float) width / opts.outWidth;
//                Log.d("ChatMessageAdapter", scale + "");
                    scaleHeight = scale * opts.outHeight;
                } else {
                    scaleHeight = width;
                }
//                Log.d("ChatMessageAdapter", width + "");
//                Log.d("ChatMessageAdapter", scaleHeight + "");
                final int minSideLength = (int) Math.min(width, scaleHeight);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, (int) (width * scaleHeight));

                opts.inScaled = true;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;

            }
            try {
                return BitmapFactory.decodeFile(filePath, opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromFile(byte[] bytes, int width, boolean scaleable) {
        if (bytes != null && bytes.length > 0) {
            BitmapFactory.Options opts = null;
            if (width > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                // 计算图片缩放比例

                float scaleHeight;
                if (scaleable) {
                    float scale = (float) width / opts.outWidth;
//                Log.d("ChatMessageAdapter", scale + "");
                    scaleHeight = scale * opts.outHeight;
                } else {
                    scaleHeight = width;
                }

                final int minSideLength = (int) Math.min(width, scaleHeight);
                opts.inSampleSize = computeSampleSize(opts, minSideLength, (int) (width * scaleHeight));
                opts.inScaled = true;
                opts.inPreferredConfig = Bitmap.Config.RGB_565;
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    /**
     * 压缩高分辨率图片用于显示
     *
     * @param path           地址
     * @param minSideLength  压缩比例参考值
     * @param maxNumOfPixels 默认压缩分辨率
     * @return 位图
     */
    public static Bitmap compressBitmapToShow(String path, int minSideLength, int maxNumOfPixels) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;//不会为图片创建内存空间
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
        opts.inDither = false;    /*不进行图片抖动处理*/
        opts.inPreferredConfig = null;  /*设置让解码器以最佳方式解码*/
//        Bitmap.Config.ALPHA_8 / Bitmap.Config.ARGB_4444;
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, opts);
    }

    /**
     * 计算图片的压缩质量
     *
     * @param imagePath
     * @return
     */
    private static int calcQuality(String imagePath) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(imagePath);
            int available = fileInputStream.available();
            //图片大小kb
            BigDecimal bigDecimal = new BigDecimal((float) available / CRITICALSIZE);
            //图片大小保留两人位小数
            BigDecimal bigDecimalScale = bigDecimal.setScale(DECIMALS, BigDecimal.ROUND_HALF_UP);
            float value = bigDecimalScale.floatValue();
            //图片小于50k
            if (value <= MINSIZE) {
                return QUALITY_LEVEL_HEIGH;
                //图片大于50k小于1m
            } else if (value > MINSIZE && value < CRITICALSIZE + MINSIZE) {
                return QUALITY_LEVEL_MID;
                //图片大于1m
            } else if (value >= CRITICALSIZE + MINSIZE) {
                return QUALITY_LEVEL_LOW;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

//    /**
//     * 压缩高分辨率图片用于显示
//     *
//     * @param path           地址
//     * @param minSideLength  压缩比例参考值
//     * @param maxNumOfPixels 默认压缩分辨率
//     * @return 位图
//     */
//    public static String compressBitmap(String path, int minSideLength, int maxNumOfPixels) {
//        BitmapFactory.Options opts = new BitmapFactory.Options();
//        opts.inJustDecodeBounds = true;//不会为图片创建内存空间
//        BitmapFactory.decodeFile(path, opts);
//        int computeSampleSize = computeSampleSize(opts, minSideLength, maxNumOfPixels);
//        opts.inSampleSize = computeSampleSize;
//        opts.inDither = false;    /*不进行图片抖动处理*/
//        opts.inPreferredConfig = null;  /*设置让解码器以最佳方式解码*/
//        opts.inJustDecodeBounds = false;
//        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
//
//        if (bitmap != null) {
//            图片复制
//            File file = Setting.createImageFile();
//            FileOutputStream fileOutputStream = null;
//            boolean isSuccess = false;
//            try {
//                fileOutputStream = new FileOutputStream(file);
//                isSuccess = bitmap.compress(Bitmap.CompressFormat
//                        .JPEG, 100, fileOutputStream);
//            Log.d(TAG, "图片压缩后的大小kb:" + fileOutputStream. / 1024 + "");
//            Log.d(TAG, "图片压缩后的大小kb:" + fileOutputStream.size() + "");
//                fileOutputStream.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//                isSuccess = false;
//            } finally {
//                if (fileOutputStream != null) {
//                    try {
//                        fileOutputStream.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                if (bitmap != null && bitmap.isRecycled()) {
//                    bitmap.recycle();
//                }
//            }
//
//            return isSuccess ? file.getAbsolutePath() : null;
//        } else {
//            return null;
//        }
//
//    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 将图片旋转放正
     *
     * @param path
     * @return
     */
    public static Bitmap rotaingImageView(String path) {
        int degrees = getRotateDegree(path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return rotaingImageView(bitmap, degrees);
    }

    /**
     * 旋转图片
     *
     * @param bitmap  位图
     * @param degrees 角度
     * @return 旋转后的位图
     */
    public static Bitmap rotaingImageView(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int getRotateDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            //Orientation Exif tag.
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static byte[] getVideoThumbnailData(String videoPath) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getHeight(), bitmap.getWidth(),
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean flag = bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        if (flag) {
            bitmap.recycle();
        }
        return baos.toByteArray();
    }


//    /**
//     * 保存位图到指定路径下(目录:{@link org.yuerbao.common.externalStorage.Setting}.IMAGECACHE_DIR,文件名:当前时间)
//     *
//     * @return 返回图片文件
//     */
//    public static File saveBitmap(Bitmap bitmap) {
//        File file = Setting.createImageFile();
//        FileOutputStream fileOutputStream = null;
//        boolean flag = false;
//        try {
//            fileOutputStream = new FileOutputStream(file);
//            if (ExternalStorageUtil.isWriteAble()) {
//                flag = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } finally {
//            if (fileOutputStream != null) {
//                try {
//                    fileOutputStream.close();
//                } catch (Exception e) {
//                }
//            }
//        }
//
//        return flag ? file : null;
//    }
//

}
