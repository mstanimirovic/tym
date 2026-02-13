package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;

public class VertexArray {
    private int arrayID;

    public VertexArray() {
        this.arrayID = glGenVertexArrays();
    }

    public void addBuffer(VertexBuffer vbo, VertexBufferLayout layout) {
        bind();
        vbo.bind();

        var attributes = layout.getAttributes();
        int offset = 0;

        for (int i = 0; i < attributes.size(); i++) {
            var attr = attributes.get(i);

            glEnableVertexAttribArray(i);

            glVertexAttribPointer(
                    i,                      // index attribute
                    attr.count,             // component number
                    attr.type,              // data type
                    attr.normalized,        // is normalized
                    layout.getStride(),     // stride (space between vertex-a)
                    offset                  // offset
            );

            offset += attr.count * attr.getSizeOfType();
        }
    }

    public void bind() {
        glBindVertexArray(arrayID);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void dispose() {
        glDeleteVertexArrays(arrayID);
    }

    public int getArrayID() {
        return arrayID;
    }
}