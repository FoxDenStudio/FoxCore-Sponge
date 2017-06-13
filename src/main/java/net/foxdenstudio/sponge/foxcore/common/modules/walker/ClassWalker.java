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
    public FieldDeclaration[] fieldList;
    public short methodListSize;
    public MethodDeclaration[] methodList;


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
            offset = parseConstantPool(offset);

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
            fieldList = new FieldDeclaration[fieldListSize];
            offset = parseFieldList(offset);

            methodListSize = u2(offset);
            offset += 2;
            methodList = new MethodDeclaration[methodListSize];
            offset = parseMethodList(offset);

//            for (FieldDeclaration fieldDeclaration : fieldList) {
//                System.out.println(constantPoolItems[fieldDeclaration.getNameIndex() - 1]);
//            }
//            System.out.println();
//            for (MethodDeclaration methodDeclaration : methodList) {
//                System.out.println(constantPoolItems[methodDeclaration.getNameIndex() - 1]);
//            }

            System.err.println(constantPoolItems[((ClassReferenceConstant) constantPoolItems[thisClass - 1]).getClassFQCNIndex() - 1]);
            new ClassWalkerDebugger(this).output(true);

        } catch (IOException e) {
            e.printStackTrace();
            new ClassWalkerDebugger(this).output(false);
        }
    }

    private int parseMethodList(int offset) {
        for (short methodListOffset = 0; methodListOffset < methodListSize; methodListOffset++) {
            short accessFlags = u2(offset);
            offset += 2;
            short nameIndex = u2(offset);
            offset += 2;
            short descriptorIndex = u2(offset);
            offset += 2;
            short attributesCount = u2(offset);
            offset += 2;

            for (int i = 0; i < attributesCount; i++) {
                short attrName = u2(offset);
                offset += 2;
                int attrLen = u4(offset);
                offset += 4;
                offset += attrLen;
            }

            methodList[methodListOffset] = new MethodDeclaration(accessFlags, nameIndex, descriptorIndex, attributesCount);
        }
        return offset;
    }

    private int parseFieldList(int offset) {
        for (short fieldListOffset = 0; fieldListOffset < fieldListSize; fieldListOffset++) {
            short accessFlags = u2(offset);
            offset += 2;
            short nameIndex = u2(offset);
            offset += 2;
            short descriptorIndex = u2(offset);
            offset += 2;
            short attributesCount = u2(offset);
            offset += 2;

            for (int i = 0; i < attributesCount; i++) {
                short attrName = u2(offset);
                offset += 2;
                int attrLen = u4(offset);
                offset += 4;
                offset += attrLen;

//                if (((UTF8StringConstant) constantPoolItems[attrName - 1]).getUTF8String().equals("RuntimeVisibleAnnotations")) {
//                    int attrLen = u4(offset);
//                    offset += 4;
//                    short numAnnos = u2(offset);
//                    offset += 2;
//
//                    for (int j = 0; j < numAnnos; j++) {
//                        offset++; //FIXME Attriubutes arent actually read... need to fix this if i want to be able to scan for annoations
//                    }
//                    System.out.println();
//                } else {
//                    int attrLen = u4(offset);
//                    offset += 4;
//                    System.out.println("T: ");
//                    for (int j = 0; j < attrLen; j++) {
//                        System.out.print(classBytes[offset + j] + " ");
//                    }
//                    System.out.println();
//                    offset += attrLen;
//                }
            }

            fieldList[fieldListOffset] = new FieldDeclaration(accessFlags, nameIndex, descriptorIndex, attributesCount);
        }
        return offset;
    }

    private int parseConstantPool(int offset) {
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
//                    System.out.println("Long");
                    offset += 8;
                    break;
                case 0x06:
//                    System.out.println("Double");
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
//                    System.out.println("Method Handle");
                    offset += 3;
                    break;
                case 0x10:
//                    System.out.println("Method Type");
                    offset += 2;
                    break;
                case 0x11:
                    throw new RuntimeException("Invalid Constant Tag Byte: 0x11");
                case 0x12:
//                    System.out.println("Invoke Dynamic");
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
