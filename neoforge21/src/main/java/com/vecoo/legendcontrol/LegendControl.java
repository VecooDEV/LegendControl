package com.vecoo.legendcontrol;

import com.mojang.logging.LogUtils;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
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
import com.vecoo.legendcontrol.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import org.slf4j.Logger;

@Mod(LegendControl.MOD_ID)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private DiscordConfig discord;

    private ServerProvider serverProvider;

    private MinecraftServer server;

    private DiscordWebhook webhook;

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
        this.serverProvider.write();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.discord = YamlConfigFactory.getInstance(DiscordConfig.class);
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