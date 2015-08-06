package de.leetgeeks.jgl.gl.texture.sprite;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.gl.texture.TextureAttributes;
import de.leetgeeks.jgl.gl.texture.TextureCache;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.08.2015
 * Time: 07:53
 */
public class SpriteMap {
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
     * Each single sprite animation mapped to it's single frames
     */
    private Map<String, List<SpriteMapIndex>> spriteMapIndex;



    /**
     *
     * @param mode
     */
    private SpriteMap(SpriteSource mode) {
        this.sourceMode = mode;
    }

    /**
     *
     * @param spritemap
     * @param jsonIndex
     * @return
     */
    public static SpriteMap withJsonSpritemap(final String spritemap, final String jsonIndex) throws Exception {
        final SpriteMap instance = new SpriteMap(SpriteSource.SpriteMap);
        instance.loadSpritemap(spritemap, jsonIndex);
        return instance;
    }

    /**
     *
     * @param spriteTextures
     * @return
     */
    public static SpriteMap withSeparateSprites(final List<String> spriteTextures) throws Exception {
        final SpriteMap instance = new SpriteMap(SpriteSource.SeparateSprites);
        instance.loadSprites(spriteTextures);
        return instance;
    }

    public int getNumberOfFrames(String animationName) {
        if (!spriteMapIndex.containsKey(animationName)) {
            return 0;
        }

        return spriteMapIndex.get(animationName).size();
    }

    public SpriteFrame getFrame(final String animationName, final int animationFrameIndex) {
        switch (sourceMode) {

            case SpriteMap:
                if (!spriteMapIndex.containsKey(animationName)) {
                    throw new IllegalArgumentException("Unknwon sprite animation: " + animationName);
                }

                final List<SpriteMapIndex> animationFrames = spriteMapIndex.get(animationName);
                final SpriteMapIndex spriteFrame = animationFrames.get(animationFrameIndex);
                final float offsetTexCoordX = spriteFrame.offset.x / ((float) spriteMap.getWidth());
                final float offsetTexCoordY = spriteFrame.offset.y / ((float) spriteMap.getHeight());
                final float sizeTexCoordX = spriteFrame.dimension.x / ((float) spriteMap.getWidth());
                final float sizeTexCoordY = spriteFrame.dimension.y / ((float) spriteMap.getHeight());

                return new SpriteFrame(spriteMap, new Vector2f(offsetTexCoordX, 1 - offsetTexCoordY - sizeTexCoordY), new Vector2f(sizeTexCoordX, sizeTexCoordY));
            case SeparateSprites:
                return new SpriteFrame(null, null, null);
        }

        return null;
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

    /**
     * <pre>
     *     {
     *      "frames": {

                         "bullet_2_0.png":
                         {
                         "frame": {"x":210,"y":566,"w":10,"h":26},
                         "rotated": false,
                         "trimmed": false,
                         "spriteSourceSize": {"x":0,"y":0,"w":10,"h":26},
                         "sourceSize": {"w":10,"h":26}
                         },
                         "bullet_2_1.png":
                         {
                         "frame": {"x":198,"y":566,"w":10,"h":26},
                         "rotated": false,
                         "trimmed": false,
                         "spriteSourceSize": {"x":0,"y":0,"w":10,"h":26},
                         "sourceSize": {"w":10,"h":26}
                         }
                    }
            }
     * </pre>
     * @param spritemap
     * @param jsonIndex
     * @throws Exception
     */
    private void loadSpritemap(final String spritemap, final String jsonIndex) throws Exception {
        // Load sprite map texture
        spriteMap = textureCache.get(spritemap, new TextureAttributes(GL11.GL_LINEAR, GL11.GL_CLAMP));

        // Load json index
        String jsonString = ResourceUtil.getResourceFileAsString(jsonIndex, this.getClass());
        final JsonObject frames = Json.parse(jsonString).asObject().get("frames").asObject();

        final Pattern frameNamePattern = Pattern.compile("(\\w+?)_(\\d{4}?).png");

        spriteMapIndex = new HashMap<>();
        frames.forEach(member -> {
            final JsonObject sprite = member.getValue().asObject();
            final JsonObject frame = sprite.get("frame").asObject();
            final int x = frame.getInt("x", 0);
            final int y = frame.getInt("y", 0);
            final int w = frame.getInt("w", 0);
            final int h = frame.getInt("h", 0);

            final String frameName = member.getName();

            // Check name represents an animation sprite like ..._0001.png
            final Matcher matcher = frameNamePattern.matcher(frameName);
            if (matcher.find()) {
                final String animationGroupName = matcher.group(1);
                final int animationFrameIndex = Integer.parseInt(matcher.group(2));

                final SpriteMapIndex index = new SpriteMapIndex(frameName, new Vector2f(w, h), new Vector2f(x, y), animationFrameIndex);
                if (spriteMapIndex.containsKey(animationGroupName)) {
                    spriteMapIndex.get(animationGroupName).add(index);
                } else {
                    final List<SpriteMapIndex> list = new ArrayList<>();
                    list.add(index);
                    spriteMapIndex.put(animationGroupName, list);
                }
            }
        });

        // Sort frames of each animation
        spriteMapIndex.entrySet().forEach(entry -> {
            final String animationName = entry.getKey();
            final List<SpriteMapIndex> animationFrames = entry.getValue();
            animationFrames.sort(Comparator.comparingInt(SpriteMapIndex::getAnimationIndex));
        });
    }

    /**
     * Inner class which represents one sprite frame in the spritemap.
     * Contains the sprite name and its size and offset within the map.
     */
    private static class SpriteMapIndex {
        String name;
        Vector2f dimension;
        Vector2f offset;
        int animationIndex;

        public SpriteMapIndex(String name, Vector2f dimension, Vector2f offset, int animationIndex) {
            this.name = name;
            this.dimension = dimension;
            this.offset = offset;
            this.animationIndex = animationIndex;
        }

        public int getAnimationIndex() {
            return animationIndex;
        }
    }

}
