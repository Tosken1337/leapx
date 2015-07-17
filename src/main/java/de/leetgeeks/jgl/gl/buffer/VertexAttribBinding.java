package de.leetgeeks.jgl.gl.buffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 16.07.2015
 * Time: 20:30
 */
public class VertexAttribBinding {
    public final static int POSITION_INDEX = 0;
    public final static int TEX_COORD_INDEX = 1;
    public final static int NORMAL_INDEX = 2;

    public int index;
    public int numComponentsPerVertex;
    public int dataType;
    public boolean normalized;
    public int stride;
    public long offset;

    public VertexAttribBinding(int index, int numComponentsPerVertex) {
        this.index = index;
        this.numComponentsPerVertex = numComponentsPerVertex;
        this.dataType = GL11.GL_FLOAT;
        this.normalized = false;
        this.stride = 0;
        this.offset = 0;
    }

    public VertexAttribBinding set() {
        GL20.glVertexAttribPointer(index, numComponentsPerVertex, dataType, normalized, stride, offset);
        return this;
    }
}
