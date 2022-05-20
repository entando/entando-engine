package org.entando.entando.web.common.exceptions;

import org.springframework.validation.BindingResult;

public class ValidationUnprocessableEntityException extends RuntimeException {

    private final transient BindingResult bindingResult;

    public ValidationUnprocessableEntityException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }
}
