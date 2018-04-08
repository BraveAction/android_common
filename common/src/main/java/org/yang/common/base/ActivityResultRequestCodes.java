package org.yang.common.base;

/**
 * Created by Gxy on 2017/6/19
 */

public interface ActivityResultRequestCodes {
    int WRITE_EXTERNAL_STORAGE_PERMISSION = 0;      //将文件写入sd卡的权限请求代码
    int READ_EXTERNAL_STORAGE_PERMISSION = 1;      //读取文件的权限请求代码
    int CAMERA_AND_PHOTO_PERMISSION = 2;            //拍照和选取照片的权限请求代码
    int CAMERA_AND_PHOTO_FORRESULT = 3;             //拍照和选取照片后的结果请求代码


}
