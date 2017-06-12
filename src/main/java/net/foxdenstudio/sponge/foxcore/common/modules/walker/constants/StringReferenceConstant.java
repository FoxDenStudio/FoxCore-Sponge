package net.foxdenstudio.sponge.foxcore.common.modules.walker.constants;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;

public class StringReferenceConstant implements IConstant {
    private final short utf8StringIndex;

    public StringReferenceConstant(short utf8StringIndex) {

        this.utf8StringIndex = utf8StringIndex;
    }

    public static int parse(ClassWalker classWalker, int offset, short constantPoolOffset) {
        short utf8StringIndex = classWalker.u2(offset);
        offset += 2;
        classWalker.constantPoolItems[constantPoolOffset] = new StringReferenceConstant(utf8StringIndex);
        return offset;
    }

    public short getUTF8StringIndex() {
        return utf8StringIndex;
    }

    @Override
    public String toString() {
        return "StringReferenceConstant{" +
                "utf8StringIndex=" + utf8StringIndex +
                '}';
    }
}
