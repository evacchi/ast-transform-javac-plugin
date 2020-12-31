package io.github.evacchi.javac.plugin;

import java.nio.file.Path;

/**
 *
 * A pair class Path, binaryName.
 *
 * <p>we assume here binary name == classFile path - extension
 * (path is relative to the target root); hence the binary name
 * can be inferred from the given Path
 *
 */

public class ClassFile {

    public static final String CLASS_FILE_EXTENSION = ".class";

    private final Path path;
    private final String binaryName;

    protected ClassFile(Path path, String binaryName) {
        this.path = path;
        this.binaryName = binaryName;
        if (this.path.isAbsolute()) {
            throw new IllegalArgumentException("Path must be relative, given: " + this.path());
        }
    }

    public ClassFile(String path) {
        this(Path.of(path), toBinaryName(path));
    }

    private static String toBinaryName(String path) {
        return path.substring(0, path.length() - CLASS_FILE_EXTENSION.length());
    }

    public Path path() {
        return path;
    }

    public String binaryName() {
        return binaryName;
    }
}
