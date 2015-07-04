package de.leetgeeks.jgl.util;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 20:28
 */
public class ResourceUtil {
    private ResourceUtil() {}

    public static String getResourceFileAsString(String resource, Class c) throws Exception {
        File f = new File(c.getResource(resource).toURI());
        return Files.readAllLines(f.toPath()).stream()
                .collect(Collectors.joining("\n"));
    }
}
