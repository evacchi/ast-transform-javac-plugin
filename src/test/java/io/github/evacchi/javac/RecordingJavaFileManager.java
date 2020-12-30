package io.github.evacchi.javac;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Path;

import javax.tools.FileObject;
import javax.tools.ForwardingFileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.ForwardingJavaFileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;


/**
 * Delegates to a {@link StandardJavaFileManager} and
 * collects compiled source files and their mapping to generated class files into a {@link FileMapper}
 *
 * inspired from the Takari RecordingJavaFileManager
 *
 * @see FileMapper
 */
class RecordingJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private final FileMapper fileMapper;
    private final Path sourcePath;
    private final Path targetPath;

    RecordingJavaFileManager(StandardJavaFileManager fileManager, Path sourcePath, Path targetPath) {
        super(fileManager);
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
        this.fileMapper = new FileMapper();
    }

    public FileMapper processedFiles() {
        return fileMapper;
    }

    private void record(FileObject inputFile, FileObject outputFile) {
        Path input = sourcePath.relativize(Path.of(inputFile.toUri().getPath()));
        Path output = targetPath.relativize(Path.of(outputFile.toUri().getPath()));
        fileMapper.update(input, output);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, final FileObject sibling) throws IOException {
        FileObject fileObject = super.getFileForOutput(location, packageName, relativeName, sibling);
        return new ForwardingFileObject<>(fileObject) {
            @Override
            public OutputStream openOutputStream() throws IOException {
                try {
                    return super.openOutputStream();
                } finally {
                    record(sibling, fileObject);
                }
            }

            @Override
            public Writer openWriter() throws IOException {
                return new OutputStreamWriter(openOutputStream());
            }
        };
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, javax.tools.JavaFileObject.Kind kind, final FileObject sibling) throws IOException {
        JavaFileObject fileObject = super.getJavaFileForOutput(location, className, kind, sibling);
        return new ForwardingJavaFileObject<>(fileObject) {
            @Override
            public OutputStream openOutputStream() throws IOException {
                try {
                    return super.openOutputStream();
                } finally {
                    record(sibling, fileObject);
                }
            }

            @Override
            public Writer openWriter() throws IOException {
                return new OutputStreamWriter(openOutputStream());
            }
        };
    }
}
