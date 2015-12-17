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

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Collections;

import static org.lwjgl.opengl.GL11.*;

public class HighlightList extends ArrayList<Highlight> implements IRenderable {

    Minecraft mc;

    public HighlightList(Minecraft mc) {
        this.mc = mc;
    }

    @Override
    public void render() {
        glDisable(GL_LIGHTING);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDepthMask(false);
        //glDisable(GL_DEPTH_TEST);

        glLineWidth(2f);

        this.forEach(Highlight::render);
        glColor4f(1, 1, 1, 1);

        //glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LIGHTING);

    }

    public void sortZ(double x, double y, double z) {
        Collections.sort(this, (o1, o2) ->
                o1.getPos().toDouble().add(0.5, 0.5, 0.5).distanceSquared(x, y, z)
                        < o2.getPos().toDouble().add(0.5, 0.5, 0.5).distanceSquared(x, y, z)
                        ? 1 : -1);
    }
}
