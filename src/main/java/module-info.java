import io.github.evacchi.javac.plugin.MyPlugin;

module javac.plugin {
    requires jdk.compiler;
    requires java.logging;

    exports io.github.evacchi.query;
    exports io.github.evacchi.javac.plugin;

    provides com.sun.source.util.Plugin with MyPlugin;
}