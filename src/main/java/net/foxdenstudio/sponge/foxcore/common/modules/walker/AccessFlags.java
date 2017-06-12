package net.foxdenstudio.sponge.foxcore.common.modules.walker;

public class AccessFlags {
    public static final int ACC_PUBLIC = 0x1;
    public static final int ACC_PRIVATE = 0x2;
    public static final int ACC_PROTECTED = 0x4;
    public static final int ACC_STATIC = 0x8;
    public static final int ACC_FINAL = 0x10;
    public static final int ACC_SUPER = 0x20;
    public static final int ACC_SYNCHRONIZED = 0x20;
    public static final int ACC_VOLATILE = 0x40;
    public static final int ACC_BRIDGE = 0x40;
    public static final int ACC_TRANSIENT = 0x80;
    public static final int ACC_VARARGS = 0x80;
    public static final int ACC_NATIVE = 0x100;
    public static final int ACC_INTERFACE = 0x200;
    public static final int ACC_ABSTRACT = 0x400;
    public static final int ACC_STRICT = 0x800;
    public static final int ACC_SYNTHETIC = 0x1000;
    public static final int ACC_ANNOTATION = 0x2000;
    public static final int ACC_ENUM = 0x4000;
    private final short accessMask;


    public AccessFlags(short accessMask) {

        this.accessMask = accessMask;
    }

    public boolean isClassPublic() {
        return (accessMask & ACC_PUBLIC) != 0;
    }

    public boolean isClassFinal() {
        return (accessMask & ACC_FINAL) != 0;
    }

    public boolean isClassSuper() {
        return (accessMask & ACC_SUPER) != 0;
    }

    public boolean isClassInterface() {
        return (accessMask & ACC_INTERFACE) != 0;
    }

    public boolean isClassAbstract() {
        return (accessMask & ACC_ABSTRACT) != 0;
    }

    public boolean isClassSynthetic() {
        return (accessMask & ACC_SYNTHETIC) != 0;
    }

    public boolean isClassAnnotation() {
        return (accessMask & ACC_ANNOTATION) != 0;
    }

    public boolean isClassEnum() {
        return (accessMask & ACC_ENUM) != 0;
    }

    @Override
    public String toString() {
        return "AccessFlags{" +
                "accessMask=" + accessMask +
                ", classPublic=" + isClassPublic() +
                ", classFinal=" + isClassFinal() +
                ", classSuper=" + isClassSuper() +
                ", classInterface=" + isClassInterface() +
                ", classAbstract=" + isClassAbstract() +
                ", classSynthetic=" + isClassSynthetic() +
                ", classAnnotation=" + isClassAnnotation() +
                ", classEnum=" + isClassEnum() +
                '}';
    }
}
