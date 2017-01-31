package com.okandroid.boot.lang;

/**
 * 标记一个操作是否需要继续处理，一个视图是否还可用，或者一个请求是否已经取消或者过期。
 * Created by idonans on 16-4-13.
 */
public interface Available {

    boolean isAvailable();

}
