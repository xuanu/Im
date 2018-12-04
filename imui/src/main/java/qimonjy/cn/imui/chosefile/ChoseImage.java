package qimonjy.cn.imui.chosefile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.steven.fileprovider.FileProvider8;

import java.io.File;

/**
 * 选择图片
 * <p>
 * Created by zeffect on 2016/12/27.
 *
 * @author zzx
 */

public class ChoseImage {
    /**
     * 启动手机相机拍摄照片
     *
     * @param pContext     上下文
     * @param pFile        存储图片的路径
     * @param pRequestCode 请求码
     */
    public static void choseFromCameraCapture(Context pContext, File pFile, int pRequestCode) {
        if (pContext == null) {
            return;
        }
        if (pFile == null) {
            pFile = new File(pContext.getExternalCacheDir(), System.currentTimeMillis() + "");
        }
        Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider8.getUriForFile(pContext, pFile));
        if (pContext instanceof Activity) {
            ((Activity) pContext).startActivityForResult(intentFromCapture, pRequestCode);
        }
    }

    /**
     * 从本地相册?取图片
     *
     * @param pContext    上下文
     * @param requestCode 请求码
     * @see ChoseImage#getGalleryPath(Context, Intent) 可以用这个得到路径
     */
    public static void choseImageFromGallery(Context pContext, int requestCode) {
        if (pContext == null) {
            return;
        }
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_PICK);
        if (pContext instanceof Activity) {
            ((Activity) pContext).startActivityForResult(intentFromGallery, requestCode);
        }
    }

    /***
     * 获取路径，通用
     *
     * @param pContext 上下文
     * @param pIntent  用choseImageFromGallery返回的intent
     * @return 绝对路径
     * @see ChoseImage#choseImageFromGallery(Context, int) 可以用这个选择图片
     */
    public static String getGalleryPath(Context pContext, Intent pIntent) {
        String localPath = "";
        if (pIntent != null) {
            if (pIntent.getData() != null) {
                Cursor cr = pContext.getContentResolver().query(pIntent.getData(),
                        new String[]{MediaStore.Images.Media.DATA}, null,
                        null, null);
                if (cr == null) {
                    return localPath;
                }
                if (cr.moveToFirst()) {
                    localPath = cr.getString(cr
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                }
                cr.close();
            }
        }
        return localPath;
    }

}