package net.foxdenstudio.sponge.foxcore.common.modules.walker.constants;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;

public class NameAndTypeDescriptorConstant implements IConstant {

    private final int nameIndex;
    private final int typeIndex;

    public NameAndTypeDescriptorConstant(int nameIndex, int typeIndex) {

        this.nameIndex = nameIndex;
        this.typeIndex = typeIndex;
    }

    public static int parse(ClassWalker classWalker, int offset, short constantPoolOffset) {
        int nameIndex = classWalker.u2(offset);
        offset += 2;
        int typeIndex = classWalker.u2(offset);
        offset += 2;

        classWalker.constantPoolItems[constantPoolOffset] = new NameAndTypeDescriptorConstant(nameIndex, typeIndex);

        return offset;
    }

    public int getNameIndex() {
        return nameIndex;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    @Override
    public String toString() {
        return "NameAndTypeDescriptorConstant{" +
                "nameIndex=" + nameIndex +
                ", typeIndex=" + typeIndex +
                '}';
    }
}
