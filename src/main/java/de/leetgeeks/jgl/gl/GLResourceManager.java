package de.leetgeeks.jgl.gl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 16.07.2015
 * Time: 20:00
 */
public final class GLResourceManager {
    private static final Logger log = LogManager.getLogger();

    private Map<String, GLResource> storage = new HashMap<>();


    private GLResourceManager() {
    }

    public void add(final String name, GLResource resource) {
        if (storage.containsKey(name)) {
            log.error("Resource with given name already present");
            return;
        }

        storage.put(name, resource);
    }

    public void freeResources() {
        storage.values().stream()
                .forEach(GLResource::free);
        storage.clear();
    }
}
