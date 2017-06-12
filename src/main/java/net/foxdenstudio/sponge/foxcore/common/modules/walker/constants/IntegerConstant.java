package net.foxdenstudio.sponge.foxcore.common.modules.walker.constants;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;

public class IntegerConstant implements IConstant {

    private final int data;

    public IntegerConstant(int data) {

        this.data = data;
    }

    public static int parse(ClassWalker classWalker, int offset, short constantPoolOffset) {
        int data = classWalker.u4(offset);
        offset += 4;
        classWalker.constantPoolItems[constantPoolOffset] = new IntegerConstant(data);
        return offset;
    }

    public int getData() {
        return data;
    }

    @Override
    public String toString() {
        return "IntegerConstant{" +
                "data=" + data +
                '}';
    }
}
