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
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;


public class RenderHandler {

    private Minecraft mc;
    private HighlightList list;

    private byte red = 0;

    public RenderHandler(Minecraft mc) {
        super();
        this.mc = mc;
        list = new HighlightList(mc);
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

    public void updateList(List<Vector3i> posList, List<Vector3f> colorList) {
        this.list.clear();
        if (posList == null || colorList == null || posList.size() == 0) return;

        Map<Vector3i, ColorBlender> posColorMap = new HashMap<>();
        Iterator<Vector3i> posIt = posList.iterator();
        Iterator<Vector3f> colorIt = colorList.iterator();

        while (posIt.hasNext()) {
            Vector3i pos = posIt.next();
            Vector3f color = colorIt.next();
            if (posColorMap.containsKey(pos)) {
                posColorMap.get(pos).blend(color);
            } else {
                posColorMap.put(pos, new ColorBlender(color));
            }
        }

        posColorMap.entrySet().forEach(entry -> this.list.add(new Highlight(entry.getKey(), entry.getValue().color)));
    }

    private class ColorBlender {
        Vector3f color;
        int weight;

        public ColorBlender(Vector3f color) {
            this.color = color;
            weight = 1;
        }

        public void blend(Vector3f newColor) {
            final float a = ((float) weight) / (weight + 1),
                    b = 1.0f / (weight + 1);
            color = new Vector3f(
                    a * color.getX() + b * newColor.getX(),
                    a * color.getY() + b * newColor.getY(),
                    a * color.getZ() + b * newColor.getZ()
            );
            weight++;
        }


    }

}
