package com.vecoo.legendcontrol_defender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.extralib.server.UtilCommand;
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
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.ltrust"))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(UtilCommand.suggestOnlinePlayers())
                                .executes(e -> executeAdd(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))))

                .then(Commands.literal("remove")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (UUID playerUUID : LegendControlService.getPlayersTrust(s.getSource().getPlayerOrException().getUUID())) {
                                        val playerName = UtilPlayer.getPlayerName(playerUUID);

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
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.ltrust.reload"))
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(@Nonnull ServerPlayerEntity player, @Nonnull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getCantSelfTrust()), Util.NIL_UUID);
            return 0;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getAlreadyTrusted()), Util.NIL_UUID);
            return 0;
        }

        if (LegendControlDefender.getInstance().getServerConfig().getTrustLimit() > 0
            && trustedPlayers.size() >= LegendControlDefender.getInstance().getServerConfig().getTrustLimit()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTrustLimit()), Util.NIL_UUID);
            return 0;
        }

        if (!LegendControlService.addPlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemove(@Nonnull ServerPlayerEntity player, @Nonnull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotPlayerTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!LegendControlService.removePlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveAll(@Nonnull ServerPlayerEntity player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (LegendControlService.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!LegendControlService.clearPlayersTrust(player.getUUID())) {
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getRemoveAllTrust()), Util.NIL_UUID);
        return 1;
    }

    private static int executeList(@Nonnull ServerPlayerEntity player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getListTrust()), Util.NIL_UUID);

        for (UUID playerUUID : trustedPlayers) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTrustedPlayers()
                    .replace("%player%", UtilPlayer.getPlayerName(playerUUID))), Util.NIL_UUID);
        }
        return 1;
    }

    private static int executeReload(@Nonnull CommandSource source) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        try {
            LegendControlDefender.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getErrorReload()), false);
            LegendControlDefender.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getReload()), false);
        return 1;
    }
}