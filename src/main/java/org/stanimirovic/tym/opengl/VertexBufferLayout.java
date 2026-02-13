package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;

import java.util.ArrayList;
import java.util.List;

public class VertexBufferLayout {

    public static class VertexAttribute {
        public int type;          // GL_FLOAT, GL_INT, etc.
        public int count;         // component number
        public boolean normalized;

        public VertexAttribute(int type, int count, boolean normalized) {
            this.type = type;
            this.count = count;
            this.normalized = normalized;
        }

        public int getSizeOfType() {
            return switch (type) {
                case GL_FLOAT, GL_INT, GL_UNSIGNED_INT -> 4;
                case GL_UNSIGNED_BYTE, GL_BYTE -> 1;
                default -> throw new RuntimeException("Unknown type: " + type);
            };
        }
    }

    private List<VertexAttribute> attributes;
    private int stride;

    public VertexBufferLayout() {
        this.attributes = new ArrayList<>();
        this.stride = 0;
    }

    public VertexBufferLayout pushFloat(int count) {
        attributes.add(new VertexAttribute(GL_FLOAT, count, false));
        stride += count * 4; // float = 4 bajta
        return this;
    }

    public VertexBufferLayout pushUInt(int count) {
        attributes.add(new VertexAttribute(GL_UNSIGNED_INT, count, false));
        stride += count * 4;
        return this;
    }

    public VertexBufferLayout pushUByte(int count, boolean normalized) {
        attributes.add(new VertexAttribute(GL_UNSIGNED_BYTE, count, normalized));
        stride += count;
        return this;
    }

    public VertexBufferLayout push(int type, int count, boolean normalized) {
        VertexAttribute attr = new VertexAttribute(type, count, normalized);
        attributes.add(attr);
        stride += count * attr.getSizeOfType();
        return this;
    }

    public List<VertexAttribute> getAttributes() {
        return attributes;
    }

    public int getStride() {
        return stride;
    }
}