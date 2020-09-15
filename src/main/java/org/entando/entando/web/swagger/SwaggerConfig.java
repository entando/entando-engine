package org.entando.entando.web.swagger;

import static java.util.Optional.ofNullable;

import com.fasterxml.classmate.TypeResolver;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.entando.entando.web.user.model.UserAuthoritiesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import springfox.documentation.builders.AuthorizationCodeGrantBuilder;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
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
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ComponentScan
@EnableSwagger2
public class SwaggerConfig {

    private static final String REFERENCE_NAME = "entando";

    @Autowired
    private TypeResolver typeResolver;

    private final String clientId;
    private final String clientSecret;
    private final String authServer;

    public SwaggerConfig() {

        this.authServer = ofNullable(System.getenv("KEYCLOAK_AUTH_URL"))
                .map(authServer -> authServer.replace("/auth", ""))
                .orElse("http://localhost:8081");

        System.err.println("AUTH SERVER URL: " + this.authServer);

        this.clientId = System.getenv("KEYCLOAK_CLIENT_ID");
        this.clientSecret = System.getenv("KEYCLOAK_CLIENT_SECRET");
    }

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
//                .groupName("entando")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes((getSecuritySchemes()))
                .securityContexts(securityContext())
                .directModelSubstitute(UserAuthoritiesRequest.class, String.class)
                .alternateTypeRules(
                        // This rule is necessary to allow Swagger resolving Map<String, List<String>> types
                        AlternateTypeRules.newRule(
                                typeResolver.resolve(Map.class, String.class, typeResolver.resolve(List.class, String.class)),
                                typeResolver.resolve(Map.class, String.class, WildcardType.class), Ordered.HIGHEST_PRECEDENCE)
                );
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

//    @Bean
//    public UiConfiguration uiConfig() {
//        return UiConfigurationBuilder.builder()
//                .displayRequestDuration(true)
//                .validatorUrl("")
//                .build();
//    }
}