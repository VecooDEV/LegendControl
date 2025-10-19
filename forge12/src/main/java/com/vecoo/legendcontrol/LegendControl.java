package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.legendcontrol.command.CheckLegendsCommand;
import com.vecoo.legendcontrol.command.LegendControlCommand;
import com.vecoo.legendcontrol.config.DiscordConfig;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.discord.DiscordWebhook;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import com.vecoo.legendcontrol.listener.OtherListener;
import com.vecoo.legendcontrol.listener.ParticleListener;
import com.vecoo.legendcontrol.listener.ResultListener;
import com.vecoo.legendcontrol.storage.ServerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = LegendControl.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static Logger LOGGER;

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private DiscordConfig discord;

    private ServerProvider serverProvider;

    private MinecraftServer server;

    private DiscordWebhook webhook;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;
        LOGGER = event.getModLog();

        loadConfig();
    }

    @Mod.EventHandler
    public void onInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ParticleListener());
        MinecraftForge.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new OtherListener());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();

        event.registerServerCommand(new CheckLegendsCommand());
        event.registerServerCommand(new LegendControlCommand());
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.serverProvider.write();
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
            this.discord = new DiscordConfig();
            this.discord.init();
            this.webhook = new DiscordWebhook(this.discord.getWebhookUrl());
        } catch (Exception e) {
            LOGGER.error("Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            if (this.serverProvider == null) {
                this.serverProvider = new ServerProvider("/%directory%/storage/LegendControl/", this.server);
            }

            this.serverProvider.init();
        } catch (Exception e) {
            LOGGER.error("Error load storage.", e);
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

    public DiscordConfig getDiscordConfig() {
        return instance.discord;
    }

    public ServerProvider getServerProvider() {
        return instance.serverProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }

    public DiscordWebhook getWebhook() {
        return instance.webhook;
    }
}