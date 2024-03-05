package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.vecoo.extraapi.player.UtilPlayer;
import com.vecoo.legendcontrol.commands.CheckLegendsCommand;
import com.vecoo.legendcontrol.commands.LegendControlCommand;
import com.vecoo.legendcontrol.commands.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.listener.LegendControlListener;
import com.vecoo.legendcontrol.providers.LegendaryProvider;
import com.vecoo.legendcontrol.providers.TrustProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LegendControl.MOD_ID)
public class LegendControl {

    public static final String MOD_ID = "legendcontrol";

    private static LegendControl instance;

    private MinecraftServer server;

    private ServerConfig config;
    private LocaleConfig locale;

    private LegendaryProvider legendaryProvider;
    private TrustProvider trustProvider;

    public LegendControl() {
        instance = this;

        MinecraftForge.EVENT_BUS.register(new LegendControlListener());
        Pixelmon.EVENT_BUS.register(new LegendControlListener());

        UtilPlayer.registerPermission(MOD_ID, "command.checkleg");
        UtilPlayer.registerPermission(MOD_ID, "command.ltrust");
        UtilPlayer.registerPermission(MOD_ID, "command.main");

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.legendaryProvider = new LegendaryProvider();
            this.legendaryProvider.init();
            this.trustProvider = new TrustProvider();
            this.trustProvider.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CheckLegendsCommand.register(event.getDispatcher());
        LegendaryTrustCommand.register(event.getDispatcher());
        LegendControlCommand.register(event.getDispatcher());
    }

    public static LegendControl getInstance() {
        return instance;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public ServerConfig getConfig() {
        return instance.config;
    }

    public LocaleConfig getLocale() {
        return this.locale;
    }

    public LegendaryProvider getLegendaryProvider() {
        return this.legendaryProvider;
    }

    public TrustProvider getTrustProvider() {
        return this.trustProvider;
    }
}