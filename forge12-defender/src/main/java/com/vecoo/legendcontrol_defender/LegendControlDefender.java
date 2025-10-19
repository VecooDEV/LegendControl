package com.vecoo.legendcontrol_defender;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.legendcontrol_defender.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.config.ServerConfig;
import com.vecoo.legendcontrol_defender.listener.DefenderListener;
import com.vecoo.legendcontrol_defender.storage.PlayerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = LegendControlDefender.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public class LegendControlDefender {
    public static final String MOD_ID = "legendcontrol_defender";
    private static Logger LOGGER;

    private static LegendControlDefender instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private PlayerProvider playerProvider;

    private MinecraftServer server;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;
        LOGGER = event.getModLog();

        loadConfig();
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        Pixelmon.EVENT_BUS.register(new DefenderListener());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();

        event.registerServerCommand(new LegendaryTrustCommand());
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.playerProvider.write();
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
        } catch (Exception e) {
            LOGGER.error("Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            if (this.playerProvider == null) {
                this.playerProvider = new PlayerProvider("/%directory%/storage/LegendControl/Defender/players/", this.server);
            }

            this.playerProvider.init();
        } catch (Exception e) {
            LOGGER.error("Error load storage.", e);
        }
    }

    public static LegendControlDefender getInstance() {
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

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}