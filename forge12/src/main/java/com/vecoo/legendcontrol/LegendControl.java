package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.config.YamlConfigFactory;
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
import com.vecoo.legendcontrol.service.ServerService;
import lombok.Getter;
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

    @Getter
    private static LegendControl instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;
    private DiscordConfig discordConfig;

    private ServerService serverService;

    private MinecraftServer server;

    private DiscordWebhook discordWebhook;

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event) {
        instance = this;
        LOGGER = event.getModLog();

        loadConfig();
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ParticleListener());
        MinecraftForge.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new OtherListener());
    }

    @Mod.EventHandler
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();

        event.registerServerCommand(new CheckLegendsCommand());
        event.registerServerCommand(new LegendControlCommand());
    }

    @Mod.EventHandler
    public void onFMLServerStopping(FMLServerStoppingEvent event) {
        this.serverService.save();
    }

    public void loadConfig() {
        this.serverConfig = YamlConfigFactory.load(ServerConfig.class, "config/LegendControl/config.yml");
        this.localeConfig = YamlConfigFactory.load(LocaleConfig.class, "config/LegendControl/locale.yml");
        this.discordConfig = YamlConfigFactory.load(DiscordConfig.class, "config/LegendControl/discord.yml");
        this.discordWebhook = new DiscordWebhook(this.discordConfig.getWebhookUrl());
    }

    private void loadStorage() {
        try {
            this.serverService = new ServerService("/%directory%/storage/LegendControl/", this.server);
            this.serverService.init();
        } catch (Exception e) {
            LOGGER.error("Error load storage.", e);
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getServerConfig() {
        return instance.serverConfig;
    }

    public LocaleConfig getLocaleConfig() {
        return instance.localeConfig;
    }

    public DiscordConfig getDiscordConfig() {
        return instance.discordConfig;
    }

    public ServerService getServerService() {
        return instance.serverService;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }

    public DiscordWebhook getDiscordWebhook() {
        return instance.discordWebhook;
    }
}