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

import com.flowpowered.math.vector.Vector3i;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;


public class PositionRenderer {

    private Minecraft mc;
    private HighlightList list;

    private byte red = 0;

    public PositionRenderer(Minecraft mc) {
        super();
        this.mc = mc;
        list = new HighlightList(mc);
        /*
        float mx = 5;
        float my = 79;
        float mz = -20;

        list.add(new Highlight(new Vector3i(mx, my, mz++), new Vector3f(1, 0, 0), 0f / 6f));
        list.add(new Highlight(new Vector3i(mx, my, mz++), new Vector3f(1, 1, 0), 1f / 6f));
        list.add(new Highlight(new Vector3i(mx, my, mz++), new Vector3f(0, 1, 0), 2f / 6f));
        list.add(new Highlight(new Vector3i(mx, my, mz++), new Vector3f(0, 1, 1), 3f / 6f));
        list.add(new Highlight(new Vector3i(mx, my, mz++), new Vector3f(0, 0, 1), 4f / 6f));
        list.add(new Highlight(new Vector3i(mx, my, mz++), new Vector3f(1, 0, 1), 5f / 6f));
        */
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {

        //System.out.println("TEST");
        double playerX = mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * event.partialTicks;
        double playerY = mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * event.partialTicks;
        double playerZ = mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * event.partialTicks;
        list.sortZ(playerX, playerY, playerZ);

        glPushMatrix();

        glTranslated(-playerX, -playerY, -playerZ);

        list.render();
        glPopMatrix();



    }

    public void updateList(List<Vector3i> posList) {
        this.list.clear();
        Set<Vector3i> set = new HashSet<>();
        posList.forEach(set::add);
        set.forEach(pos -> this.list.add(new Highlight(pos /*, FCHelper.RGBfromHSV(Math.random() * 360, 1, 1)*/)));
    }

}
