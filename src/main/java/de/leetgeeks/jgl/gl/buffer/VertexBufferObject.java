package de.leetgeeks.jgl.gl.buffer;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.GLResource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.OpenGLException;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 16.07.2015
 * Time: 19:19
 */
public class VertexBufferObject implements GLResource {
    private int id;

    private Map<Integer, VertexAttribBinding> attribBindings = new HashMap<>();

    private VertexBufferObject() {
    }

    public static VertexBufferObject create() {
        int bufferId = GL15.glGenBuffers();
        if (bufferId <= 0) {
            throw new OpenGLException(GL11.glGetError());
        }
        GLHelper.checkAndThrow();

        final VertexBufferObject vbo = new VertexBufferObject();
        vbo.id = bufferId;
        return vbo;
    }

    public static VertexBufferObject create(final float[] data) {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return create(buffer, GL15.GL_STATIC_DRAW);
    }

    public static VertexBufferObject create(final FloatBuffer data) {
        return create(data, GL15.GL_STATIC_DRAW);
    }

    public static VertexBufferObject create(final FloatBuffer data, int usage) {
        final VertexBufferObject vbo = create();
        vbo.setVertexData(data, usage);
        return vbo;
    }

    public void setVertexData(final FloatBuffer data, int usage) {
        bind();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
        unbind();
        GLHelper.checkAndThrow();
    }

    public void bind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
    }

    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    public VertexBufferObject addAttribBinding(final VertexAttribBinding attribBinding) {
        if (attribBindings.containsKey(attribBinding.index)) {
            throw new Error("Vertex attribute binding for attribute at position " + attribBinding.index + " already defined");
        }

        attribBindings.put(attribBinding.index, attribBinding);
        return this;
    }

    public List<VertexAttribBinding> getAttribBindings() {
        return attribBindings.values().stream().collect(Collectors.toList());
    }

    @Override
    public void free() {
        GL15.glDeleteBuffers(id);
    }
}
