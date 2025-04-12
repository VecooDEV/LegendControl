package com.vecoo.legendcontrol_defender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

import java.util.Set;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ltrust")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.ltrust"))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (String nick : s.getSource().getOnlinePlayerNames()) {
                                        if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(nick);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeAdd(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (UUID uuid : LegendControlFactory.PlayerProvider.getPlayersTrust(s.getSource().getPlayerOrException().getUUID())) {
                                        String name = UtilPlayer.getPlayerName(uuid);
                                        if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(name);
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

    private static int executeAdd(ServerPlayerEntity player, String target) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getCantSelfTrust()), Util.NIL_UUID);
            return 0;
        }

        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());

        if (trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getAlreadyTrusted()), Util.NIL_UUID);
            return 0;
        }

        if (LegendControlDefender.getInstance().getConfig().getTrustLimit() > 0 && trustedPlayers.size() >= LegendControlDefender.getInstance().getConfig().getTrustLimit()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTrustLimit()), Util.NIL_UUID);
            return 0;
        }

        LegendControlFactory.PlayerProvider.addPlayerTrust(player.getUUID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemove(ServerPlayerEntity player, String target) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (!UtilPlayer.hasUUID(target)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotPlayerTrust()), Util.NIL_UUID);
            return 0;
        }

        LegendControlFactory.PlayerProvider.removePlayerTrust(player.getUUID(), targetUUID);

        player.sendMessage(UtilChat.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveAll(ServerPlayerEntity player) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        LegendControlFactory.PlayerProvider.removePlayersTrust(player.getUUID());

        player.sendMessage(UtilChat.formatMessage(localeConfig.getRemoveAllTrust()), Util.NIL_UUID);
        return 1;
    }

    private static int executeList(ServerPlayerEntity player) {
        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (trustedPlayers.isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getListTrust()), Util.NIL_UUID);

        for (UUID uuid : trustedPlayers) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTrustedPlayers()
                    .replace("%player%", UtilPlayer.getPlayerName(uuid))), Util.NIL_UUID);
        }
        return 1;
    }

    private static int executeReload(CommandSource source) {
        LegendControlDefender.getInstance().loadConfig();
        LegendControlDefender.getInstance().loadStorage();

        source.sendSuccess(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getReload()), false);
        return 1;
    }
}