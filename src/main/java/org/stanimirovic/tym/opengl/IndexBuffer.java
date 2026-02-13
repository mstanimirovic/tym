package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;

import java.nio.IntBuffer;
import org.lwjgl.system.MemoryUtil;

public class IndexBuffer {
    private int bufferID;
    private int count;

    public IndexBuffer(int[] indices) {
        this.count = indices.length;
        this.bufferID = glGenBuffers();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferID);

        IntBuffer buffer = MemoryUtil.memAllocInt(indices.length);
        buffer.put(indices).flip();

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        MemoryUtil.memFree(buffer);
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferID);
    }

    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
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