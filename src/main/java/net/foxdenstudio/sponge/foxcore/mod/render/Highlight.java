/*
 * This file is part of FoxCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) gravityfox - https://gravityfox.net/
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.foxdenstudio.sponge.foxcore.mod.render;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;

import static org.lwjgl.opengl.GL11.*;

public class Highlight implements IRenderable {

    private static final float OFFSET = 0.001f;
    private static final int PERIOD = 2000;

    boolean[][][] filled = new boolean[3][3][3];

    Vector3i pos;
    Vector3f color;
    float phase;

    public Highlight(Vector3i pos) {
        this(pos, new Vector3f(1, 1, 1));
    }

    public Highlight(Vector3i pos, float phase) {
        this(pos, new Vector3f(1, 1, 1), phase);
    }

    public Highlight(Vector3i pos, Vector3f color) {
        this(pos, color, 0f);
    }

    public Highlight(Vector3i pos, Vector3f color, float phase) {
        this.pos = pos;
        this.color = color;
        this.phase = phase;
    }

    public void render() {
        final float alpha = (1f - ((System.currentTimeMillis()) % PERIOD) / (float) PERIOD + phase) % 1f;
        glColor4f(color.getX(), color.getY(), color.getZ(), alpha);
        drawBoxLines();
        glColor4f(color.getX(), color.getY(), color.getZ(), alpha / 4 + 0.1f);
        drawBoxFaces();
    }

    public void drawBoxFaces() {

        final float x1 = pos.getX() - OFFSET;
        final float y1 = pos.getY() - OFFSET;
        final float z1 = pos.getZ() - OFFSET;
        final float x2 = pos.getX() + OFFSET + 1;
        final float y2 = pos.getY() + OFFSET + 1;
        final float z2 = pos.getZ() + OFFSET + 1;

        glBegin(GL_QUADS);
        if (!filled[0][1][1]) {
            glVertex3f(x1, y1, z1);
            glVertex3f(x1, y1, z2);
            glVertex3f(x1, y2, z2);
            glVertex3f(x1, y2, z1);
        }

        if (!filled[1][0][1]) {
            glVertex3f(x1, y1, z1);
            glVertex3f(x2, y1, z1);
            glVertex3f(x2, y1, z2);
            glVertex3f(x1, y1, z2);
        }

        if (!filled[1][1][0]) {
            glVertex3f(x1, y1, z1);
            glVertex3f(x1, y2, z1);
            glVertex3f(x2, y2, z1);
            glVertex3f(x2, y1, z1);
        }

        if (!filled[2][1][1]) {
            glVertex3f(x2, y1, z1);
            glVertex3f(x2, y2, z1);
            glVertex3f(x2, y2, z2);
            glVertex3f(x2, y1, z2);
        }

        if (!filled[1][2][1]) {
            glVertex3f(x1, y2, z1);
            glVertex3f(x1, y2, z2);
            glVertex3f(x2, y2, z2);
            glVertex3f(x2, y2, z1);
        }

        if (!filled[1][1][2]) {
            glVertex3f(x1, y1, z2);
            glVertex3f(x2, y1, z2);
            glVertex3f(x2, y2, z2);
            glVertex3f(x1, y2, z2);
        }

        glEnd();
    }

    public void drawBoxLines() {
        final float x1 = pos.getX() - OFFSET;
        final float y1 = pos.getY() - OFFSET;
        final float z1 = pos.getZ() - OFFSET;
        final float x2 = pos.getX() + OFFSET + 1;
        final float y2 = pos.getY() + OFFSET + 1;
        final float z2 = pos.getZ() + OFFSET + 1;

        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glBegin(GL_LINES);

        glVertex3f(x1, y1, z1);
        glVertex3f(x2, y1, z1);
        glVertex3f(x1, y1, z1);
        glVertex3f(x1, y2, z1);
        glVertex3f(x1, y1, z1);
        glVertex3f(x1, y1, z2);

        glVertex3f(x2, y1, z1);
        glVertex3f(x2, y2, z1);
        glVertex3f(x2, y1, z1);
        glVertex3f(x2, y1, z2);

        glVertex3f(x1, y2, z1);
        glVertex3f(x2, y2, z1);
        glVertex3f(x1, y2, z1);
        glVertex3f(x1, y2, z2);

        glVertex3f(x1, y1, z2);
        glVertex3f(x2, y1, z2);
        glVertex3f(x1, y1, z2);
        glVertex3f(x1, y2, z2);

        glVertex3f(x2, y2, z1);
        glVertex3f(x2, y2, z2);
        glVertex3f(x2, y1, z2);
        glVertex3f(x2, y2, z2);
        glVertex3f(x1, y2, z2);
        glVertex3f(x2, y2, z2);

        glEnd();
    }

    public Vector3i getPos() {
        return pos;
    }

    public void setFilled(boolean[][][] filled) {
        this.filled = filled;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }
}
