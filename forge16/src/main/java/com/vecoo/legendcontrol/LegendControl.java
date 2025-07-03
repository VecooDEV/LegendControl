package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.vecoo.extralib.database.UtilDatabase;
import com.vecoo.legendcontrol.command.CheckLegendsCommand;
import com.vecoo.legendcontrol.command.LegendControlCommand;
import com.vecoo.legendcontrol.config.DiscordConfig;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.config.StorageConfig;
import com.vecoo.legendcontrol.discord.DiscordWebhook;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import com.vecoo.legendcontrol.listener.OtherListener;
import com.vecoo.legendcontrol.listener.ParticleListener;
import com.vecoo.legendcontrol.listener.ResultListener;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import com.vecoo.legendcontrol.storage.server.impl.ServerDatabaseProvider;
import com.vecoo.legendcontrol.storage.server.impl.ServerJsonProvider;
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

@Mod(LegendControl.MOD_ID)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogManager.getLogger("LegendControl");

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private StorageConfig storage;
    private DiscordConfig discord;

    private ServerProvider serverProvider;

    private MinecraftServer server;

    private UtilDatabase database;
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
        if (this.database != null) {
            this.database.close();
        }
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.storage = YamlConfigFactory.getInstance(StorageConfig.class);
            this.discord = YamlConfigFactory.getInstance(DiscordConfig.class);
            this.webhook = new DiscordWebhook(this.discord.getWebhookUrl());
        } catch (Exception e) {
            LOGGER.error("[LegendControl] Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            if (this.storage.getStorageType().equalsIgnoreCase("JSON")) {
                this.serverProvider = new ServerJsonProvider(this.storage.getStoragePathJson(), this.server);
            } else if (this.database == null) {
                this.database = new UtilDatabase(this.storage.getStorageType(), this.storage.getAddress(), this.storage.getDatabase(), this.storage.getUsername(), this.storage.getPassword(),
                        this.storage.getPrefix(), this.storage.getMaxPoolSize(), this.storage.getMinimumIdle(), this.storage.getMaxLifeTime(), this.storage.getKeepAliveTime(), this.storage.getConnectionTimeout(), this.storage.isUseSSL(), this.storage.getThreadPool());

                this.serverProvider = new ServerDatabaseProvider();
            }

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

    public StorageConfig getStorageConfig() {
        return instance.storage;
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

    public UtilDatabase getDatabase() {
        return instance.database;
    }

    public DiscordWebhook getWebhook() {
        return instance.webhook;
    }
}