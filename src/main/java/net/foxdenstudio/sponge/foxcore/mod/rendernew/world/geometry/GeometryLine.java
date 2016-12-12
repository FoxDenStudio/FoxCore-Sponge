package net.foxdenstudio.sponge.foxcore.mod.rendernew.world.geometry;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Fox on 12/4/2016.
 */
public class GeometryLine extends Geometry {

    Vector3d start, end;
    Vector3d localStart, localEnd;



    public GeometryLine(Vector3d start, Vector3d end, Vector3f color, float opacity) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.opacity = opacity;

        this.position = start.add(end).div(2);
        this.localStart = start.sub(position);
        this.localEnd = end.sub(position);
    }

    @Override
    public void render() {
        glPushAttrib(GL_CURRENT_BIT | GL_LINE_BIT | GL_HINT_BIT);
        glColor4f(color.getX(), color.getY(), color.getZ(), opacity);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        glBegin(GL_LINES);

        glVertex3d(localStart.getX(), localStart.getY(), localStart.getZ());
        glVertex3d(localEnd.getX(), localEnd.getY(), localEnd.getZ());

        glEnd();
        glPopAttrib();
    }
}
