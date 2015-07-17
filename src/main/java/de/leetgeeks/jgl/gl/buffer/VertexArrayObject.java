package de.leetgeeks.jgl.gl.buffer;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.GLResource;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.OpenGLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 16.07.2015
 * Time: 19:20
 */
public class VertexArrayObject implements GLResource {
    private int id;

    private List<VertexBufferObject> boundBuffers = new ArrayList<>();

    private VertexArrayObject() {}

    public static VertexArrayObject create() {
        int vertexArrayId = GL30.glGenVertexArrays();
        if (vertexArrayId <= 0) {
            throw new OpenGLException(GL11.glGetError());
        }

        final VertexArrayObject vao = new VertexArrayObject();
        vao.id = vertexArrayId;
        return vao;
    }

    public static VertexArrayObject create(List<VertexBufferObject> vertexBufferObjects) {
        VertexArrayObject vertexArrayObject = create();
        vertexArrayObject.bind();

        vertexBufferObjects.forEach(vbo -> {
            vbo.bind();
            vbo.getAttribBindings().forEach(VertexAttribBinding::set);
            //vbo.unbind();

            vertexArrayObject.boundBuffers.add(vbo);
        });

        vertexArrayObject.enable();
        vertexArrayObject.unbind();

        GLHelper.checkAndThrow();

        return vertexArrayObject;
    }

    public static VertexArrayObject create(VertexBufferObject vertexBufferObject) {
        VertexArrayObject vertexArrayObject = create();
        vertexArrayObject.bind();
        vertexBufferObject.bind();
        vertexBufferObject.getAttribBindings().forEach(VertexAttribBinding::set);
        vertexBufferObject.unbind();
        vertexArrayObject.unbind();

        GLHelper.checkAndThrow();

        vertexArrayObject.boundBuffers.add(vertexBufferObject);

        return vertexArrayObject;
    }

    public VertexArrayObject bind() {
        GL30.glBindVertexArray(id);
        return this;
    }

    public VertexArrayObject unbind() {
        GL30.glBindVertexArray(0);
        return this;
    }

    public VertexArrayObject enable() {
        boundBuffers.stream().flatMap(vertexBufferObject -> vertexBufferObject.getAttribBindings().stream()).forEach(attribBinding -> {
            GL20.glEnableVertexAttribArray(attribBinding.index);
        });
        return this;
    }

    @Override
    public void free() {
        GL30.glDeleteVertexArrays(id);
    }
}
