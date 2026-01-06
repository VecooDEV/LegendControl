package com.vecoo.legendcontrol_defender;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.extralib.config.YamlConfigFactory;
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

@Mod(modid = LegendControlDefender.MOD_ID, acceptableRemoteVersions = "*", useMetadata = true)
public class LegendControlDefender {
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
        this.playerService.save();
    }

    public void loadConfig() {
        this.serverConfig = YamlConfigFactory.load(ServerConfig.class, "config/LegendControl/Defender/config.yml");
        this.localeConfig = YamlConfigFactory.load(LocaleConfig.class, "config/LegendControl/Defender/locale.yml");
        this.discordConfig = YamlConfigFactory.load(DiscordConfig.class, "config/LegendControl/Defender/discord.yml");
        this.discordWebhook = new DiscordWebhook(this.discordConfig.getWebhookUrl());
    }

    private void loadStorage() {
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