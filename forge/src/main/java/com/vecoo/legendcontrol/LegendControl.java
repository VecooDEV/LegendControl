package com.vecoo.legendcontrol;

import com.envyful.api.config.yaml.YamlConfigFactory;
import com.envyful.api.forge.command.ForgeCommandFactory;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.vecoo.legendcontrol.commands.CheckLegendsCommand;
import com.vecoo.legendcontrol.commands.LegendControlCommand;
import com.vecoo.legendcontrol.commands.LegendaryTrustCommand;
import com.vecoo.legendcontrol.config.LegendControlConfig;
import com.vecoo.legendcontrol.config.LegendControlLocale;
import com.vecoo.legendcontrol.listener.LegendarySpawnListener;
import com.vecoo.legendcontrol.listener.PlayerLoggedInListener;
import com.vecoo.legendcontrol.utils.TaskTickListener;
import com.vecoo.legendcontrol.utils.data.UtilsLegendary;
import com.vecoo.legendcontrol.utils.data.UtilsTrust;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@Mod(
        modid = "legendcontrol",
        name = "LegendControl",
        version = "1.0.0",
        acceptableRemoteVersions = "*"
)
public class LegendControl {

    private static LegendControl instance;

    private ForgeCommandFactory commandFactory = new ForgeCommandFactory();

    public static File fileTrust;
    public static File fileLegendary;

    private LegendControlConfig config;
    private LegendControlLocale locale;

    @Mod.EventHandler
    public void onServerStarting(FMLPreInitializationEvent event) throws IOException {
        instance = this;

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(new LegendarySpawnListener());
        Pixelmon.EVENT_BUS.register(new LegendarySpawnListener());

        MinecraftForge.EVENT_BUS.register(new TaskTickListener());
        MinecraftForge.EVENT_BUS.register(new PlayerLoggedInListener());

        File directory = new File(event.getModConfigurationDirectory(), "LegendControl/data");
        if (!directory.exists()) {
            directory.mkdir();
        }

        fileTrust = new File(directory, "trust.json");
        fileLegendary = new File(directory, "legendary.json");
        if (!fileTrust.exists()) {
            fileTrust.createNewFile();
            UtilsTrust.writeNewGSON();
        }
        if (!fileLegendary.exists()) {
            fileLegendary.createNewFile();
            UtilsLegendary.writeNewGSON();
        }
        UtilsTrust.readFromGSON();
        UtilsLegendary.readFromGSON();

        PixelmonConfig.legendarySpawnChance = 100;

        HashMap<String, Integer> dataMap = UtilsLegendary.getDataMap();
        if (dataMap.get("LegendaryChance") == null) {
            LegendarySpawnListener.legendaryChance = getConfig().getBaseChance();
        } else {
            LegendarySpawnListener.legendaryChance = dataMap.get("LegendaryChance");
        }
    }

    public void loadConfig() {
        try {
            this.config = YamlConfigFactory.getInstance(LegendControlConfig.class);
            this.locale = YamlConfigFactory.getInstance(LegendControlLocale.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        this.commandFactory.registerCommand(event.getServer(), new LegendaryTrustCommand());
        this.commandFactory.registerCommand(event.getServer(), new LegendControlCommand());
        this.commandFactory.registerCommand(event.getServer(), new CheckLegendsCommand());
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) throws IOException {
        UtilsLegendary.writeToGSON("LegendaryChance", LegendarySpawnListener.legendaryChance);
    }

    public static LegendControl getInstance() {
        return instance;
    }

    public LegendControlConfig getConfig() {
        return this.config;
    }

    public LegendControlLocale getLocale() {
        return this.locale;
    }
}