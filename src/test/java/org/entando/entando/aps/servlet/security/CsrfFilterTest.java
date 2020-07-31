package org.entando.entando.aps.servlet.security;

import com.agiletec.aps.system.SystemConstants;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CsrfFilterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsrfFilterTest.class);

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private MockEnvironment mockEnvironment;


    @Before
    public void initTest() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        mockEnvironment = new MockEnvironment();
        setEnvironments(mockEnvironment);
    }

    //ORIGIN
    //Domain different from those set in the environment variable ENTANDO_CSRF_ALLOWED_DOMAINS
    @Test
    public void originForbidden() {
        testFilter("http://xxxx.it", null);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    //ORIGIN
    //Domain contained in the environment variable
    @Test
    public void originWithWhiteList() {
        testFilter("http://organization.it", null);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    //ORIGIN
    //Domain contained in the environment variable with wildcard
    @Test
    public void originWithWildCard() {
        testFilter("http://xxx.test.entando.com", null);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    //REFERER
    //Domain different from those set in the environment variable ENTANDO_CSRF_ALLOWED_DOMAINS
    @Test
    public void refererForbidden() {
        testFilter(null, "http://xxxx.it/index.html");
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    //REFERER
    //Domain contained in the environment variable ENTANDO_CSRF_ALLOWED_DOMAINS
    @Test
    public void refererWithWhiteList() {
        testFilter(null, "http://organization.it/test/index.html");
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    //REFERER
    //Domain contained in the environment variable with wildcard
    @Test
    public void refererWithWildCard() {
        testFilter(null, "http://xxx.yyy.zzz.entando.com/test/index.html");
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    // ENTANDO_CSRF_PROTECTION environment variable other than basic
    @Test
    public void csrfProtectionNotEnabled() {
        mockEnvironment.setProperty(SystemConstants.ENTANDO_CSRF_PROTECTION, "basic");
        testFilter("http://test.it", null);
        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }


    private void testFilter(String origin, String referer) {

        CsrfFilter csrfFilter = new CsrfFilter(mockEnvironment);
        MockFilterConfig filterConfig = new MockFilterConfig();
        request.addHeader("Cookie", "JSESSIONID=xxxxxxxxxxxx");
        if (origin != null) {
            request.addHeader("origin", origin);
        }
        if (referer != null) {
            request.addHeader("referer", referer);
        }

        try {
            csrfFilter.init(filterConfig);
            csrfFilter.doFilterInternal(request, response, filterChain);
        } catch (ServletException | IOException e) {
            LOGGER.error("Error test --> ", e);
        } finally {
            csrfFilter.destroy();
        }
    }


    //Set enviroments for test
    private void setEnvironments(MockEnvironment mockEnvironment) {
        mockEnvironment.setProperty(SystemConstants.ENTANDO_CSRF_PROTECTION, "basic");
        mockEnvironment.setProperty(SystemConstants.ENTANDO_CSRF_ALLOWED_DOMAINS, "http://organization.it,https://organization.it,*.entando.com");

    }

}
