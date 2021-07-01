package org.entando.entando.web.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.entando.entando.web.common.exceptions.FileMaxSizeException;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FileBrowserSizeInterceptor extends HandlerInterceptorAdapter {

    private int fileUploadMaxSize;

    public FileBrowserSizeInterceptor(int maxSize) {
        this.fileUploadMaxSize = maxSize;
    }

    public void setFileUploadMaxSize(int fileUploadMaxSize) {
        this.fileUploadMaxSize = fileUploadMaxSize;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return isValidRequest(request);
    }

    private boolean isValidRequest(HttpServletRequest request) {
        if (isUploadMethod(request) && request.getContentLength() > this.fileUploadMaxSize) {
            throw new FileMaxSizeException("Invalid max content-length");
        }
        return true;
    }

    private boolean isUploadMethod(HttpServletRequest request) {
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        return HttpMethod.POST.equals(method) || HttpMethod.POST.equals(method);
    }
}
