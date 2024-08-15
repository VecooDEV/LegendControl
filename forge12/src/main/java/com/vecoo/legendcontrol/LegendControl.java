package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.legendcontrol.commands.CheckLegendsCommand;
import com.vecoo.legendcontrol.commands.LegendControlCommand;
import com.vecoo.legendcontrol.commands.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.PermissionsConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.listener.LegendaryListener;
import com.vecoo.legendcontrol.task.ParticleTask;
import com.vecoo.legendcontrol.storage.player.PlayerProvider;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = "legendcontrol",
        name = "LegendControl",
        version = "2.0.1",
        acceptableRemoteVersions = "*"
)
public class LegendControl {
    private static final Logger LOGGER = LogManager.getLogger("LegendControl");

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private PermissionsConfig permissions;

    private PlayerProvider playerProvider;
    private ServerProvider serverProvider;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(new ParticleTask());
        Pixelmon.EVENT_BUS.register(new LegendaryListener());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CheckLegendsCommand());
        event.registerServerCommand(new LegendaryTrustCommand());
        event.registerServerCommand(new LegendControlCommand());
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
            this.permissions = new PermissionsConfig();
            this.permissions.init();
            this.playerProvider = new PlayerProvider();
            this.playerProvider.init();
            this.serverProvider = new ServerProvider();
            this.serverProvider.init();
        } catch (Exception e) {
            LOGGER.error("Error load config.");
        }
    }

    public static LegendControl getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getConfig() {
        return instance.config;
    }

    public LocaleConfig getLocale() {
        return instance.locale;
    }

    public PermissionsConfig getPermissions() {
        return instance.permissions;
    }

    public ServerProvider getServerProvider() {
        return instance.serverProvider;
    }

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }
}