package com.vecoo.legendcontrol;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.vecoo.legendcontrol.commands.CheckLegendsCommand;
import com.vecoo.legendcontrol.commands.LegendControlCommand;
import com.vecoo.legendcontrol.commands.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.LocaleConfig;
import com.vecoo.legendcontrol.config.PermissionsConfig;
import com.vecoo.legendcontrol.config.ServerConfig;
import com.vecoo.legendcontrol.listener.LegendaryListener;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import com.vecoo.legendcontrol.storage.player.PlayerProvider;
import com.vecoo.legendcontrol.task.ParticleTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LegendControl.MOD_ID)
public class LegendControl {
    public static final String MOD_ID = "legendcontrol";
    private static final Logger LOGGER = LogManager.getLogger("LegendControl");

    private static LegendControl instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private PermissionsConfig permissions;

    private PlayerProvider playerProvider;
    private ServerProvider serverProvider;

    public LegendControl() {
        instance = this;

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ParticleTask());
        Pixelmon.EVENT_BUS.register(new LegendaryListener());
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
            this.permissions = YamlConfigFactory.getInstance(PermissionsConfig.class);
            this.playerProvider = new PlayerProvider();
            this.playerProvider.init();
            this.serverProvider = new ServerProvider();
            this.serverProvider.init();
        } catch (Exception e) {
            LOGGER.error("Error load config.");
        }
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

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getConfig() {
        return instance.config;
    }

    public LocaleConfig getLocale() {
        return instance.locale;
    }

    public PermissionsConfig getPermissions() {
        return instance.permissions;
    }

    public ServerProvider getServerProvider() {
        return instance.serverProvider;
    }

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }
}