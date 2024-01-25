package com.vecoo.legendcontrol;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.envyful.api.forge.player.ForgePlayerManager;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.vecoo.legendcontrol.commands.CheckLegendsCommand;
import com.vecoo.legendcontrol.commands.LegendControlCommand;
import com.vecoo.legendcontrol.commands.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import com.vecoo.legendcontrol.providers.LegendaryProvider;
import com.vecoo.legendcontrol.providers.TrustProvider;
import com.vecoo.legendcontrol.util.TaskTickListener;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LegendControl.MOD_ID)
public class LegendControl {

    public static final String MOD_ID = "legendcontrol";

    private static LegendControl instance;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    private ServerConfig config;
    private LocaleConfig locale;

    private LegendaryProvider legendaryProvider;
    private TrustProvider trustProvider;

    public LegendControl() {
        instance = this;

        MinecraftForge.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());

        MinecraftForge.EVENT_BUS.register(new TaskTickListener());

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
        LegendarySpawnListener.legendaryChance = legendaryProvider.getLegendaryChance().getLegendaryChance();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        this.commandFactory.registerCommand(event.getDispatcher(), new CheckLegendsCommand());
        this.commandFactory.registerCommand(event.getDispatcher(), new LegendControlCommand());
        this.commandFactory.registerCommand(event.getDispatcher(), new LegendaryTrustCommand());
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        legendaryProvider.getLegendaryChance().setLegendaryChance(LegendarySpawnListener.legendaryChance);
    }

    public static LegendControl getInstance() {
        return instance;
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