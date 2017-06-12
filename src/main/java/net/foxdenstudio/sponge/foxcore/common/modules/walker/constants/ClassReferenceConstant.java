package net.foxdenstudio.sponge.foxcore.common.modules.walker.constants;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;

public class ClassReferenceConstant implements IConstant {

    private final short classFQCNIndex;

    public ClassReferenceConstant(short classFQCNIndex) {

        this.classFQCNIndex = classFQCNIndex;
    }

    public static int parse(ClassWalker classWalker, int offset, short constantPoolOffset) {
        short classFQCNIndex = classWalker.u2(offset);
        offset += 2;
        classWalker.constantPoolItems[constantPoolOffset] = new ClassReferenceConstant(classFQCNIndex);
        return offset;
    }

    public short getClassFQCNIndex() {
        return classFQCNIndex;
    }

    @Override
    public String toString() {
        return "ClassReferenceConstant{" +
                "classFQCNIndex=" + classFQCNIndex +
                '}';
    }
}
