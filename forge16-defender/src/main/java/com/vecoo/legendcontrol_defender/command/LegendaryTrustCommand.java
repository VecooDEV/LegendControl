package com.vecoo.legendcontrol_defender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.util.CommandUtil;
import com.vecoo.extralib.util.PermissionUtil;
import com.vecoo.extralib.util.PlayerUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.service.LegendControlService;
import lombok.val;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

import javax.annotation.Nonnull;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ltrust")
                .requires(p -> PermissionUtil.hasPermission(p, "minecraft.command.ltrust"))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(CommandUtil.suggestOnlinePlayers())
                                .executes(e -> executeAdd(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (UUID playerUUID : LegendControlService.getPlayersTrust(s.getSource().getPlayerOrException().getUUID())) {
                                        val playerName = PlayerUtil.getPlayerName(playerUUID);

                                        if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(playerName);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeRemove(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"))))
                        .then(Commands.literal("all")
                                .executes(e -> executeRemoveAll(e.getSource().getPlayerOrException()))))

                .then(Commands.literal("list")
                        .executes(e -> executeList(e.getSource().getPlayerOrException())))

                .then(Commands.literal("reload")
                        .requires(p -> PermissionUtil.hasPermission(p, "minecraft.command.ltrust.reload"))
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(@Nonnull ServerPlayerEntity player, @Nonnull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getCantSelfTrust()), Util.NIL_UUID);
            return 0;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.contains(targetUUID)) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getAlreadyTrusted()), Util.NIL_UUID);
            return 0;
        }

        if (LegendControlDefender.getInstance().getServerConfig().getTrustLimit() > 0
                && trustedPlayers.size() >= LegendControlDefender.getInstance().getServerConfig().getTrustLimit()) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getTrustLimit()), Util.NIL_UUID);
            return 0;
        }

        if (!LegendControlService.addPlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendMessage(TextUtil.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemove(@Nonnull ServerPlayerEntity player, @Nonnull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getNotPlayerTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!LegendControlService.removePlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendMessage(TextUtil.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveAll(@Nonnull ServerPlayerEntity player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (LegendControlService.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!LegendControlService.clearPlayersTrust(player.getUUID())) {
            return 0;
        }

        player.sendMessage(TextUtil.formatMessage(localeConfig.getRemoveAllTrust()), Util.NIL_UUID);
        return 1;
    }

    private static int executeList(@Nonnull ServerPlayerEntity player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(TextUtil.formatMessage(localeConfig.getListTrust()), Util.NIL_UUID);

        for (UUID playerUUID : trustedPlayers) {
            player.sendMessage(TextUtil.formatMessage(localeConfig.getTrustedPlayers()
                    .replace("%player%", PlayerUtil.getPlayerName(playerUUID))), Util.NIL_UUID);
        }
        return 1;
    }

    private static int executeReload(@Nonnull CommandSource source) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        try {
            LegendControlDefender.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSuccess(TextUtil.formatMessage(localeConfig.getErrorReload()), false);
            LegendControlDefender.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSuccess(TextUtil.formatMessage(localeConfig.getReload()), false);
        return 1;
    }
}