package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
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
import com.vecoo.legendcontrol.storage.server.ServerProvider;
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

import java.util.concurrent.ScheduledExecutorService;

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

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ParticleListener());
        MinecraftForge.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new ResultListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new OtherListener());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CheckLegendsCommand.register(event.getDispatcher());
        LegendControlCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();

        PermissionAPI.registerNode("minecraft.command.checkleg", DefaultPermissionLevel.OP, "/checkleg");
        PermissionAPI.registerNode("minecraft.command.checkleg.modify", DefaultPermissionLevel.OP, "Modify version /checkleg");
        PermissionAPI.registerNode("minecraft.command.lc", DefaultPermissionLevel.OP, "/lc");
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        if (config.getDespawnTime() > 0) {
            for (PixelmonEntity pixelmonEntity : LegendarySpawnListener.getLegends()) {
                if (pixelmonEntity.isAlive() && !pixelmonEntity.hasOwner()) {
                    if (pixelmonEntity.battleController != null) {
                        pixelmonEntity.battleController.endBattle();
                    }
                    pixelmonEntity.remove();
                }
            }
        }
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