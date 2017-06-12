package net.foxdenstudio.sponge.foxcore.common.modules.walker.constants;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;

public class UTF8StringConstant implements IConstant {
    private final String utf8String;

    public UTF8StringConstant(String utf8String) {
        this.utf8String = utf8String;
    }

    public static int parse(ClassWalker classWalker, int offset, short constantPoolOffset) {
        final short stringLength = classWalker.u2(offset);
        offset += 2;

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stringLength; i++) {
            stringBuilder.append((char) classWalker.classBytes[offset++]);
        }
        classWalker.constantPoolItems[constantPoolOffset] = new UTF8StringConstant(stringBuilder.toString());
        return offset;
    }

    public String getUTF8String() {
        return utf8String;
    }

    @Override
    public String toString() {
        return "UTF8StringConstant{" +
                "utf8String='" + utf8String + '\'' +
                '}';
    }
}
