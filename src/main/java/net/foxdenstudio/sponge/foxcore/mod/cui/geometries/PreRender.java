package net.foxdenstudio.sponge.foxcore.mod.cui.geometries;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;

public abstract class PreRender {

    private float colorR, colorG, colorB, colorA;

    public abstract void accept(Tessellator tessellator, VertexBuffer vertexBuffer, float partialTicks);

    public float getColorR() {
        return this.colorR;
    }

    public float getColorG() {
        return this.colorG;
    }

    public float getColorB() {
        return this.colorB;
    }

    public float getColorA() {
        return this.colorA;
    }

    public PreRender setColorR(float colorR) {
        this.colorR = colorR;
        return this;
    }

    public PreRender setColorG(float colorG) {
        this.colorG = colorG;
        return this;
    }

    public PreRender setColorB(float colorB) {
        this.colorB = colorB;
        return this;
    }

    public PreRender setColorA(float colorA) {
        this.colorA = colorA;
        return this;
    }
}