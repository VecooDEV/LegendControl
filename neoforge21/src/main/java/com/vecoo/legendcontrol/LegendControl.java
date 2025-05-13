package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.vecoo.legendcontrol.command.CheckLegendsCommand;
import com.vecoo.legendcontrol.command.LegendControlCommand;
import com.vecoo.legendcontrol.config.DiscordConfig;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.discord.DiscordWebhook;
import com.vecoo.legendcontrol.listener.*;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import com.vecoo.legendcontrol.util.PermissionNodes;
import com.vecoo.legendcontrol.util.TaskUtils;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod(LegendControl.MOD_ID)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogManager.getLogger("LegendControl");

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private DiscordConfig discord;

    private ServerProvider serverProvider;

    private MinecraftServer server;

    private DiscordWebhook webhook;

    public LegendControl() {
        instance = this;

        this.loadConfig();

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new ParticleListener());
        NeoForge.EVENT_BUS.register(new TaskUtils.EventHandler());
        NeoForge.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new OtherListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.permissionList.add(PermissionNodes.CHECKLEGENDARY_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.CHECKLEGENDARY_MODIFY_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.LEGENDCONTROL_COMMAND);

        event.addNodes(new ArrayList<>(PermissionNodes.permissionList));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CheckLegendsCommand.register(event.getDispatcher());
        LegendControlCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.discord = YamlConfigFactory.getInstance(DiscordConfig.class);
        } catch (Exception e) {
            LOGGER.error("[LegendControl] Error load config.", e);
        }
        this.webhook = new DiscordWebhook(discord.getWebhookUrl());
    }

    public void loadStorage() {
        try {
            this.serverProvider = new ServerProvider("/%directory%/storage/LegendControl/", this.server);
            this.serverProvider.init();
        } catch (Exception e) {
            LOGGER.error("[LegendControl] Error load storage.", e);
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

    public DiscordConfig getDiscord() {
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