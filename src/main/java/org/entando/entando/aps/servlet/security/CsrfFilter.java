package org.entando.entando.aps.servlet.security;

import com.agiletec.aps.system.SystemConstants;
import org.entando.entando.aps.system.exception.CSRFProtectionException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CsrfFilter extends OncePerRequestFilter {

    private static final String JOLLY_CHARACTER = "*.";

    private Environment env;

    public CsrfFilter() {
    }

    public CsrfFilter(Environment env) {
        this.env = env;
    }

    public String getEnv(String key) {
        return System.getenv(key) != null ? System.getenv(key) : env.getProperty(key);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isCsfrProtectionActive = SystemConstants.CSRF_BASIC_PROTECTION.equals(getEnv(SystemConstants.ENTANDO_CSRF_PROTECTION));

        String origin = req.getHeader(SystemConstants.ORIGIN);
        String referer = req.getHeader(SystemConstants.REFERER);

        String url = Optional.ofNullable(origin)
                .orElse(Optional.ofNullable(referer).orElse(""));

        if (isCsfrProtectionActive && !"".equals(url) && !isSafeVerbs(req) && req.getHeader(SystemConstants.COOKIE) != null && req.getHeader(SystemConstants.COOKIE).contains(SystemConstants.JSESSIONID)) {
            if (check(url)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }
        filterChain.doFilter(req, response);
    }

    private boolean check(String url) {
        try {
            URI uri = new URI(url);
            url = uri.getScheme().concat("://").concat(uri.getHost());
        } catch (Exception e) {
            throw new CSRFProtectionException("URISyntaxException --> ", e);
        }

        String finalUrl = url;
        return getWhiteList().stream().anyMatch(domain -> domain.equals(finalUrl))
                ||
                getSubDomainFromWildCard().stream().anyMatch(url::endsWith);
    }

    private List<String> getWhiteList() {
        return getDomais(getEnv(SystemConstants.ENTANDO_CSRF_ALLOWED_DOMAINS))
                .stream()
                .filter(rs -> !rs.startsWith(JOLLY_CHARACTER))
                .collect(Collectors.toList());
    }

    private List<String> getSubDomainFromWildCard() {
        return getDomais(getEnv(SystemConstants.ENTANDO_CSRF_ALLOWED_DOMAINS))
                .stream()
                .filter(rs -> rs.startsWith(JOLLY_CHARACTER))
                .map(rs -> rs.replace("*.", "").trim())
                .collect(Collectors.toList());
    }

    private List<String> getDomais(String allowedDomainsString) {
        return Arrays.asList(allowedDomainsString.split(SystemConstants.SEPARATOR_DOMAINS));
    }

    private boolean isSafeVerbs(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod()) || HttpMethod.HEAD.matches(request.getMethod()) || HttpMethod.OPTIONS.matches(request.getMethod());
    }
}
