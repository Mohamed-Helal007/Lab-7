package ems.exception;

public class EmsException extends Exception {
    public EmsException(String message) {
        super(message);
    }

    public EmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
