package com.ky.android.photo.util;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ky.android.photo.AppGlobals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static File getParentFile(@NonNull Context context) {
        final File externalSaveDir = context.getExternalCacheDir();
        if (externalSaveDir == null) {
            return context.getCacheDir();
        } else {
            return externalSaveDir;
        }
    }

    /**
     * 保存图片到相册
     *
     * @param context
     * @param path
     * @param srcFilename
     * @param attachExtension
     * @return 成功：0 失败：-1 已存在：1
     */
    public static int saveFileToAlbum(Context context, String path, String srcFilename, String attachExtension) {
        return saveFileToAlbum(context, path, getSavePathInAlbum(srcFilename, attachExtension), true);
    }

    public static String getSavePathInAlbum(String fileName, String fileExtension) {
        String fullFileName;
        if (!TextUtils.isEmpty(FileUtils.getExtensionName(fileName))) {
            fullFileName = fileName;
        } else {
            fullFileName = fileName + "." + (TextUtils.isEmpty(fileExtension) ? "jpg" : fileExtension);
        }
        return getSystemImageDirectory() + fullFileName;
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    public static String getSystemImageDirectory() {
        String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        return picturePath + "/kuasheng/";
    }


    /**
     * @return 成功：0 失败：-1 已存在：1
     */
    public static int saveFileToAlbum(Context context, String src, String dst, boolean hint) {
        if (TextUtils.isEmpty(src)) {
            if (hint) {
                Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
            return -1;
        }

        if (new File(dst).exists()) {
            if (hint) {
                Toast.makeText(context, "已保存到手机相册", Toast.LENGTH_SHORT).show();
            }
            return 1;
        } else {
            if (copy(src, dst) != -1) {
                try {
                    ContentValues values = new ContentValues(2);
                    values.put(MediaStore.Images.Media.MIME_TYPE, getMimeType(dst));
                    values.put(MediaStore.Images.Media.DATA, dst);
                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    if (hint) {
                        Toast.makeText(context, "已保存到手机相册", Toast.LENGTH_SHORT).show();
                    }
                    return 0;
                } catch (Exception e) {
                    if (hint) {
                        Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (hint) {
                    Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return -1;
    }

    public static long copy(String srcPath, String dstPath) {
        if (TextUtils.isEmpty(srcPath) || TextUtils.isEmpty(dstPath)) {
            return -1;
        }

        File source = new File(srcPath);
        if (!source.exists()) {
            return -1;
        }

        if (srcPath.equals(dstPath)) {
            return source.length();
        }

        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            fcin = new FileInputStream(source).getChannel();
            fcout = new FileOutputStream(create(dstPath)).getChannel();
            ByteBuffer tmpBuffer = ByteBuffer.allocateDirect(4096);
            while (fcin.read(tmpBuffer) != -1) {
                tmpBuffer.flip();
                fcout.write(tmpBuffer);
                tmpBuffer.clear();
            }
            return source.length();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fcin != null) {
                    fcin.close();
                }
                if (fcout != null) {
                    fcout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static File create(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        File f = new File(filePath);
        if (!f.getParentFile().exists()) {// 如果不存在上级文件夹
            f.getParentFile().mkdirs();
        }
        try {
            f.createNewFile();
            return f;
        } catch (IOException e) {
            if (f != null && f.exists()) {
                f.delete();
            }
            return null;
        }
    }

    public static String getMimeType(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        String type = null;
        String extension = getExtensionName(filePath.toLowerCase());
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        if (TextUtils.isEmpty(type) && filePath.endsWith("aac")) {
            type = "audio/aac";
        }

        return type;
    }

    public static int saveVideoToAlbum(final String src, final String duration, final String extensionName) {
        if (TextUtils.isEmpty(src)) {
            Toast.makeText(AppGlobals.get(), "视频保存失败", Toast.LENGTH_SHORT).show();
            return -1;
        }
        String suf = getExtensionName(src);
        if (TextUtils.isEmpty(suf) && !TextUtils.isEmpty(extensionName)) {
            suf = extensionName;
        }
        if (TextUtils.isEmpty(suf)) {
            suf = "mp4";
        }
        final String dst = getSystemImageDirectory() + System.currentTimeMillis() + "." + suf;
        Log.e(TAG, "dst " + dst);
        if (new File(dst).exists()) {
            Toast.makeText(AppGlobals.get(), "已保存至" + dst, Toast.LENGTH_SHORT).show();
            return 1;
        } else {
            // 异步保存视频到本地
            VideoAsyncTask task = new VideoAsyncTask();
            task.execute(src, dst, duration);
            return 0;
        }
    }

    static class VideoAsyncTask extends AsyncTask<String, Void, Boolean> {

        String[] data;

        @Override
        protected Boolean doInBackground(String... s) {
            data = s;
            return copy(s[0], s[1]) != -1;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                try {
                    ContentValues values = new ContentValues(2);
                    values.put(MediaStore.Video.Media.MIME_TYPE, getMimeType(data[1]));
                    values.put(MediaStore.Video.Media.DATA, data[1]);
                    values.put(MediaStore.Video.Media.DURATION, data[2]);
                    AppGlobals.get().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                    Toast.makeText(AppGlobals.get(), "已保存至" + data[1], Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(AppGlobals.get(), "视频保存失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AppGlobals.get(), "视频保存失败", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled(Boolean s) {
            super.onCancelled(s);
        }
    }
}
