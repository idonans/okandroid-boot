package com.okandroid.boot.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteClosable;
import android.graphics.BitmapRegionDecoder;

import com.okandroid.boot.lang.Available;
import com.okandroid.boot.lang.Charsets;
import com.okandroid.boot.lang.Progress;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * IO 辅助类
 * Created by idonans on 16-4-13.
 */
public final class IOUtil {

    private IOUtil() {
    }

    /**
     * SQLiteDatabase 不需要关闭
     */
    @Deprecated
    public static void closeQuietly(SQLiteClosable closeable) {
        // ignore
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeQuietly(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }


    public static void closeQuietly(BitmapRegionDecoder decoder) {
        if (decoder != null) {
            decoder.recycle();
        }
    }

    public static long copy(InputStream from, OutputStream to, Available available, Progress progress) throws Exception {
        long copy = 0;
        byte[] step = new byte[8 * 1024];
        int read;
        while ((read = from.read(step)) != -1) {
            AvailableUtil.mustAvailable(available);
            to.write(step, 0, read);
            copy += read;
            Progress.append(progress, read);
        }
        return copy;
    }

    public static long copy(byte[] from, OutputStream to, Available available, Progress progress) throws Exception {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(from);
            return copy(bais, to, available, progress);
        } finally {
            closeQuietly(bais);
        }
    }

    public static long copy(InputStream from, OutputStream to, long count, Available available, Progress progress) throws Exception {
        int stepSize = 8 * 1024;
        if (stepSize > count) {
            stepSize = (int) count;
        }
        long copy = 0;
        byte[] step = new byte[stepSize];
        int read;
        while ((read = from.read(step, 0, stepSize)) != -1) {
            AvailableUtil.mustAvailable(available);
            to.write(step, 0, read);
            copy += read;
            Progress.append(progress, read);
            if (count < copy) {
                throw new IndexOutOfBoundsException("count:" + count + ", copy:" + copy);
            }
            if (count == copy) {
                break;
            }
            long remainCount = count - copy;
            if (stepSize > remainCount) {
                stepSize = (int) remainCount;
            }
        }
        return copy;
    }

    public static long copy(File from, OutputStream to, Available available, Progress progress) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(from);
            return copy(fis, to, available, progress);
        } finally {
            closeQuietly(fis);
        }
    }

    public static long copy(InputStream from, File to, Available available, Progress progress) throws Exception {
        return copy(from, to, false, available, progress);
    }

    public static long copy(InputStream from, File to, boolean append, Available available, Progress progress) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(to, append);
            return copy(from, fos, available, progress);
        } finally {
            closeQuietly(fos);
        }
    }

    public static long copy(File from, File to, Available available, Progress progress) throws Exception {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(from);
            fos = new FileOutputStream(to);
            return copy(fis, fos, available, progress);
        } finally {
            closeQuietly(fis);
            closeQuietly(fos);
        }
    }

    public static byte[] read(InputStream is, long count, Available available, Progress progress) throws Exception {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            copy(is, baos, count, available, progress);
            return baos.toByteArray();
        } finally {
            closeQuietly(baos);
        }
    }

    public static String readAsString(InputStream is, long count, Available available, Progress progress) throws Exception {
        byte[] all = read(is, count, available, progress);
        return new String(all, Charsets.UTF8);
    }

    public static byte[] readAll(InputStream is, Available available, Progress progress) throws Exception {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            copy(is, baos, available, progress);
            return baos.toByteArray();
        } finally {
            closeQuietly(baos);
        }
    }

    public static String readAllAsString(InputStream is, Available available, Progress progress) throws Exception {
        byte[] all = readAll(is, available, progress);
        return new String(all, Charsets.UTF8);
    }

    /**
     * 读取所有的行并返回，注意返回的行内容不包括换行符号 '\n', '\r', "\r\n" <br>
     * 每读取一行，进度 append 1.
     */
    public static List<String> readAllLines(InputStream is, Available available, Progress progress) throws Exception {
        List<String> allLines = new ArrayList<>();
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, Charsets.UTF8);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                AvailableUtil.mustAvailable(available);
                allLines.add(line);
                Progress.append(progress, 1);
            }
        } finally {
            closeQuietly(br);
            closeQuietly(isr);
        }
        return allLines;
    }

}
