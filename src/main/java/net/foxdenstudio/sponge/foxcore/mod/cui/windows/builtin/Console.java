package net.foxdenstudio.sponge.foxcore.mod.cui.windows.builtin;

import net.foxdenstudio.sponge.foxcore.mod.cui.windows.Window;

public class Console extends Window {
    public Console() {
        super("Console");
        this.setPositionX(200);
        this.setPositionY(50);
        this.setWidth(360);
        this.setHeight(240);
        this.setVisible(true);

        this.addComponent(new LogTextArea().setWidth(240).setHeight(180).setPositionX(50).setPositionY(20));
    }
}
