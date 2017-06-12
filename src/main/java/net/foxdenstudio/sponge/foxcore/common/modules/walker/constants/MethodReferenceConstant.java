package net.foxdenstudio.sponge.foxcore.common.modules.walker.constants;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;

public class MethodReferenceConstant implements IConstant {
    private final short classRefIndex;
    private final short nameAndTypeIndex;

    public MethodReferenceConstant(short classRefIndex, short nameAndTypeIndex) {

        this.classRefIndex = classRefIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public static int parse(ClassWalker classWalker, int offset, short constantPoolOffset) {
        final short classRefIndex = classWalker.u2(offset);
        offset += 2;
        final short nameAndTypeIndex = classWalker.u2(offset);
        offset += 2;
        classWalker.constantPoolItems[constantPoolOffset] = new MethodReferenceConstant(classRefIndex, nameAndTypeIndex);
        return offset;
    }

    public short getClassRefIndex() {
        return classRefIndex;
    }

    public short getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }

    @Override
    public String toString() {
        return "MethodReferenceConstant{" +
                "classRefIndex=" + classRefIndex +
                ", nameAndTypeIndex=" + nameAndTypeIndex +
                '}';
    }
}
