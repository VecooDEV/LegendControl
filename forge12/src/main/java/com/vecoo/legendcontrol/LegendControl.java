package com.vecoo.legendcontrol;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.vecoo.legendcontrol.commands.CheckLegendsCommand;
import com.vecoo.legendcontrol.commands.LegendControlCommand;
import com.vecoo.legendcontrol.commands.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.listener.LegendControlListener;
import com.vecoo.legendcontrol.storage.player.PlayerProvider;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
        modid = "legendcontrol",
        name = "LegendControl",
        version = "1.3.0",
        acceptableRemoteVersions = "*"
)
public class LegendControl {
    private static LegendControl instance;

    private MinecraftServer server;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private ServerConfig config;
    private LocaleConfig locale;

    private PlayerProvider playerProvider;
    private ServerProvider serverProvider;

    @Mod.EventHandler
    public void onPreInitialization(FMLPreInitializationEvent event) {
        instance = this;

        this.loadConfig();

        PixelmonConfig.legendarySpawnChance = 100;

        MinecraftForge.EVENT_BUS.register(new LegendControlListener());
        Pixelmon.EVENT_BUS.register(new LegendControlListener());
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.playerProvider = new PlayerProvider();
            this.playerProvider.init();
            this.serverProvider = new ServerProvider();
            this.serverProvider.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        this.commandFactory.registerCommand(event.getServer(), new LegendaryTrustCommand());
        this.commandFactory.registerCommand(event.getServer(), new LegendControlCommand());
        this.commandFactory.registerCommand(event.getServer(), new CheckLegendsCommand());
    }

    public static LegendControl getInstance() {
        return instance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public ServerConfig getConfig() {
        return this.config;
    }

    public LocaleConfig getLocale() {
        return this.locale;
    }

    public PlayerProvider getPlayerProvider() {
        return this.playerProvider;
    }

    public ServerProvider getServerProvider() {
        return this.serverProvider;
    }
}