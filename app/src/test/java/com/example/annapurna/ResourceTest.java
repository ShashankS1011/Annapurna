package com.example.annapurna;

import com.example.annapurna.util.Resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ResourceTest {

    @Test
    public void testLoadingResource() {
        Resource<String> resource = Resource.loading("Preload");
        assertEquals(Resource.Status.LOADING, resource.getStatus());
        assertEquals("Preload", resource.getData());
        assertNull(resource.getMessage());
    }

    @Test
    public void testSuccessResource() {
        Resource<String> resource = Resource.success("Data");
        assertEquals(Resource.Status.SUCCESS, resource.getStatus());
        assertEquals("Data", resource.getData());
        assertNull(resource.getMessage());
    }

    @Test
    public void testErrorResource() {
        Resource<String> resource = Resource.error("Network Error", null);
        assertEquals(Resource.Status.ERROR, resource.getStatus());
        assertNull(resource.getData());
        assertEquals("Network Error", resource.getMessage());
    }
}
