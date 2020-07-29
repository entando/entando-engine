package org.entando.entando.aps.servlet.security;

import com.agiletec.aps.system.SystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrfFilter.class);

    private final String WILDCARD = "*.";


    private Environment env;

    public CsrfFilter() {
    }

    public CsrfFilter(Environment env) {
        this.env = env;
    }

    public String getEnv(String key){
        return System.getenv(key) != null ? System.getenv(key) : env.getProperty(key);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isCsfrProtectionActive = "basic".equals(getEnv(SystemConstants.ENTANDO_CSRF_PROTECTION));

        if(isCsfrProtectionActive && req.getHeader("Cookie") != null && req.getHeader("Cookie").contains("JSESSIONID") ) {
            if (check(req)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            }
        }
        filterChain.doFilter(req, response);
    }

    private boolean check(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        // Get url form origin or referer
        String url = Optional.ofNullable(origin)
                .orElse(Optional.ofNullable(referer).orElse(""));

        try {
            URI uri = new URI(url);
            url = uri.getScheme().concat("://").concat(uri.getHost());
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException --> ",e);
            throw new RuntimeException();
        }

        String finalUrl = url;
        return getWhiteList().stream().anyMatch(domain -> domain.equals(finalUrl))
                ||
                getSubDomainFromWildCard().stream().anyMatch(url::endsWith);
    }

    private List<String> getWhiteList() {
        return getDomais(getEnv(SystemConstants.ENTANDO_CSRF_ALLOWED_DOMAINS))
                .stream()
                .filter(rs -> !rs.startsWith(WILDCARD))
                .collect(Collectors.toList());
    }

    private List<String> getSubDomainFromWildCard() {
        return getDomais(getEnv(SystemConstants.ENTANDO_CSRF_ALLOWED_DOMAINS))
                .stream()
                .filter(rs -> rs.startsWith(WILDCARD))
                .map(rs->rs.replace("*.","").trim())
                .collect(Collectors.toList());
    }

    private List<String> getDomais(String allowedDomainsString) {
        return Arrays.asList(allowedDomainsString.split(SystemConstants.SEPARATOR_DOMAINS));
    }
}
