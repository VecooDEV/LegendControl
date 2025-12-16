package com.vecoo.legendcontrol;

import com.mojang.logging.LogUtils;
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
import com.vecoo.legendcontrol.util.PermissionNodes;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(LegendControl.MOD_ID)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogUtils.getLogger();

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

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ParticleListener());
        NeoForge.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new OtherListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.registerPermission(event);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CheckLegendsCommand.register(event.getDispatcher());
        LegendControlCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        this.serverService.save();
    }

    public void loadConfig() {
        this.serverConfig = YamlConfigFactory.load(ServerConfig.class, Path.of("config/LegendControl/config.yml"));
        this.localeConfig = YamlConfigFactory.load(LocaleConfig.class, Path.of("config/LegendControl/locale.yml"));
        this.discordConfig = YamlConfigFactory.load(DiscordConfig.class, Path.of("config/LegendControl/discord.yml"));
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