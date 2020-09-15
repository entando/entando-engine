package org.entando.entando.web.swagger;

import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan
@EnableSwagger2
public class SwaggerConfig {

    private static final String REFERENCE_NAME = "entando";
    private static final String swaggerUi = "swagger_ui";

    private final String clientId;
    private final String clientSecret;
    private final String authServer;

    public SwaggerConfig() {

        this.authServer = "http://keb-kc-keb.apps.rd.entando.org/auth/realms/entando/protocol/openid-connect";
        this.clientId = swaggerUi;
        this.clientSecret = swaggerUi;
    }

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("entando")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes((getSecuritySchemes()))
                .securityContexts(securityContext());
    }

    private List<SecurityScheme> getSecuritySchemes() {

        SecurityScheme oAuth = new OAuthBuilder()
                .name(REFERENCE_NAME)
                .grantTypes(getGrantTypes())
                .build();

        return Collections.singletonList(oAuth);
    }

    private List<GrantType> getGrantTypes() {

        GrantType grantType = new AuthorizationCodeGrantBuilder()
                .tokenEndpoint(new TokenEndpoint(this.authServer + "/token", "oauthtoken"))
                .tokenRequestEndpoint(
                        new TokenRequestEndpoint(this.authServer + "/auth", this.clientId, this.clientSecret))
                .build();

        return Collections.singletonList(grantType);
    }

    private List<SecurityContext> securityContext() {

        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(getDefaultAuths())
                .forPaths(PathSelectors.regex("/.*"))
                .build();

        return Collections.singletonList(securityContext);
    }

    private List<SecurityReference> getDefaultAuths() {

        SecurityReference securityReference = SecurityReference.builder()
                .reference(REFERENCE_NAME)
                .scopes(new AuthorizationScope[0])
                .build();

        return Collections.singletonList(securityReference);
    }


    @Bean
    public SecurityConfiguration security() {

        return SecurityConfigurationBuilder.builder()
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();
    }
}