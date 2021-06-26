package org.entando.entando.web.swagger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agiletec.aps.system.SystemConstants;
import com.fasterxml.classmate.TypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;

@ExtendWith(MockitoExtension.class)
class SwaggerConfigTest {

    private final String authUrl =  "http://localhost:9999/auth";
    private final String realm =  "entandoRealm";

    @Mock
    private Environment environment;

    private TypeResolver typeResolver = new TypeResolver();

    @BeforeEach
    public void setup() {
        when(environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_AUTH_URL)).thenReturn(authUrl);
        Mockito.lenient().when(environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_REALM)).thenReturn(realm);
        when(environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_ENABLED)).thenReturn("true");
    }

    @Test
    void createSwaggerConfigWithKeycloakEnabled() {

        new SwaggerConfig(environment, typeResolver);

        verify(environment, times(3)).getProperty(anyString());
    }

    @Test
    void createSwaggerConfigWithKeycloakDisabled() {

        when(environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_ENABLED)).thenReturn("false");

        new SwaggerConfig(environment, typeResolver);

        verify(environment, times(2)).getProperty(anyString());
    }


    @Test
    void getDocketTest() {

        SwaggerConfig swaggerConfig = new SwaggerConfig(environment, typeResolver);
        Docket docket = swaggerConfig.api();

        assertEquals("entando", docket.getGroupName());
        assertEquals(true, docket.isEnabled());
    }


    @Test
    void securityTest() {

        SecurityConfiguration security = new SwaggerConfig(environment, typeResolver).security();

        assertTrue(security.getUseBasicAuthenticationWithAccessCodeGrant());
    }
}
