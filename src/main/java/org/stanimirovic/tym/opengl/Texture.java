package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.stb.STBImage.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

public class Texture {
    private int textureID;
    private int width;
    private int height;
    private int channels;

    public Texture(String filepath) {
        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);

            ByteBuffer imageBuffer = stbi_load(filepath, w, h, channels, 0);
            if (imageBuffer == null) {
                throw new RuntimeException("Failed to load texture: " + filepath +
                        " - " + stbi_failure_reason());
            }

            this.width = w.get(0);
            this.height = h.get(0);
            this.channels = channels.get(0);

            int format = GL_RGB;
            if (this.channels == 4) {
                format = GL_RGBA;
            } else if (this.channels == 1) {
                format = GL_RED;
            }

            glTexImage2D(GL_TEXTURE_2D, 0, format, this.width, this.height,
                    0, format, GL_UNSIGNED_BYTE, imageBuffer);
            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(imageBuffer);
        }

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public Texture(int width, int height, int format) {
        this.textureID = glGenTextures();
        this.width = width;
        this.height = height;

        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0,
                format, GL_UNSIGNED_BYTE, (ByteBuffer) null);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind(int slot) {
        glActiveTexture(GL_TEXTURE0 + slot);
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void bind() {
        bind(0);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void dispose() {
        glDeleteTextures(textureID);
    }

    public int getTextureID() {
        return textureID;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }
}