package io.github.evacchi.javac.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.io.TempDir;

public abstract class BaseIT {

    public static @TempDir Path temp;

    final Path workingDirectory;
    final Path sourceDirectory;

    final Path targetDirectory;

    public BaseIT(Path workingDirectory) {
        this.workingDirectory = workingDirectory.toAbsolutePath();
        sourceDirectory = Paths.get("src/main/java");
        targetDirectory = Paths.get("target/classes");
    }

    void touch(Path fullPath) throws IOException {
        Files.write(fullPath, "/** this is a comment **/".getBytes(), StandardOpenOption.APPEND);
        Files.setLastModifiedTime(fullPath, theFuture());
    }

    FileTime theFuture() {
        return FileTime.fromMillis(System.currentTimeMillis() + 1000);
    }

    Path copySourceToTemp(String prefix) throws IOException {
        Path prj = temp.resolve(prefix);
        Files.walk(workingDirectory.resolve(sourceDirectory)).forEach(IOConsumer.of(src -> {
            Path relativePath = workingDirectory.relativize(src);
            Path dest = prj.resolve(relativePath);
            if (Files.isRegularFile(src)) {
                Files.createDirectories(dest.getParent());
                Files.copy(src, dest);
            }
        }));
        return prj;
    }
}
