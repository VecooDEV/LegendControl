package com.vecoo.legendcontrol.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.server.UtilCommand;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.api.LegendSourceName;
import com.vecoo.legendcontrol.api.service.LegendControlService;
import com.vecoo.legendcontrol.util.Utils;
import lombok.val;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import javax.annotation.Nonnull;

public class LegendControlCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("lc")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.lc"))
                .then(Commands.literal("add")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests(UtilCommand.suggestAmount(Sets.newHashSet(10, 25, 50)))
                                .executes(e -> executeAdd(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests(UtilCommand.suggestAmount(Sets.newHashSet(10, 25, 50)))
                                .executes(e -> executeRemove(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))

                .then(Commands.literal("set")
                        .then(Commands.argument("chance", FloatArgumentType.floatArg(0F, 100F))
                                .suggests(UtilCommand.suggestAmount(Sets.newHashSet(10, 50, 100)))
                                .executes(e -> executeSet(e.getSource(), FloatArgumentType.getFloat(e, "chance")))))

                .then(Commands.literal("reload")
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(@Nonnull CommandSource source, float chance) {
        val localeConfig = LegendControl.getInstance().getLocaleConfig();

        if (LegendControlService.getChanceLegend() + chance > 100F) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getErrorChance()), false);
            return 0;
        }

        if (!LegendControlService.addChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getChangeChanceLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlService.getChanceLegend()))), false);
        return 1;
    }

    private static int executeRemove(@Nonnull CommandSource source, float chance) {
        val localeConfig = LegendControl.getInstance().getLocaleConfig();

        if (LegendControlService.getChanceLegend() - chance < 0F) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getErrorChance()), false);
            return 0;
        }

        if (!LegendControlService.removeChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getChangeChanceLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlService.getChanceLegend()))), false);
        return 1;
    }

    private static int executeSet(@Nonnull CommandSource source, float chance) {
        val localeConfig = LegendControl.getInstance().getLocaleConfig();

        if (!LegendControlService.setChanceLegend(LegendSourceName.PLAYER_AND_CONSOLE, chance)) {
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getChangeChanceLegendary()
                .replace("%chance%", Utils.formatFloat(LegendControlService.getChanceLegend()))), false);
        return 1;
    }

    private static int executeReload(@Nonnull CommandSource source) {
        val localeConfig = LegendControl.getInstance().getLocaleConfig();

        try {
            LegendControl.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getErrorReload()), false);
            LegendControl.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getReload()), false);
        return 1;
    }
}