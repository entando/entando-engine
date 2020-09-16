package org.entando.entando.ent.exception;

import java.util.Arrays;

public interface IEntException {

    IEntException TERM = new IEntException() {
    };

    static Object[] extractActualArgs(Object[] args) {
        int e = args.length;
        if (e == 0) {
            return new Object[0];
        } else {
            if (args[e - 1] instanceof Throwable || args[e - 1] == TERM) {
                return Arrays.copyOf(args, e - 1);
            } else {
                return args;
            }
        }
    }

    static Throwable extractActualCause(Object[] args) {
        if (args.length == 0) {
            return null;
        } else {
            Object lastElement = args[args.length - 1];
            return (lastElement instanceof Throwable) ? (Throwable) lastElement : null;
        }
    }
}
