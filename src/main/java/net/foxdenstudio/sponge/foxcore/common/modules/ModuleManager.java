package net.foxdenstudio.sponge.foxcore.common.modules;

import net.foxdenstudio.sponge.foxcore.common.modules.walker.ClassWalker;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

public class ModuleManager {

    private final ArrayList<Class<? extends Annotation>> annotationsToFind;
    private final ArrayList<File> moduleFileList;

    public ModuleManager() {
        this.annotationsToFind = new ArrayList<>();
        moduleFileList = new ArrayList<>();
    }

    public static void main(String[] args) {
        final ModuleManager moduleManager = new ModuleManager();
        moduleManager.populateFileList();
        moduleManager.moduleFileList.forEach(ClassWalker::new);
    }

    public boolean addAnnotationToFind(Class<? extends Annotation> annotationClass) {
        return annotationsToFind.add(annotationClass);
    }

    private void populateFileList() {
        try {
            File moduleFolder = new File("fc-modules").getCanonicalFile();
            System.out.println(moduleFolder.getCanonicalPath());
            if (!moduleFolder.exists()) {
                moduleFolder.mkdirs();
            }

            for (final File moduleFile : moduleFolder.listFiles()) {
                if (FilenameUtils.isExtension(moduleFile.getCanonicalPath(), new String[]{"fm", "foxmod", "fgm", "fcm", "fem"})) {
                    moduleFileList.add(moduleFile);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
