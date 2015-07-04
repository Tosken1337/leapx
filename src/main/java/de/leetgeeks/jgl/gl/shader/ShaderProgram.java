package de.leetgeeks.jgl.gl.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 20:03
 */
public final class ShaderProgram {
    private int id;

    private Map<String, Integer> uniformLocations = new HashMap<>();

    private ShaderProgram() {
    }

    public static ShaderProgram buildProgramm(final String vertexShaderSource, final String fragmentShaderSource) throws Exception {
        final ShaderProgram program = new ShaderProgram();
        program.id = glCreateProgram();
        if (program.id == 0) {
            throw new Exception("Unable to create program");
        }

        int vertShaderId = createShader(GL_VERTEX_SHADER, vertexShaderSource);
        int fragShaderId = createShader(GL_FRAGMENT_SHADER, fragmentShaderSource);

        glAttachShader(program.id, vertShaderId);
        glAttachShader(program.id, fragShaderId);

        glLinkProgram(program.id);

        int status = GL20.glGetShaderi(program.id, GL20.GL_LINK_STATUS);
        if (status == GL11.GL_FALSE){
            String error=GL20.glGetProgramInfoLog(program.id);
            System.err.println( "Linker failure: %s\n"+error);
        }

        return program;
    }

    public void use() {
        glUseProgram(id);
    }

    public void setUniformMatrixF(final String uniformName, FloatBuffer matrix) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        glUniformMatrix4fv(location, false, matrix);
    }

    private static int createShader(int shaderType, String shaderSource) {
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        int status = glGetShaderi(shaderId, GL_COMPILE_STATUS);
        if (status == GL11.GL_FALSE){

            String error= glGetShaderInfoLog(shaderId);

            String ShaderTypeString = null;
            switch(shaderType){
                case GL20.GL_VERTEX_SHADER: ShaderTypeString = "vertex"; break;
                case GL32.GL_GEOMETRY_SHADER: ShaderTypeString = "geometry"; break;
                case GL20.GL_FRAGMENT_SHADER: ShaderTypeString = "fragment"; break;
            }

            System.err.println( "Compile failure in %s shader:\n%s\n"+ShaderTypeString+error);
        }

        return shaderId;
    }
}
