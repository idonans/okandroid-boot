package com.okandroid.boot.lang;

import android.support.annotation.Nullable;

/**
 * log 处理
 * Created by idonans on 16-4-13.
 */
public class Log {

    private static Printer sPrinter = new LogPrinter();

    private Log() {
    }

    public static void setLogLevel(int logLevel) {
        sPrinter.setLogLevel(logLevel);
    }

    public static void setLogTag(Object logTag) {
        sPrinter.setLogTag(logTag);
    }

    public static void v(@Nullable Object... messages) {
        sPrinter.print(android.util.Log.VERBOSE, messages);
    }

    public static void d(@Nullable Object... messages) {
        sPrinter.print(android.util.Log.DEBUG, messages);
    }

    public static void e(@Nullable Object... messages) {
        sPrinter.print(android.util.Log.ERROR, messages);
    }

    public interface Printer {

        void setLogLevel(int logLevel);

        void setLogTag(Object logTag);

        void print(int logLevel, Object... messages);

    }

    private static class LogPrinter implements Printer {

        private String mLogTag = "okandroid";
        private int mLogLevel = android.util.Log.DEBUG;

        @Override
        public void setLogLevel(int logLevel) {
            mLogLevel = logLevel;
        }

        @Override
        public void setLogTag(Object logTag) {
            mLogTag = wrapperSafetyLogTag(logTag);
        }

        @Override
        public void print(int logLevel, Object... messages) {
            if (isLoggable(logLevel)) {
                android.util.Log.println(logLevel, mLogTag, formatLogMessages(messages));
            }
        }

        private String formatLogMessages(Object... messages) {
            StringBuilder builder = new StringBuilder();
            if (messages == null) {
                builder.append(":null");
            } else if (messages.length == 0) {
                builder.append(":empty");
            } else {
                for (Object message : messages) {
                    builder.append(String.valueOf(message));
                    builder.append(" ");
                }
            }
            return builder.toString();
        }

        private boolean isLoggable(int logLevel) {
            return logLevel <= mLogLevel;
        }

        /**
         * log tag 长度不能超过 23
         */
        private String wrapperSafetyLogTag(Object logTag) {
            String tag = String.valueOf(logTag);
            int length = tag.length();
            if (length > 23) {
                return tag.substring(0, 23);
            } else {
                return tag;
            }
        }

    }

}
