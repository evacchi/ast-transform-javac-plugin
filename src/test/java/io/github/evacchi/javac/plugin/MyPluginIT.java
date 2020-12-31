package io.github.evacchi.javac.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import io.github.evacchi.query.Query;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyPluginIT extends BaseIT {

    public MyPluginIT() {
        super(Paths.get("src/test/resources/project1"));
    }

    @Test
    public void mytest() throws Exception {
        Path prj = copySourceToTemp("mytest");
        FileScanner fsc = new FileScanner(prj.resolve(sourceDirectory), ".java");
        JavaCompiler compiler =
                new JavaCompiler(
                        fsc.scan(),
                        prj.resolve(sourceDirectory).toAbsolutePath(),
                        prj.resolve(targetDirectory).toAbsolutePath())
                        .withOption("-Xplugin:MyPlugin");

        FileMapper compiled = compiler.compile();
        DiskClassLoader cl = new DiskClassLoader(prj.resolve(targetDirectory));
        assertEquals(0, Query.IndexCount.get());
        Object result = cl.loadAndInvoke("com.example.A", "a");
        assertEquals(Collections.singletonList("quux"), result);
        assertEquals(1, Query.IndexCount.get());
        assertEquals(4, Query.IndexHits.get());
    }
}
