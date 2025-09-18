package org.entando.entando.aps.servlet;

import java.io.IOException;
import org.entando.entando.aps.system.services.IFeatureFlag;

public interface IFSuppressNIOException {
    boolean SUPPRESS_NIO_EXCEPTIONS = IFeatureFlag.readEnablementStatus("SUPPRESS_NIO_EXCEPTIONS");

    static boolean isBrokenPipeOrConnectionReset(Throwable t) {
        Throwable cause = t;

        while (cause != null) {
            if (cause instanceof IOException) {
                String message = cause.getMessage();
                if (message != null) {
                    String lowerMsg = message.toLowerCase();
                    if (lowerMsg.contains("broken pipe")
                            || lowerMsg.contains("connection reset")) {
                        return true;
                    }
                }
            }
            cause = cause.getCause();
        }
        return false;
    }


    default boolean isEnabled() {
        return SUPPRESS_NIO_EXCEPTIONS  ;
    }
}
