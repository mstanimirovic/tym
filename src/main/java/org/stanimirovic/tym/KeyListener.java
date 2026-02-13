package org.stanimirovic.tym;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {

    private static KeyListener instance;
    private final boolean[] keyPressed = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] keyReleased = new boolean[GLFW_KEY_LAST + 1];

    private KeyListener() {}

    public static KeyListener get() {
        if (instance == null) {
            instance = new KeyListener();
        }
        return instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {

        if (key < 0 || key > GLFW_KEY_LAST) return;

        if (action == GLFW_PRESS || action == GLFW_REPEAT) {
            get().keyPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
            get().keyReleased[key] = true;
        }
    }

    public static boolean isKeyPressed(int key) {
        if (key < 0 || key > GLFW_KEY_LAST) return false;
        return get().keyPressed[key];
    }

    public static boolean isKeyReleased(int key) {
        if (key < 0 || key > GLFW_KEY_LAST) return false;
        return get().keyReleased[key];
    }

    public static void endFrame() {
        Arrays.fill(get().keyReleased, false);
    }
}
