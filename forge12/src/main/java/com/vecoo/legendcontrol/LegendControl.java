package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.permission.UtilPermissions;
import com.vecoo.legendcontrol.command.CheckLegendsCommand;
import com.vecoo.legendcontrol.command.LegendControlCommand;
import com.vecoo.legendcontrol.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.PermissionConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.listener.LegendaryListener;
import com.vecoo.legendcontrol.storage.player.PlayerProvider;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import com.vecoo.legendcontrol.task.ParticleTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LegendControl.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogManager.getLogger("LegendControl");

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private PermissionConfig permission;

    private PlayerProvider playerProvider;
    private ServerProvider serverProvider;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;

        this.loadConfig();

        UtilPermissions.registerPermission(permission.getPermissionCommand());
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ParticleTask());
        Pixelmon.EVENT_BUS.register(new LegendaryListener());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CheckLegendsCommand());
        event.registerServerCommand(new LegendaryTrustCommand());
        event.registerServerCommand(new LegendControlCommand());

        this.loadStorage();
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
            this.permission = new PermissionConfig();
            this.permission.init();
        } catch (Exception e) {
            LOGGER.error("[LegendControl] Error load config.");
        }
    }

    public void loadStorage() {
        try {
            this.playerProvider = new PlayerProvider();
            this.playerProvider.init();
            this.serverProvider = new ServerProvider();
            this.serverProvider.init();
        } catch (Exception e) {
            LOGGER.error("[LegendControl] Error load storage.");
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

    public PermissionConfig getPermission() {
        return instance.permission;
    }

    public ServerProvider getServerProvider() {
        return instance.serverProvider;
    }

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }
}