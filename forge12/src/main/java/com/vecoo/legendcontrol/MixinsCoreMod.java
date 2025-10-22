package com.vecoo.legendcontrol;

import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class MixinsCoreMod implements IFMLLoadingPlugin {
    private static final String[] ALLOWED_TYPES = {
            "jar",
            "zip"
    };

    private final List<Tuple<String, String>> coremods = Lists.newArrayList();
    private final List<Tuple<String, String>> loadedCoremods = Lists.newArrayList();

    private void addToCoremodList(String mod, String mixin) {
        if (mod == null || mod.trim().isEmpty() || mixin == null || mixin.trim().isEmpty()) {
            this.log("Loaded a null mod or mixins (mod=" + mod + ", mixin=" + mixin + ") this is not valid and will be dumped!");
        } else {
            this.log("Added Optional Preloader for \"" + mod + "\" using \"" + mixin + "\"");
            this.coremods.add(new Tuple<>(mod, mixin));
        }
    }

    private void loadCoremod(File coremod, Tuple<String, String> target) {
        try {
            if (!CoreModManager.getReparseableCoremods().contains(coremod.getName())) {
                ((LaunchClassLoader) getClass().getClassLoader()).addURL(coremod.toURI().toURL());
                CoreModManager.getReparseableCoremods().add(coremod.getName());
                log("Preloaded mod \"" + coremod.getName() + "\" containing \"" + target.getFirst() + "\"");
            } else {
                log("Skipped Preloading already loaded coremod \"" + coremod.getName() + "\" with \"" + target.getSecond() + "\"");
            }
        } catch (Throwable t) {
            log("Failed to  load a coremod! Caught " + t.getClass().getSimpleName() + "!" + " caused by " + (target == null ? "target was null" : target.getFirst() + " - " + target.getSecond()), t);
        }
    }

    private void loadCoremodList() {
        loadFolder();
        initializeMixins();
    }

    private void initializeMixins() {
        try {
            MixinBootstrap.init();
            for (final Tuple<String, String> tuple : new ArrayList<>(loadedCoremods)) {
                try {
                    log("Loading Coremod mixins \"" + tuple.getSecond() + "\"");
                    Mixins.addConfiguration(tuple.getSecond());
                } catch (Throwable t) {
                    log("Caught Exception trying to preload mod configurations for \"" + (tuple != null ? tuple.getSecond() : "null entry") + "\"", t);
                }
            }
        } catch (final Throwable t) {
            log("Caught Exception trying to preload mod configurations", t);
        }
    }

    private void loadFolder() {
        try {
            final File modsFolder = new File(System.getProperty("user.dir"), "mods");
            if (!modsFolder.exists()) {
                log("The \"" + "mods" + "\" folder couldn't be found skipping this loader! Folder: " + modsFolder.toString());
                return;
            }

            Collection<File> jars = FileUtils.listFiles(modsFolder, ALLOWED_TYPES, false);

            for (final File jar : jars) {
                final ZipInputStream zip = new ZipInputStream(Files.newInputStream(jar.toPath()));
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    zip.closeEntry();
                    String name = entry.getName();
                    for (Tuple<String, String> tuple : new ArrayList<>(coremods)) {
                        if (name.equals(tuple.getFirst())) {
                            loadCoremod(jar, tuple);
                            loadedCoremods.add(tuple);
                            coremods.remove(tuple);
                        }
                    }
                }
                zip.close();
            }

        } catch (Throwable t) {
            log("Caught Exception trying to load \"" + "mods" + "\" for coremods! This will likely be fatal", t);
        }
    }

    public MixinsCoreMod() {
        addToCoremodList("com/pixelmonmod/pixelmon/Pixelmon.class", "mixins.legendcontrol.json");
        loadCoremodList();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    private void log(final String message) {
        this.log(message, null);
    }

    private void log(final String message, final Throwable e) {
        FMLLog.log.info("[" + "INFO" + "] [" + "Mixins" + "] " + "> " + message);

        if (e != null) {
            for (final String s : getException(e)) {
                FMLLog.log.info("[" + "INFO" + "] [" + "Mixins" + "] " + "> " + s);
            }
        }
    }

    private static List<String> getException(final Throwable e) {
        List<String> exception = Lists.newArrayList(e.getClass().getSimpleName() + ": " + e.getMessage());

        for (final StackTraceElement element : e.getStackTrace()) {
            exception.add(element.toString());
        }

        return exception;
    }

    public static class Tuple<X, Y> {
        private final X x;
        private final Y y;

        public final X getFirst() {
            return x;
        }

        public final Y getSecond() {
            return y;
        }

        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }
}