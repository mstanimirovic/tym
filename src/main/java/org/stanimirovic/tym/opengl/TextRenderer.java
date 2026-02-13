package org.stanimirovic.tym.opengl;

import static org.lwjgl.opengl.GL46.*;

import java.util.HashMap;
import java.util.Map;

public class TextRenderer {

    private static final Map<Character, float[][]> CHAR_SEGMENTS = new HashMap<>();

    static {
        CHAR_SEGMENTS.put('0', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.1f, 0.1f, 0.1f, 0.9f},  // left
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('1', new float[][] {
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('2', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.9f, 0.5f, 0.9f, 0.9f},  // top right
                {0.1f, 0.1f, 0.1f, 0.5f},  // bottom left
        });

        CHAR_SEGMENTS.put('3', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('4', new float[][] {
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.5f, 0.1f, 0.9f},  // top left
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('5', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.1f, 0.5f, 0.1f, 0.9f},  // top left
                {0.9f, 0.1f, 0.9f, 0.5f},  // bottom right
        });

        CHAR_SEGMENTS.put('6', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.1f, 0.1f, 0.1f, 0.9f},  // left
                {0.9f, 0.1f, 0.9f, 0.5f},  // bottom right
        });

        CHAR_SEGMENTS.put('7', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('8', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.1f, 0.1f, 0.1f, 0.9f},  // left
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('9', new float[][] {
                {0.1f, 0.9f, 0.9f, 0.9f},  // top
                {0.1f, 0.5f, 0.9f, 0.5f},  // middle
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
                {0.1f, 0.5f, 0.1f, 0.9f},  // top left
                {0.9f, 0.1f, 0.9f, 0.9f},  // right
        });

        CHAR_SEGMENTS.put('.', new float[][] {
                {0.4f, 0.1f, 0.6f, 0.1f},  // dot
        });

        CHAR_SEGMENTS.put('%', new float[][] {
                {0.1f, 0.7f, 0.3f, 0.9f},  // top left circle (approx)
                {0.7f, 0.1f, 0.9f, 0.3f},  // bottom right circle
                {0.2f, 0.1f, 0.8f, 0.9f},  // diagonal line
        });

        CHAR_SEGMENTS.put('L', new float[][] {
                {0.1f, 0.1f, 0.1f, 0.9f},  // left
                {0.1f, 0.1f, 0.9f, 0.1f},  // bottom
        });
    }

    private float charWidth;
    private float charHeight;
    private float charSpacing;

    public TextRenderer(float charWidth, float charHeight) {
        this.charWidth = charWidth;
        this.charHeight = charHeight;
        this.charSpacing = charWidth * 0.3f;
    }

    /**
     * Renderuje tekst na određenoj poziciji
     */
    public void render(String text, float x, float y, Shader shader) {
        float currentX = x;

        glLineWidth(3.0f);  // Debljina linija za tekst

        for (char c : text.toCharArray()) {
            if (c == ' ') {
                currentX += charWidth + charSpacing;
                continue;
            }

            float[][] segments = CHAR_SEGMENTS.get(c);
            if (segments != null) {
                renderChar(segments, currentX, y, shader);
            }

            currentX += charWidth + charSpacing;
        }

        glLineWidth(1.0f);  // Vrati default
    }

    /**
     * Renderuje pojedinačni karakter
     */
    private void renderChar(float[][] segments, float x, float y, Shader shader) {
        for (float[] segment : segments) {
            // Transformiši segment koordinate u world space
            float x1 = x + segment[0] * charWidth;
            float y1 = y + segment[1] * charHeight;
            float x2 = x + segment[2] * charWidth;
            float y2 = y + segment[3] * charHeight;

            // Kreiraj vertices za liniju
            float[] vertices = {x1, y1, x2, y2};

            VertexBuffer vbo = new VertexBuffer(vertices, GL_STATIC_DRAW);
            VertexBufferLayout layout = new VertexBufferLayout();
            layout.pushFloat(2);

            VertexArray vao = new VertexArray();
            vao.addBuffer(vbo, layout);

            vao.bind();
            glDrawArrays(GL_LINES, 0, 2);

            // Cleanup (u produkciji bi ovo bilo cached)
            vao.dispose();
            vbo.dispose();
        }
    }

    /**
     * Računa širinu teksta
     */
    public float getTextWidth(String text) {
        int visibleChars = 0;
        for (char c : text.toCharArray()) {
            if (c != ' ') visibleChars++;
        }
        return text.length() * (charWidth + charSpacing) - charSpacing;
    }

    public void setCharSize(float width, float height) {
        this.charWidth = width;
        this.charHeight = height;
        this.charSpacing = width * 0.3f;
    }
}