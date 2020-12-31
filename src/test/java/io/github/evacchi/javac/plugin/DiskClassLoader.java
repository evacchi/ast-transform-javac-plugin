package io.github.evacchi.javac.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class DiskClassLoader extends ClassLoader {

    private final Path basePath;

    public DiskClassLoader(Path basePath) {
        super();
        this.basePath = basePath;
    }

    Object loadAndInvoke(String className, String method)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> aClass = loadClass(className);
        Object a = aClass.getConstructor().newInstance();
        Method m = aClass.getMethod(method);
        return m.invoke(a);
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        try {
            byte[] bytes = Files.readAllBytes(basePath.resolve(name.replace('.', '/') + ".class"));
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Could not find file", e);
        }
    }
}