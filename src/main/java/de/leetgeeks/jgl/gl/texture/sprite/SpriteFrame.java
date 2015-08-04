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
    private final Vector2f texCoordOffsetScale;

    public SpriteFrame(Texture texture, Vector2f texCoordOffsetScale) {
        this.texture = texture;
        this.texCoordOffsetScale = texCoordOffsetScale;
    }

    public Texture getTexture() {
        return texture;
    }

    public Vector2f getTexCoordOffsetScale() {
        return texCoordOffsetScale;
    }
}
