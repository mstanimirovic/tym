package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class VertexBuffer {
    private int bufferID;
    private int count;

    public VertexBuffer(float[] data) {
        this.count = data.length;
        this.bufferID = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, bufferID);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        MemoryUtil.memFree(buffer);
    }

    public VertexBuffer(float[] data, int usage) {
        this.count = data.length;
        this.bufferID = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, bufferID);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();

        glBufferData(GL_ARRAY_BUFFER, buffer, usage);

        MemoryUtil.memFree(buffer);
    }

    public void updateData(float[] data) {
        bind();
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        MemoryUtil.memFree(buffer);
    }

    public void bind() {
        glBindBuffer(GL_ARRAY_BUFFER, bufferID);
    }

    public void unbind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void dispose() {
        glDeleteBuffers(bufferID);
    }

    public int getBufferID() {
        return bufferID;
    }

    public int getCount() {
        return count;
    }
}