package net.foxdenstudio.sponge.foxcore.common.modules.walker;

public class FieldDeclaration {

    private AccessFlags accessFlags;
    private short nameIndex;
    private short descriptorIndex;
    private short attributeCount;

    public FieldDeclaration(short accessFlags, short nameIndex, short descriptorIndex, short attributeCount) {
        this.accessFlags = new AccessFlags(accessFlags);
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributeCount = attributeCount;
    }

    public AccessFlags getAccessFlags() {
        return accessFlags;
    }

    public short getNameIndex() {
        return nameIndex;
    }

    public short getDescriptorIndex() {
        return descriptorIndex;
    }

    public short getAttributeCount() {
        return attributeCount;
    }
}
