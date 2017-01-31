package com.okandroid.boot.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.okandroid.boot.lang.Charsets;

import java.security.MessageDigest;

/**
 * MD5 辅助类
 * Created by idonans on 16-4-14.
 */
public class MD5Util {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 得到一个由数字和小写字母构成的32位的字符串。
     */
    @NonNull
    public static String md5(@Nullable String str) {
        try {
            if (str == null) {
                str = "";
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] data = messageDigest.digest(str.getBytes(Charsets.UTF8));
            char[] result = new char[data.length * 2];
            int c = 0;
            for (byte b : data) {
                result[c++] = HEX_DIGITS[(b >> 4) & 0xf];
                result[c++] = HEX_DIGITS[b & 0xf];
            }
            return new String(result);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
