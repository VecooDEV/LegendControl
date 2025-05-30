package com.vecoo.legendcontrol_defender;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.api.yaml.YamlConfigFactory;
import com.vecoo.legendcontrol_defender.command.LegendaryTrustCommand;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.config.ServerConfig;
import com.vecoo.legendcontrol_defender.listener.DefenderListener;
import com.vecoo.legendcontrol_defender.storage.player.PlayerProvider;
import com.vecoo.legendcontrol_defender.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(LegendControlDefender.MOD_ID)
public class LegendControlDefender {
    public static final String MOD_ID = "legendcontrol_defender";
    private static final Logger LOGGER = LogManager.getLogger("LegendControl-Defender");

    private static LegendControlDefender instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private PlayerProvider playerProvider;

    private MinecraftServer server;

    public LegendControlDefender() {
        instance = this;

        this.loadConfig();

        NeoForge.EVENT_BUS.register(this);
        Pixelmon.EVENT_BUS.register(new DefenderListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.permissionList.add(PermissionNodes.LEGENDARYTRUST_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.LEGENDARYTRUST_RELOAD_COMMAND);

        for (PermissionNode<?> node : PermissionNodes.permissionList) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LegendaryTrustCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
        this.loadStorage();
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(ServerConfig.class);
            this.locale = YamlConfigFactory.getInstance(LocaleConfig.class);
        } catch (Exception e) {
            LOGGER.error("[LegendControl-Defender] Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            this.playerProvider = new PlayerProvider("/%directory%/storage/LegendControl/Defender/players/", this.server);
            this.playerProvider.init();
        } catch (Exception e) {
            LOGGER.error("[LegendControl-Defender] Error load storage.", e);
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

    public PlayerProvider getPlayerProvider() {
        return instance.playerProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}