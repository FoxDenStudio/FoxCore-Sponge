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

package net.foxdenstudio.foxcore.mod.renderer;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;


public class TestRenderer {

    private static final float OFFSET = 0.002f;

    private Minecraft mc;

    private byte red = 0;

    public TestRenderer(Minecraft mc) {
        super();
        this.mc = mc;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        //System.out.println("TEST");
        double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
        double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
        double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;

        glPushMatrix();
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        //glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glLineWidth(1f);
        glTranslated(-playerX, -playerY, -playerZ);
        glColor4ub((byte) 255, (byte) 0, (byte) 0, (byte)(-System.currentTimeMillis() / 4));

        float mx = 0;
        float my = 80;
        float mz = 0;
        drawBox(mx, my, mz);

        drawBox(mx, my-1, mz);

        glDisable(GL_BLEND);
        glEnable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        //glEnable(GL_DEPTH_TEST);
        glPopMatrix();
    }

    public static void drawBox(float x, float y, float z) {
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glBegin(GL_LINES);
        float x1 = x - OFFSET;
        float y1 = y - OFFSET;
        float z1 = z - OFFSET;
        float x2 = x + OFFSET + 1;
        float y2 = y + OFFSET + 1;
        float z2 = z + OFFSET + 1;
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

}
