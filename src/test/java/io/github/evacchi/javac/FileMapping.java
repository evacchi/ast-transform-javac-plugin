package io.github.evacchi.javac;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple mapping between source Path and corresponding ClassFiles
 */
public class FileMapping {

    private final Path source;
    private final Collection<ClassFile> classFiles;

    FileMapping(Path source, Collection<ClassFile> classFiles) {
        this.source = source;
        this.classFiles = classFiles;
    }

    FileMapping(String source, Set<String> classFiles) {
        this(Path.of(source),
             classFiles.stream()
                     .map(ClassFile::new)
                     .collect(Collectors.toUnmodifiableList()));
    }

    public Path source() {
        return source;
    }

    public Collection<ClassFile> classFiles() {
        return classFiles;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(source.toString()).append('\n');
        classFiles.forEach(cl -> sb.append(cl.toString()).append('\n'));
        return sb.toString();
    }
}
