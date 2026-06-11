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
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(LegendControl.MOD_ID)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogManager.getLogger();

    @Getter
    private static LegendControl instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;
    private DiscordConfig discordConfig;

    private ServerService serverService;

    private MinecraftServer server;

    private DiscordWebhook discordWebhook;

    public LegendControl() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new LegendControlListener());
        MinecraftForge.EVENT_BUS.register(new LegendControlResultListener());
        Pixelmon.EVENT_BUS.register(new LegendControlListener());
        Pixelmon.EVENT_BUS.register(new LegendControlResultListener());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CheckLegendsCommand.register(event.getDispatcher());
        LegendControlCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();

        PermissionAPI.registerNode("minecraft.command.checkleg", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.checkleg.modify", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.lc", DefaultPermissionLevel.OP, "");
    }

    @SubscribeEvent
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