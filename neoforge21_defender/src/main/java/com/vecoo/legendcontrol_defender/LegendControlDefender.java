package com.vecoo.legendcontrol_defender;

import com.mojang.logging.LogUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.loader.YamlLoader;
import com.vecoo.legendcontrol_defender.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol_defender.config.DiscordConfig;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.config.PermissionConfig;
import com.vecoo.legendcontrol_defender.config.ServerConfig;
import com.vecoo.legendcontrol_defender.discord.DiscordWebhook;
import com.vecoo.legendcontrol_defender.listener.DefenderListener;
import com.vecoo.legendcontrol_defender.service.PlayerService;
import com.vecoo.legendcontrol_defender.util.PermissionNodes;
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

import java.io.IOException;

@Mod(LegendControlDefender.MOD_ID)
public class LegendControlDefender {
    public static final String MOD_ID = "legendcontrol_defender";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static LegendControlDefender instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;
    private DiscordConfig discordConfig;
    private PermissionConfig permissionConfig;

    private PlayerService playerService;

    private MinecraftServer server;

    private DiscordWebhook discordWebhook;

    public LegendControlDefender() {
        instance = this;

        loadConfig();

        NeoForge.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(new DefenderListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.registerPermission(event);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LegendaryTrustCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        this.playerService.save(true);
    }

    public void loadConfig() {
        try {
            this.serverConfig = YamlLoader.load(ServerConfig.class, "config/legendcontrol/defender/config.yml", false);
            this.localeConfig = YamlLoader.load(LocaleConfig.class, "config/legendcontrol/defender/locale.yml", false);
            this.discordConfig = YamlLoader.load(DiscordConfig.class, "config/legendcontrol/defender/discord.yml", false);
            this.permissionConfig = YamlLoader.load(PermissionConfig.class, "config/legendcontrol/defender/permissions.yml", false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        this.discordWebhook = new DiscordWebhook(this.discordConfig.getWebhookUrl());
    }

    public void loadStorage() {
        this.playerService = new PlayerService("%directory%/storage/legendcontrol/defender/players/", this.server);

        try {
            this.playerService.init();
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

    public PermissionConfig getPermissionConfig() {
        return instance.permissionConfig;
    }

    public PlayerService getPlayerService() {
        return instance.playerService;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }

    public DiscordWebhook getDiscordWebhook() {
        return instance.discordWebhook;
    }
}