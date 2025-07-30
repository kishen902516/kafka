package com.example.kafka.abstraction.security;

public class CertificateException extends RuntimeException {

    public CertificateException(String message) {
        super(message);
    }

    public CertificateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CertificateException(Throwable cause) {
        super(cause);
    }
}