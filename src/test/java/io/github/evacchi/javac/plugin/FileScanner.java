package io.github.evacchi.javac.plugin;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Scans all files on a given path, filtering by extension
 */
public class FileScanner {

    private final Path searchPath;
    private final String extension;

    public FileScanner(Path searchPath, String extension) {
        this.searchPath = searchPath;
        this.extension = extension;
    }

    public Collection<Path> scan() {
        try {
            // naive file extension filtering, ok for poc purposes
            return Files.walk(searchPath)
                    .filter(f -> f.toString().endsWith(extension))
                    .map(searchPath::relativize)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean isNewer(Path p, long lastTimestamp) throws IOException {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(p, BasicFileAttributes.class);
        return basicFileAttributes.isRegularFile() && basicFileAttributes.lastModifiedTime().toMillis() > lastTimestamp;
    }
}
