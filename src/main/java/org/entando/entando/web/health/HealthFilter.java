package org.entando.entando.web.health;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class HealthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        if(httpServletRequest.getRequestURI().endsWith("api/health")){
            httpServletResponse.setStatus(200);
        }else{
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }
}
