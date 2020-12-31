package io.github.evacchi.javac.plugin;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * maps source code Paths to class files -- using string for simple serialization
 */
public class FileMapper {

    // we use String for simple serialization
    private final HashMap<String, Set<String>> sourceToClasses = new HashMap<>();

    public boolean isEmpty() {
        return sourceToClasses.isEmpty();
    }

    public void update(Path p, Path classFile) {
        getOrDefault(p).add(classFile.toString());
    }

    public void update(FileMapping fileMapping) {
        for (ClassFile classFile : fileMapping.classFiles()) {
            update(fileMapping.source(), classFile.path());
        }
    }

    public void merge(FileMapper batch) {
        for (var e : batch.sourceToClasses.entrySet()) {
            getOrDefault(e.getKey()).addAll(e.getValue());
        }
    }

    Collection<Path> get(Path path) {
        return sourceToClasses.getOrDefault(path.toString(), Collections.emptySet()).stream().map(Path::of).collect(Collectors.toUnmodifiableList());
    }

    public void remove(FileMapping f) {
        sourceToClasses.remove(f.source().toString());
    }

    /**
     * remove the given paths and return a FileMapper containing
     * the removed elements
     */
    FileMapper removeAll(Collection<Path> paths) {
        FileMapper removed = new FileMapper();
        for (Path p : paths) {
            String key = p.toString();
            Set<String> r = sourceToClasses.remove(key);
            if (r != null)
                removed.sourceToClasses.put(key, r);
        }
        return removed;
    }

    public Collection<FileMapping> asCollection() {
        return sourceToClasses.entrySet().stream()
                .map(e -> new FileMapping(e.getKey(), e.getValue()))
                .collect(Collectors.toUnmodifiableList());
    }

    public Collection<ClassFile> classFiles() {
        return sourceToClasses.values().stream()
                .flatMap(Collection::stream)
                .map(ClassFile::new)
                .collect(Collectors.toUnmodifiableList());
    }



    // utilities to initialize the key/value store

    private Set<String> getOrDefault(Path p) {
        return getOrDefault(p.toString());
    }

    private Set<String> getOrDefault(String p) {
        return sourceToClasses.computeIfAbsent(p, k -> new HashSet<>());
    }

    void store(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeObject(sourceToClasses);
    }

    @SuppressWarnings("unchecked")
    void load(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        HashMap<String, Set<String>> cache = (HashMap<String, Set<String>>) inputStream.readObject();
        this.sourceToClasses.putAll(cache);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (var e : sourceToClasses.entrySet()) {
            s.append(e.getKey());
            s.append('\n');
            if (e.getValue() != null) {
                for (String p : e.getValue()) {
                    s.append("    ").append(p).append('\n');
                }
            }
        }
        return s.toString();
    }
}
