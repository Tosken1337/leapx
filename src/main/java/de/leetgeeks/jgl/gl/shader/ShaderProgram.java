package de.leetgeeks.jgl.gl.shader;

import de.leetgeeks.jgl.gl.GLHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.joml.Vector4f;
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
    private static final Logger log = LogManager.getLogger();
    private int id;

    private Map<String, Integer> uniformLocations = new HashMap<>();

    private ShaderProgram() {
    }

    public static ShaderProgram buildProgram(final String vertexShaderSource, final String fragmentShaderSource) throws Exception {
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

        GLHelper.checkAndThrow();

        return program;
    }

    public void use() {
        glUseProgram(id);
    }

    public void setUniformMatrixF(final String uniformName, FloatBuffer matrix) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        if (location > -1) {
            glUniformMatrix4fv(location, false, matrix);
        }
    }

    public void setUniformF(final String uniformName, float value) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        if (location > -1) {
            glUniform1f(location, value);
        }
    }

    public void setUniformVector2f(final String uniformName, Vector2f vec) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        if (location > -1) {
            glUniform2f(location, vec.x, vec.y);
        }
    }

    public void setUniformVector4f(final String uniformName, Vector4f vec) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        if (location > -1) {
            glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
        }
    }

    public void setUniformTextureUnit(final String uniformName, int unit) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        if (location > -1) {
            glUniform1i(location, unit);
        }
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

            log.error("Compile failure in {} shader:\n{}", ShaderTypeString, error);
        }

        return shaderId;
    }

    public void setUniformB(String uniformName, boolean value) {
        int location = uniformLocations.computeIfAbsent(uniformName, s -> glGetUniformLocation(id, s));
        if (location > -1) {
            glUniform1i(location, value ? 1 : 0);
        }
    }
}
