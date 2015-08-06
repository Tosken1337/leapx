package de.leetgeeks.jgl.gl.texture.sprite;

import de.leetgeeks.jgl.gl.texture.Texture;
import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.08.2015
 * Time: 10:01
 */
public final class SpriteFrame {
    private final Texture texture;
    private final Vector2f texCoordOffset;
    private final Vector2f texCoordSize;

    public SpriteFrame(Texture texture, Vector2f texCoordOffset, Vector2f texCoordSize) {
        this.texture = texture;
        this.texCoordOffset = texCoordOffset;
        this.texCoordSize = texCoordSize;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f getTexCoordOffset() {
        return texCoordOffset;
    }

    public Vector2f getTexCoordSize() {
        return texCoordSize;
    }
}
