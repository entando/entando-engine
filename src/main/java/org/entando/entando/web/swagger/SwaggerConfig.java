package org.entando.entando.web.swagger;

import com.agiletec.aps.system.SystemConstants;
import com.fasterxml.classmate.TypeResolver;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.entando.entando.web.user.model.UserAuthoritiesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;
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
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@ComponentScan
@EnableSwagger2WebMvc
public class SwaggerConfig {

    private static final String REFERENCE_NAME = "entando";
    private static final String GROUP_NAME = "entando";

    private TypeResolver typeResolver;

    private String kcClientId;
    private String kcClientSecret;
    private String authServer;

    @Autowired
    private ServletContext servletContext;

    public SwaggerConfig(Environment environment, TypeResolver typeResolver) {

        this.typeResolver = typeResolver;
        this.authServer = environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_AUTH_URL);

        boolean kcEnabled = Boolean.parseBoolean(environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_ENABLED));
        if (kcEnabled) {
            String kcRealm = environment.getProperty(SystemConstants.SYSTEM_PROP_KEYCLOAK_REALM);
            this.authServer = String.format("%s/realms/%s/protocol/openid-connect", this.authServer, kcRealm);
        }
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(GROUP_NAME)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .securitySchemes((getSecuritySchemes()))
                .securityContexts(securityContext())
                .pathProvider(new EntandoPathProvider())
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
                        new TokenRequestEndpoint(this.authServer + "/auth", this.kcClientId, this.kcClientSecret))
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
                .useBasicAuthenticationWithAccessCodeGrant(true)
                .build();
    }

    private class EntandoPathProvider extends DefaultPathProvider {
        @Override
        public String getOperationPath(String operationPath) {
            String contextPath = servletContext.getContextPath();
            if (operationPath.startsWith(contextPath)) {
                operationPath = operationPath.substring(contextPath.length());
            }
            return Paths.removeAdjacentForwardSlashes(UriComponentsBuilder.newInstance().replacePath(operationPath)
                    .build().toString());
        }
    }

}