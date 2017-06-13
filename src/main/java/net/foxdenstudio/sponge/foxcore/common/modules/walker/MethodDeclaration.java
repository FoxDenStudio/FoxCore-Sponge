package net.foxdenstudio.sponge.foxcore.common.modules.walker;

public class MethodDeclaration {
    private AccessFlags accessFlags;
    private short nameIndex;
    private short descriptorIndex;
    private short attributeCount;

    public MethodDeclaration(short accessFlags, short nameIndex, short descriptorIndex, short attributeCount) {
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

    @Override
    public String toString() {
        return "MethodDeclaration{" +
                "accessFlags=" + accessFlags +
                ", nameIndex=" + nameIndex +
                ", descriptorIndex=" + descriptorIndex +
                ", attributeCount=" + attributeCount +
                '}';
    }
}
