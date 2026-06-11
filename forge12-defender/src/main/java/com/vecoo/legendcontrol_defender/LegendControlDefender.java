package com.vecoo.legendcontrol_defender;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.loader.YamlLoader;
import com.vecoo.legendcontrol_defender.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol_defender.config.DiscordConfig;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.config.ServerConfig;
import com.vecoo.legendcontrol_defender.discord.DiscordWebhook;
import com.vecoo.legendcontrol_defender.listener.DefenderListener;
import com.vecoo.legendcontrol_defender.service.PlayerService;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(modid = LegendControlDefender.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public class    LegendControlDefender {
    public static final String MOD_ID = "legendcontrol_defender";
    private static Logger LOGGER;

    @Getter
    private static LegendControlDefender instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;
    private DiscordConfig discordConfig;

    private PlayerService playerService;

    private MinecraftServer server;

    private DiscordWebhook discordWebhook;

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event) {
        instance = this;
        LOGGER = event.getModLog();

        loadConfig();
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        Pixelmon.EVENT_BUS.register(new DefenderListener());
    }

    @Mod.EventHandler
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();

        event.registerServerCommand(new LegendaryTrustCommand());
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.playerService.save(true);
    }

    public void loadConfig() {
        try {
            this.serverConfig = YamlLoader.load(ServerConfig.class, "config/legendcontrol/defender/config.yml", false);
            this.localeConfig = YamlLoader.load(LocaleConfig.class, "config/legendcontrol/defender/locale.yml", false);
            this.discordConfig = YamlLoader.load(DiscordConfig.class, "config/legendcontrol/defender/discord.yml", false);
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