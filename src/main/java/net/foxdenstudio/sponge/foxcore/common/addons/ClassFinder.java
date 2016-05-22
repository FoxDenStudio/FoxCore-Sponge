/*
 * This file is part of FoxCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) gravityfox - https://gravityfox.net/
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.foxdenstudio.sponge.foxcore.common.addons;

import net.foxdenstudio.sponge.foxcore.plugin.FoxCoreMain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Created by d4rkfly3r on 5/22/2016.
 */
public final class ClassFinder {

    private static final String ADDON_DIR = "fcaddons";
    private JarFileLoader jarFileLoader;
    private ScalaFileLoader scalaFileLoader;
    private ArrayList<Class<?>> subClasses;

    public void initialize() {
        jarFileLoader = new JarFileLoader(new URL[]{});
        scalaFileLoader = new ScalaFileLoader(new URL[]{});
        subClasses = findSubclasses(getClasspathLocations());
    }

    @Nonnull
    public List<Class<?>> getClasses(Class<? extends Annotation> annotationClass) {
        return subClasses.parallelStream().filter(aClass -> aClass.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
    }

    public ArrayList<Class<?>> getAllClasses() {
        return subClasses;
    }

    @Nonnull
    private ArrayList<Class<?>> findSubclasses(@Nonnull Map<URL, String> locations) {
        ArrayList<Class<?>> v = new ArrayList<>();
        ArrayList<Class<?>> w;
        for (URL url : locations.keySet()) {
            w = findSubclasses(url, locations.get(url));
            if ((w.size() > 0)) v.addAll(w);
        }
        return v;
    }

    @Nonnull
    private ArrayList<Class<?>> findSubclasses(@Nonnull URL location, @Nonnull String packageName) {
        Map<Class<?>, URL> thisResult = new TreeMap<>((c1, c2) -> String.valueOf(c1).compareTo(String.valueOf(c2)));
        ArrayList<Class<?>> v = new ArrayList<>();
        List<URL> knownLocations = new ArrayList<>();
        knownLocations.add(location);
        // TODO: add getResourceLocations() to this list
        for (URL url : knownLocations) {
            File directory = new File(url.getFile());
            if (directory.exists()) {
                String[] files = directory.list();
                for (String file : files) {
                    if (file.endsWith(".class")) {
                        String classname = file.substring(0, file.length() - 6);
                        try {
                            Class c = jarFileLoader.loadClass(packageName + "." + classname);
                            thisResult.put(c, url);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } else {
                try {
                    JarURLConnection conn = (JarURLConnection) url.openConnection();
                    JarFile jarFile = conn.getJarFile();
                    Enumeration<JarEntry> e = jarFile.entries();
                    while (e.hasMoreElements()) {
                        JarEntry entry = e.nextElement();
                        String entryName = entry.getName();
                        if (!entry.isDirectory() && entryName.endsWith(".class")) {
                            String classname = entryName.substring(0, entryName.length() - 6);
                            if (classname.startsWith("/")) {
                                classname = classname.substring(1);
                            }
                            classname = classname.replace('/', '.');
                            try {
                                Class c = jarFileLoader.loadClass(classname);
                                thisResult.put(c, url);
                            } catch (Error | Exception ignored) {
                                ignored.printStackTrace();
                            }
                        }
                    }
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }
        }
        v.addAll(thisResult.keySet().stream().collect(Collectors.toList()));
        return v;
    }


    private Map<URL, String> getClasspathLocations() {
        Map<URL, String> map = new TreeMap<>((u1, u2) -> String.valueOf(u1).compareTo(String.valueOf(u2)));
        Path addonDir = Paths.get(ADDON_DIR); // TODO URGENT - Get Addons dir from config
        if (Files.notExists(addonDir)) {
            try {
                if (FoxCoreMain.instance() != null) {
                    FoxCoreMain.instance().logger().info("Creating Addon Directory ( " + addonDir.toAbsolutePath() + " )!");
                }else{
                    System.out.println("Creating Addon Directory ( " + addonDir.toAbsolutePath() + " )!");
                }
                Files.createDirectories(addonDir);
            } catch (IOException e) {
                if (FoxCoreMain.instance() != null) {
                    FoxCoreMain.instance().logger().error("Addon Directory ( " + addonDir.toAbsolutePath() + " ) could not be created!");
                }else{
                    System.err.println("Addon Directory ( " + addonDir.toAbsolutePath() + " ) could not be created!");
                }
                return map;
            }
        }
        if (Files.isDirectory(addonDir)) {
            try (final DirectoryStream<Path> stream = Files.newDirectoryStream(addonDir)) {
                stream.forEach(filePath -> {
                    try {
                        jarFileLoader.addFile("jar:" + filePath.toUri() + "!/");
                        include(null, filePath, map);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (final IOException e) {
                e.printStackTrace();
            }
            // ELSE NO FILES IN ADDONS DIRECTORY
        } else {
            if (FoxCoreMain.instance() != null) {
                FoxCoreMain.instance().logger().error("Addon Directory ( " + addonDir.toAbsolutePath() + " ) is not a folder!");
            }else{
                System.err.println("Addon Directory ( " + addonDir.toAbsolutePath() + " ) is not a folder!");
            }
        }
        return map;
    }

    private void include(@Nullable String name, @Nonnull Path filePath, @Nonnull Map<URL, String> map) {
        if (Files.notExists(filePath)) return;
        if (!Files.isDirectory(filePath)) {
            includeJar(filePath, map);
            return;
        }
        if (name == null) {
            name = "";
        } else {
            name += ".";
        }
        final String finalName = name;
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(filePath, Files::isDirectory)) {
            stream.forEach(dirPath -> {
                try {
                    map.put(new URL("file://" + dirPath.toAbsolutePath()), finalName + dirPath.getFileName());
                } catch (IOException ioe) {
                    return;
                }
                include(finalName + dirPath.getFileName(), dirPath, map);

            });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void includeJar(@Nonnull Path filePath, @Nonnull Map<URL, String> map) {
        if (Files.isDirectory(filePath)) return;
        URL jarURL;
        JarFile jar;
        try {
            jarURL = new URL("file:/" + filePath.toAbsolutePath());
            jarURL = new URL("jar:" + jarURL.toExternalForm() + "!/");
            JarURLConnection conn = (JarURLConnection) jarURL.openConnection();
            jar = conn.getJarFile();
        } catch (final Exception e) {
            return;
        }
        if (jar == null) return;
        map.put(jarURL, "");
    }

    public static class JarFileLoader extends java.net.URLClassLoader {
        public JarFileLoader(URL[] urls) {
            super(urls);
        }

        public void addFile(String path) throws MalformedURLException {
            addURL(new URL(path));
        }
    }

    public static class ScalaFileLoader extends scala.reflect.internal.util.ScalaClassLoader.URLClassLoader {
        public ScalaFileLoader(URL[] urls) {
            // ALERT!!! WHATEVER YOU DO, DO NOT get rid or change in any way the new ArrayList<URL>... everything dies...
            super(scala.collection.JavaConverters.collectionAsScalaIterableConverter(new ArrayList<URL>()).<URL>asScala().toSeq(), null /* Is null OK here? TBD... */);
        }

        public void addFile(String path) throws MalformedURLException {
            addURL(new URL(path));
        }
    }
}