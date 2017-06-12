package net.foxdenstudio.sponge.foxcore.common.modules.walker;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.constants.IConstant;
import org.apache.commons.lang3.mutable.MutableInt;

import java.io.PrintStream;
import java.util.HashMap;

public class ClassWalkerDebugger {

    private final ClassWalker classWalker;

    public ClassWalkerDebugger(ClassWalker classWalker) {

        this.classWalker = classWalker;
    }

    public void output(boolean error) {

        final PrintStream out = error ? System.err : System.out;

        HashMap<Class<? extends IConstant>, MutableInt> data = new HashMap<>();
        for (IConstant constantPoolItem : classWalker.constantPoolItems) {
            final Class<? extends IConstant> aClass = constantPoolItem.getClass();
            if (!data.containsKey(aClass)) {
                data.put(aClass, new MutableInt(0));
            }
            data.get(aClass).add(1);
        }

        out.println();
        for (int i = 0; i < 25; i++) {
            out.print('-');
        }
        out.println();

        out.println("Valid Signature: " + classWalker.hasValidSignature);
        out.println("Major Version: " + classWalker.majorVersion);
        out.println("Minor Version: " + classWalker.minorVersion);
        out.println("Constant Pool Size: " + classWalker.constantPoolSize);
        out.println("Access Flags: " + classWalker.accessFlags);
        out.println("This Class: " + classWalker.thisClass);
        out.println("Super Class: " + classWalker.superClass);
        out.println("Interface List Size: " + classWalker.interfaceListSize);
        out.println("Field List Size: " + classWalker.fieldListSize);
        out.println();

        out.println("Constant Pool Counts");
        data.forEach((aClass, mutableInt) -> {
            out.println("\t" + aClass.getSimpleName() + ": " + mutableInt);
        });
        for (int i = 0; i < 25; i++) {
            out.print('-');
        }
        out.println();
    }
}
