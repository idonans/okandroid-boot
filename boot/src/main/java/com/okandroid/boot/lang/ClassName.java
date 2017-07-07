package com.okandroid.boot.lang;

/**
 * Created by idonans on 2017/7/7.
 */

public class ClassName {

    public static String valueOf(Object object) {
        return object.getClass().getSimpleName() + "@" + object.hashCode();
    }

}
