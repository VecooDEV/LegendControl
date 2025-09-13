package com.vecoo.legendcontrol_defender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.util.PermissionNodes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ltrust")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.LEGENDARYTRUST_COMMAND))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (String playerName : s.getSource().getOnlinePlayerNames()) {
                                        if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(playerName);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeAdd(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (UUID playerUUID : LegendControlFactory.PlayerProvider.getPlayersTrust(s.getSource().getPlayerOrException().getUUID())) {
                                        String name = UtilPlayer.getPlayerName(playerUUID);
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
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.LEGENDARYTRUST_RELOAD_COMMAND))
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeAdd(ServerPlayer player, String target) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (!UtilPlayer.hasUUID(target)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getCantSelfTrust()));
            return 0;
        }

        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());

        if (trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getAlreadyTrusted()));
            return 0;
        }

        if (LegendControlDefender.getInstance().getConfig().getTrustLimit() > 0 && trustedPlayers.size() >= LegendControlDefender.getInstance().getConfig().getTrustLimit()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getTrustLimit()));
            return 0;
        }

        LegendControlFactory.PlayerProvider.addPlayerTrust(player.getUUID(), targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemove(ServerPlayer player, String target) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (!UtilPlayer.hasUUID(target)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(target);
        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotPlayerTrust()));
            return 0;
        }

        LegendControlFactory.PlayerProvider.removePlayerTrust(player.getUUID(), targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemoveAll(ServerPlayer player) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        LegendControlFactory.PlayerProvider.removePlayersTrust(player.getUUID());

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getRemoveAllTrust()));
        return 1;
    }

    private static int executeList(ServerPlayer player) {
        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocale();

        if (trustedPlayers.isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getListTrust()));

        for (UUID playerUUID : trustedPlayers) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getTrustedPlayers()
                    .replace("%player%", UtilPlayer.getPlayerName(playerUUID))));
        }
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        LegendControlDefender.getInstance().loadConfig();

        source.sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocale().getReload()));
        return 1;
    }
}