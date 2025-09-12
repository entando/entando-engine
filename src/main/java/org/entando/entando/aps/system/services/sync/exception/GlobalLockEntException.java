package org.entando.entando.aps.system.services.sync.exception;

import org.entando.entando.ent.exception.EntException;

public class GlobalLockEntException extends EntException {

    public GlobalLockEntException(String message) {
        super(message);
    }

    public GlobalLockEntException(String message, Throwable cause) {
        super(message, cause);
    }

}
