package de.leetgeeks.jgl.gl.texture.sprite;

import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.gl.texture.TextureAttributes;
import de.leetgeeks.jgl.gl.texture.TextureCache;
import de.leetgeeks.jgl.util.GameDuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.08.2015
 * Time: 07:53
 */
public class SpriteAnimation {
    private final static Logger log = LogManager.getLogger();

    private enum SpriteSource {
        SpriteMap,
        SeparateSprites
    }

    /**
     *
     */
    private static final TextureCache textureCache = new TextureCache();

    /**
     *
     */
    private SpriteSource sourceMode;

    /**
     *
     */
    private List<Texture> sprites;

    /**
     *
     */
    private Texture spriteMap;

    /**
     * Each single sprite frame within the map indexed by it's name
     */
    private Map<String, SpriteMapIndex> spriteMapIndex;

    /**
     *
     */
    private boolean isRunning;

    /**
     *
     */
    private GameDuration startTime;



    /**
     *
     * @param mode
     */
    private SpriteAnimation(SpriteSource mode) {
        this.sourceMode = mode;
    }

    /**
     *
     * @param spritemap
     * @param jsonIndex
     * @return
     */
    public static SpriteAnimation withJsonSpritemap(final File spritemap, final File jsonIndex) throws Exception {
        final SpriteAnimation instance = new SpriteAnimation(SpriteSource.SpriteMap);
        instance.loadSpritemap(spritemap, jsonIndex);
        return instance;
    }

    /**
     *
     * @param spriteTextures
     * @return
     */
    public static SpriteAnimation withSeparateSprites(final List<String> spriteTextures) throws Exception {
        final SpriteAnimation instance = new SpriteAnimation(SpriteSource.SeparateSprites);
        instance.loadSprites(spriteTextures);
        return instance;
    }

    public SpriteFrame getFrame(final GameDuration startTime) {
        // @todo compute texture and coordinates based on mode and current time
        return new SpriteFrame(null, null);
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void loadSprites(final List<String> spriteTextures) {
        this.sprites = new ArrayList<>();
        spriteTextures.stream()
                .forEachOrdered(textureFile -> {
                    try {
                        final Texture texture = textureCache.get(textureFile, new TextureAttributes(GL11.GL_LINEAR, GL11.GL_CLAMP));
                        sprites.add(texture);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void loadSpritemap(final File spritemap, final File jsonIndex) {

    }

    /**
     * Inner class which represents one sprite frame in the spritemap.
     * Contains the sprite name and its size and offset within the map.
     */
    private static class SpriteMapIndex {
        String name;
        Vector2f dimension;
        Vector2f offset;

        public SpriteMapIndex(String name, Vector2f dimension, Vector2f offset) {
            this.name = name;
            this.dimension = dimension;
            this.offset = offset;
        }
    }

}
