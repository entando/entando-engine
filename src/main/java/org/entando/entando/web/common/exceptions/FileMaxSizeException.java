package org.entando.entando.web.common.exceptions;

import com.agiletec.aps.system.exception.EntRuntimeException;

public class FileMaxSizeException extends EntRuntimeException {

    public FileMaxSizeException(String message) {
        super(message);
    }

    public FileMaxSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
