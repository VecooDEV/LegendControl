package com.vecoo.legendcontrol_defender;

import com.mojang.logging.LogUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.config.YamlConfigFactory;
import com.vecoo.legendcontrol_defender.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol_defender.config.DiscordConfig;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
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

import java.nio.file.Path;

@Mod(LegendControlDefender.MOD_ID)
public class LegendControlDefender {
    public static final String MOD_ID = "legendcontrol_defender";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static LegendControlDefender instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;
    private DiscordConfig discordConfig;

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
        this.playerService.save();
    }

    public void loadConfig() {
        this.serverConfig = com.vecoo.extralib.config.YamlConfigFactory.load(ServerConfig.class, Path.of("config/LegendControl/Defender/config.yml"));
        this.localeConfig = com.vecoo.extralib.config.YamlConfigFactory.load(LocaleConfig.class, Path.of("config/LegendControl/Defender/locale.yml"));
        this.discordConfig = YamlConfigFactory.load(DiscordConfig.class, Path.of("config/LegendControl/Defender/discord.yml"));
        this.discordWebhook = new DiscordWebhook(this.discordConfig.getWebhookUrl());
    }

    public void loadStorage() {
        try {
            this.playerService = new PlayerService("/%directory%/storage/LegendControl/Defender/players/", this.server);
            this.playerService.init();
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