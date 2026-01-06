package com.vecoo.legendcontrol;

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
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();

        PermissionAPI.registerNode("minecraft.command.checkleg", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.checkleg.modify", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.lc", DefaultPermissionLevel.OP, "");
    }

    @SubscribeEvent
    public void onFMLServerStopping(FMLServerStoppingEvent event) {
        this.serverService.save();
    }

    public void loadConfig() {
        this.serverConfig = YamlConfigFactory.load(ServerConfig.class, "config/LegendControl/config.yml");
        this.localeConfig = YamlConfigFactory.load(LocaleConfig.class, "config/LegendControl/locale.yml");
        this.discordConfig = YamlConfigFactory.load(DiscordConfig.class, "config/LegendControl/discord.yml");
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