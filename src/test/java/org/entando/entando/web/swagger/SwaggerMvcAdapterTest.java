package org.entando.entando.web.swagger;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@RunWith(MockitoJUnitRunner.class)
public class SwaggerMvcAdapterTest {

    @Mock
    private ResourceHandlerRegistry resourceHandlerRegistry;
    @Mock
    private ResourceHandlerRegistration resourceHandlerRegistration;
    private SwaggerMvcAdapter swaggerMvcAdapter;

    @Test
    public void testaddResourceHandlers() {

        when(resourceHandlerRegistry.addResourceHandler(anyString())).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations(anyString())).thenReturn(resourceHandlerRegistration);

        swaggerMvcAdapter = new SwaggerMvcAdapter();
        swaggerMvcAdapter.addResourceHandlers(resourceHandlerRegistry);

        verify(resourceHandlerRegistry, times(2)).addResourceHandler(anyString());
        verify(resourceHandlerRegistration, times(2)).addResourceLocations(anyString());
    }
}
