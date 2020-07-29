package org.entando.entando.aps.system.exception;

public class CSRFProtectionException extends RuntimeException {

    private String message;

    public CSRFProtectionException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
