package com.okandroid.boot.lang;

/**
 * Created by idonans on 16-4-13.
 */
public class NotAvailableException extends RuntimeException {

    public NotAvailableException() {
    }

    public NotAvailableException(String detailMessage) {
        super(detailMessage);
    }

    public NotAvailableException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NotAvailableException(Throwable throwable) {
        super(throwable);
    }

}
