package com.vecoo.legendcontrol_defender;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.vecoo.legendcontrol_defender.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol_defender.config.DiscordConfig;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.config.ServerConfig;
import com.vecoo.legendcontrol_defender.discord.DiscordWebhook;
import com.vecoo.legendcontrol_defender.listener.DefenderListener;
import com.vecoo.legendcontrol_defender.storage.PlayerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(LegendControlDefender.MOD_ID)
public class LegendControlDefender {
    public static final String MOD_ID = "legendcontrol_defender";
    private static final Logger LOGGER = LogManager.getLogger("LegendControl-Defender");

    private static LegendControlDefender instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private DiscordConfig discord;

    private PlayerProvider playerProvider;

    private MinecraftServer server;

    private DiscordWebhook webhook;

    public LegendControlDefender() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(new DefenderListener());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LegendaryTrustCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();

        PermissionAPI.registerNode("minecraft.command.ltrust", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.ltrust.reload", DefaultPermissionLevel.OP, "");
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.playerProvider.write();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.discord = YamlConfigFactory.getInstance(DiscordConfig.class);
            this.webhook = new DiscordWebhook(this.discord.getWebhookUrl());
        } catch (IOException e) {
            LOGGER.error("[LegendControl-Defender] Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            this.playerProvider = new PlayerProvider("/%directory%/storage/LegendControl/Defender/players/", this.server);
            this.playerProvider.init();
        } catch (Exception e) {
            LOGGER.error("[LegendControl] Error load storage.", e);
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

    public DiscordConfig getDiscordConfig() {
        return instance.discord;
    }

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }

    public DiscordWebhook getWebhook() {
        return instance.webhook;
    }
}