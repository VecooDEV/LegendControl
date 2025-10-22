package com.vecoo.legendcontrol.command;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.text.UtilText;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.factory.LegendControlFactory;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class LegendControlCommand extends CommandBase {
    @Override
    @Nonnull
    public String getName() {
        return "lc";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "/lc";
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "set", "reload");
        }

        if (args.length == 2) {
            if (!args[0].equals("reload")) {
                return getListOfStringsMatchingLastWord(args, "10", "20", "30", "40", "50", "60", "70", "80", "90", "100");
            }
        }

        return Collections.emptyList();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) {
        try {
            switch (args[0]) {
                case "add": {
                    executeAdd(sender, Float.parseFloat(args[1]));
                    break;
                }

                case "remove": {
                    if (args[1].matches("\\d+")) {
                        executeRemove(sender, Float.parseFloat(args[1]));
                        break;
                    }
                }

                case "set": {
                    if (args[1].matches("\\d+")) {
                        executeSet(sender, Float.parseFloat(args[1]));
                        break;
                    }
                }

                case "reload": {
                    if (sender.canUseCommand(4, "gamemode")) {
                        executeReload(sender);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            sender.sendMessage(UtilChat.formatMessage("/lc"));
        }
    }

    private static void executeAdd(@Nonnull ICommandSender source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() + chance > 100F) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()));
            return;
        }

        if (!LegendControlFactory.ServerProvider.addChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return;
        }

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
    }

    private static void executeRemove(@Nonnull ICommandSender source, float chance) {
        if (LegendControlFactory.ServerProvider.getChanceLegend() - chance < 0F) {
            source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getErrorChance()));
            return;
        }

        if (!LegendControlFactory.ServerProvider.removeChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return;
        }

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
    }

    private static void executeSet(@Nonnull ICommandSender source, float chance) {
        if (!LegendControlFactory.ServerProvider.setChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return;
        }

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getChangeChanceLegendary()
                .replace("%chance%", UtilText.getFormattedFloat(LegendControlFactory.ServerProvider.getChanceLegend()))));
    }

    private static void executeReload(@Nonnull ICommandSender source) {
        LegendControl.getInstance().loadConfig();
        LegendControl.getInstance().loadStorage();

        source.sendMessage(UtilChat.formatMessage(LegendControl.getInstance().getLocale().getReload()));
    }
}