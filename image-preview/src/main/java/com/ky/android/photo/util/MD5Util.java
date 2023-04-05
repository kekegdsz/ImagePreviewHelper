package com.ky.android.photo.util;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MD5Util {


    private static final char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public MD5Util() {
    }

    @NonNull
    public static String getFileMD5(@NonNull File file) {
        FileInputStream in = null;

        String var3;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            var3 = MD5((ByteBuffer) ch.map(FileChannel.MapMode.READ_ONLY, 0L, file.length()));
            return var3;
        } catch (FileNotFoundException var15) {
            var3 = "";
            return var3;
        } catch (IOException var16) {
            var3 = "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var14) {
                }
            }

        }

        return var3;
    }

    @NonNull
    public static String MD5(@NonNull String s) {
        MessageDigest mdInst;
        try {
            mdInst = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var11) {
            var11.printStackTrace();
            return "";
        }

        byte[] btInput = s.getBytes();
        mdInst.update(btInput);
        byte[] md = mdInst.digest();
        int length = md.length;
        char[] str = new char[length * 2];
        int k = 0;
        byte[] var7 = md;
        int var8 = md.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            byte b = var7[var9];
            str[k++] = hexDigits[b >>> 4 & 15];
            str[k++] = hexDigits[b & 15];
        }

        return new String(str);
    }

    @NonNull
    private static String getSubStr(@NonNull String str, int subNu, char replace) {
        int length = str.length();
        if (length > subNu) {
            str = str.substring(length - subNu, length);
        } else if (length < subNu) {
            str = str + createPaddingString(subNu - length, replace);
        }

        return str;
    }

    @NonNull
    private static String createPaddingString(int n, char pad) {
        if (n <= 0) {
            return "";
        } else {
            char[] paddingArray = new char[n];
            Arrays.fill(paddingArray, pad);
            return new String(paddingArray);
        }
    }

    @NonNull
    private static String MD5(ByteBuffer buffer) {
        String s = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer);
            byte[] tmp = md.digest();
            char[] str = new char[32];
            int k = 0;

            for (int i = 0; i < 16; ++i) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            s = new String(str);
        } catch (NoSuchAlgorithmException var8) {
            var8.printStackTrace();
        }

        return s;
    }

    public static String getFileMD52(File file) {
        if (!file.isFile()) {
            return null;
        } else {
            MessageDigest digest = null;
            FileInputStream in = null;
            byte[] buffer = new byte[1024];

            try {
                digest = MessageDigest.getInstance("MD5");
                in = new FileInputStream(file);

                int len;
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    digest.update(buffer, 0, len);
                }

                in.close();
                return bytesToHexString(digest.digest());
            } catch (Exception var6) {
                var6.printStackTrace();
                return null;
            }
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src != null && src.length > 0) {
            for (int i = 0; i < src.length; ++i) {
                int v = src[i] & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static String getMD5Str(String str) {
        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            hash = digest.digest(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        byte[] var3 = hash;
        int var4 = hash.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            if ((b & 255) < 16) {
                hex.append("0");
            }

            hex.append(Integer.toHexString(b & 255));
        }

        String md5 = hex.toString();
        md5 = md5.toUpperCase();
        return md5;
    }
}