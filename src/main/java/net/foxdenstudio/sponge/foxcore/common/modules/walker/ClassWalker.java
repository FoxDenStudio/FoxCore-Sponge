package net.foxdenstudio.sponge.foxcore.common.modules.walker;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.constants.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ClassWalker {

    public boolean hasValidSignature;
    public short majorVersion;
    public short minorVersion;
    public short constantPoolSize;
    public byte[] classBytes;
    public IConstant[] constantPoolItems;
    public AccessFlags accessFlags;
    public short thisClass;
    public short superClass;
    public short interfaceListSize;
    public short fieldListSize;


    public ClassWalker(final File moduleFile) {
        try {
            System.out.println("Reading: " + moduleFile.toPath().toString());
            classBytes = Files.readAllBytes(moduleFile.toPath());

            int offset = 0;
            hasValidSignature = checkSignature(); //Check signature (CAFEBABE)
            offset += 4;
            minorVersion = u2(offset); //Get minor version
            offset += 2;
            majorVersion = u2(offset); //Get major version J9=53, J8=52, J7=51
            offset += 2;
            constantPoolSize = (short) (u2(offset) - 1); //Get size of constant pool
            offset += 2;

            constantPoolItems = new IConstant[constantPoolSize];
            offset = parseConstantPool(offset, constantPoolSize);

            accessFlags = new AccessFlags(u2(offset));
            offset += 2;

            thisClass = u2(offset);
            offset += 2;

            superClass = u2(offset);
            offset += 2;

            interfaceListSize = u2(offset);
            offset += 2;
            offset += 2 * interfaceListSize; // FIXME Currently ignores interface inheritence, parse this at some point

            fieldListSize = u2(offset);
            offset += 2;

            new ClassWalkerDebugger(this).output(true);

        } catch (IOException e) {
            e.printStackTrace();
            new ClassWalkerDebugger(this).output(false);
        }
    }

    private int parseConstantPool(int offset, short constantPoolSize) {
        for (short constantPoolOffset = 0; constantPoolOffset < constantPoolSize; constantPoolOffset++) {
            switch (classBytes[offset++]) {
                case 0x01: // UTF8
                    offset = UTF8StringConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x02:
                    throw new RuntimeException("Invalid Constant Tag Byte: 0x02");
                case 0x03: // Integer
                    offset = IntegerConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x04: // Float
                    offset = FloatConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x05:
                    System.out.println("Long");
                    offset += 8;
                    break;
                case 0x06:
                    System.out.println("Double");
                    offset += 8;
                    break;
                case 0x07: // Class Ref
                    offset = ClassReferenceConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x08: // String Ref
                    offset = StringReferenceConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x09: // Field Ref
                    offset = FieldReferenceConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x0a: // Method Ref
                    offset = MethodReferenceConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x0b: // Interface Method Ref
                    offset = InterfaceMethodReferenceConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x0c: // Name & Type Desc.
                    offset = NameAndTypeDescriptorConstant.parse(this, offset, constantPoolOffset);
                    break;
                case 0x0d:
                    throw new RuntimeException("Invalid Constant Tag Byte: 0x0d");
                case 0x0e:
                    throw new RuntimeException("Invalid Constant Tag Byte: 0x0e");
                case 0x0F:
                    System.out.println("Method Handle");
                    break;
                case 0x10:
                    System.out.println("Method Type");
                    break;
                case 0x11:
                    throw new RuntimeException("Invalid Constant Tag Byte: 0x11");
                case 0x12:
                    System.out.println("Invoke Dynamic");
//                    offset = parseInvokeDynamic(offset, constantPoolItems, constantPoolOffset);
                    offset += 4;
                    break;
                default:
                    throw new RuntimeException("Invalid Constant Tag Byte: " + (classBytes[offset - 1]) + " @ " + offset);
            }
        }

        return offset;
    }

    public byte u1(int offset) {
        return classBytes[offset];
    }

    public short u2(int offset) {
        return (short) ((classBytes[offset] & 0xFF) << 8 | classBytes[offset + 1] & 0xFF);
    }

    public int u4(int offset) {
        return ((classBytes[offset] & 0xFF) << 18 | (classBytes[offset + 1] & 0xFF) << 16 | (classBytes[offset + 2] & 0xFF) << 8 | classBytes[offset + 3] & 0xFF);
    }

    private boolean checkSignature() {
        return u1(0) == ((byte) 0xCA) && u1(1) == ((byte) 0xFE) && u1(2) == ((byte) 0xBA) && u1(3) == ((byte) 0xBE);
    }
}
