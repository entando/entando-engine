package org.entando.entando.aps.system.services.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.entando.entando.aps.system.services.api.cache.ApiServiceCacheWrapper;
import org.entando.entando.aps.system.services.api.model.ApiMethod;
import org.entando.entando.aps.system.services.api.model.ApiResource;
import org.entando.entando.aps.system.services.api.model.ApiService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.entando.entando.ent.exception.EntException;
import org.entando.entando.aps.system.services.api.cache.ApiResourceCacheWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApiCatalogManagerTest {

    @Mock
    private ApiServiceCacheWrapper cacheWrapper;

    @Mock
    private ApiResourceCacheWrapper resourceCacheWrapper;

    @Mock
    private ApiCatalogDAO apiCatalogDAO;

    @InjectMocks
    private ApiCatalogManager apiCatalogManager;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testResources() throws EntException {
        when(resourceCacheWrapper.getMasterResources()).thenReturn(createResources());
        Map<String, ApiResource> resources = this.apiCatalogManager.getResources();
        assertThat(resources.size(), is(16));
    }

    @Test
    void testGetMethod() throws Throwable {
        when(resourceCacheWrapper.getMasterResource("getService")).thenReturn(createResource(null, "getService"));
        ApiMethod method = this.apiCatalogManager.getMethod(ApiMethod.HttpMethod.GET, "getService");
        Assertions.assertNotNull(method);
        Assertions.assertTrue(method.isActive());
    }

    @Test
    void testGetMethods() throws Throwable {
        when(resourceCacheWrapper.getMasterResources()).thenReturn(createResources());
        List<ApiMethod> methods = this.apiCatalogManager.getMethods(ApiMethod.HttpMethod.GET);
        Assertions.assertNotNull(methods);
        Assertions.assertTrue(methods.size() > 0);
    }

    @Test
    void testUpdateMethodStatus() throws Throwable {
        when(resourceCacheWrapper.getMasterResource("getService")).thenReturn(createResource(null, "getService"));
        ApiMethod method = this.apiCatalogManager.getMethod(ApiMethod.HttpMethod.GET, "getService");
        method.setStatus(false);
        this.apiCatalogManager.updateMethodConfig(method);
        method = this.apiCatalogManager.getMethod(ApiMethod.HttpMethod.GET, "getService");
        Assertions.assertFalse(method.isActive());
    }

    @Test
    void testGetServices() throws Throwable {
        Mockito.lenient().when(resourceCacheWrapper.getMasterResources()).thenReturn(createResources());
        Map<String, ApiService> services = this.apiCatalogManager.getServices();
        Assertions.assertNotNull(services);
        Assertions.assertTrue(services.isEmpty());
    }

    private Map<String, ApiResource> createResources() throws EntException {
        ApiResourceLoader loader = new ApiResourceLoader(ApiCatalogManager.DEFAULT_LOCATION_PATTERN);
        Map<String, ApiResource> res = loader.getResources();
        Map<String, ApiResource> resources = new HashMap<>();
        for (Map.Entry<String, ApiResource> entry : res.entrySet()) {
            resources.put(entry.getKey(), entry.getValue());
        }
        return resources;
    }

    private ApiResource createResource(String namespace, String resourceName) throws EntException {
        ApiResourceLoader loader = new ApiResourceLoader(ApiCatalogManager.DEFAULT_LOCATION_PATTERN);
        Map<String, ApiResource> resources = loader.getResources();
        String resourceCode = ApiResource.getCode(namespace, resourceName);
        return resources.get(resourceCode);
    }

}
