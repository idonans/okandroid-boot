package com.sample.boot;

import android.support.test.runner.AndroidJUnit4;

import com.okandroid.boot.lang.ClassName;
import com.okandroid.boot.lang.Log;
import com.okandroid.boot.util.FileUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private final String CLASS_NAME = ClassName.valueOf(this);

    @Test
    public void testFilenameAndExtension() throws Exception {
        String[] urls = new String[]{
                "http://test.com/a.jpg",
                "a.jpg",
                "./a.jpg",
                "./a.png@format.webp",
                "http://img.zcool.cn/community/01cc6559843cb80000002129693b5e.jpg@1280w_1l_2o_100sh.jpg",
                "/storage/0/Android/cache/a.png",
                "../../.tar.gz",
                "../../download.tar.gz",
                "app/local/download.apk",
                "#app/local/download.apk",
                "?app/local/download.apk",
                "app/local/download.apk????#a.png",
                "app/local/abc.png?a=b#c#d"
        };

        for (String url : urls) {
            String filename = FileUtil.getFilenameFromUrl(url);
            String extension = FileUtil.getFileExtensionFromUrl(url);
            Log.d(CLASS_NAME, url, "-> filename:", filename, "-> extension:", extension);
        }
    }
}
