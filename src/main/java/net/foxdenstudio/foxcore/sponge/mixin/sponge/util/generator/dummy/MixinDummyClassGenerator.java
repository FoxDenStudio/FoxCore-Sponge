package net.foxdenstudio.foxcore.sponge.mixin.sponge.util.generator.dummy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.spongepowered.api.util.generator.dummy.DummyClassGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(DummyClassGenerator.class)
public abstract class MixinDummyClassGenerator {

    private static final String[] validClassPrefixes = { "org.spongepowered.api" };

    @Inject(method = "generateMethods", at = @At("HEAD"))
    private void onGenerateMethods(ClassWriter cw, String internalName, List<Method> methods, Class<?> exceptionType, CallbackInfo ci) {
        if (methods == null || methods.isEmpty()) return;

        Map<String, Method> map = new HashMap<>();
        List<Method> dispose = new ArrayList<>();

        for (Method method : methods) {
            String desc = Type.getMethodDescriptor(method);
            Method existing = map.get(desc);
            if (existing != null) {
                String className = method.getDeclaringClass().getName();
                boolean sponge = false;
                for (String prefix : validClassPrefixes) {
                    if (className.startsWith(prefix)) {
                        sponge = true;
                        break;
                    }
                }
                if (sponge) {
                    dispose.add(existing);
                    map.put(desc, method);
                } else {
                    dispose.add(method);
                }
            } else {
                map.put(desc, method);
            }
        }

        methods.removeAll(dispose);
    }
}
