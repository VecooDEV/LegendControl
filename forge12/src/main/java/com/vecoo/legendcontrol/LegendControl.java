package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.loader.YamlLoader;
import com.vecoo.legendcontrol.command.CheckLegendsCommand;
import com.vecoo.legendcontrol.command.LegendControlCommand;
import com.vecoo.legendcontrol.config.DiscordConfig;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.discord.DiscordWebhook;
import com.vecoo.legendcontrol.listener.LegendControlListener;
import com.vecoo.legendcontrol.listener.LegendControlResultListener;
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

import java.io.IOException;

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

        MinecraftForge.EVENT_BUS.register(new LegendControlListener());
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        Pixelmon.EVENT_BUS.register(new LegendControlResultListener());
        Pixelmon.EVENT_BUS.register(new LegendControlListener());
    }

    @Mod.EventHandler
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();

        event.registerServerCommand(new CheckLegendsCommand());
        event.registerServerCommand(new LegendControlCommand());
    }

    @Mod.EventHandler
    public void onFMLServerStopping(FMLServerStoppingEvent event) {
        this.serverService.save(true);
    }

    public void loadConfig() {
        try {
            this.serverConfig = YamlLoader.load(ServerConfig.class, "config/legendcontrol/config.yml", false);
            this.localeConfig = YamlLoader.load(LocaleConfig.class, "config/legendcontrol/locale.yml", false);
            this.discordConfig = YamlLoader.load(DiscordConfig.class, "config/legendcontrol/discord.yml", false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        this.discordWebhook = new DiscordWebhook(this.discordConfig.getWebhookUrl());
    }

    private void loadStorage() {
        this.serverService = new ServerService("%directory%/storage/legendcontrol/", this.server);

        try {
            this.serverService.init();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
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