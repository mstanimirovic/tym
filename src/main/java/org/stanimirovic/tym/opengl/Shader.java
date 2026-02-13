package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    private int programID;
    private Map<String, Integer> uniformLocationCache;

    public Shader(String vertexSource, String fragmentSource) {
        this.uniformLocationCache = new HashMap<>();
        this.programID = createShaderProgram(vertexSource, fragmentSource);
    }

    public static Shader fromFiles(String vertexPath, String fragmentPath) {
        try {
            String vertexSource = new String(Files.readAllBytes(Paths.get(vertexPath)));
            String fragmentSource = new String(Files.readAllBytes(Paths.get(fragmentPath)));
            return new Shader(vertexSource, fragmentSource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader files: " + e.getMessage());
        }
    }

    private int compileShader(int type, String source) {
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shaderID);
            String shaderType = type == GL_VERTEX_SHADER ? "vertex" : "fragment";
            throw new RuntimeException("Failed to compile " + shaderType + " shader: " + error);
        }

        return shaderID;
    }

    private int createShaderProgram(String vertexSource, String fragmentSource) {
        int program = glCreateProgram();
        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        glValidateProgram(program);

        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            String error = glGetProgramInfoLog(program);
            throw new RuntimeException("Failed to link shader program: " + error);
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    private int getUniformLocation(String name) {
        if (uniformLocationCache.containsKey(name)) {
            return uniformLocationCache.get(name);
        }

        int location = glGetUniformLocation(programID, name);
        if (location == -1) {
            System.err.println("Warning: uniform '" + name + "' doesn't exist!");
        }
        uniformLocationCache.put(name, location);
        return location;
    }

    public void setUniform1i(String name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }

    public void setUniform1f(String name, float value) {
        glUniform1f(getUniformLocation(name), value);
    }

    public void setUniform2f(String name, float v0, float v1) {
        glUniform2f(getUniformLocation(name), v0, v1);
    }

    public void setUniform3f(String name, float v0, float v1, float v2) {
        glUniform3f(getUniformLocation(name), v0, v1, v2);
    }

    public void setUniform4f(String name, float v0, float v1, float v2, float v3) {
        glUniform4f(getUniformLocation(name), v0, v1, v2, v3);
    }

    public void setUniformMat4f(String name, float[] matrix) {
        glUniformMatrix4fv(getUniformLocation(name), false, matrix);
    }

    public void dispose() {
        glDeleteProgram(programID);
    }

    public int getProgramID() {
        return programID;
    }
}