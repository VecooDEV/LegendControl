package com.vecoo.legendcontrol_defender.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.extralib.server.UtilCommand;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.factory.LegendControlFactory;
import com.vecoo.legendcontrol_defender.config.LocaleConfig;
import com.vecoo.legendcontrol_defender.util.PermissionNodes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ltrust")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.LEGENDARYTRUST_COMMAND))
                .then(Commands.literal("add")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(UtilCommand.suggestOnlinePlayers())
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

    private static int executeAdd(@NotNull ServerPlayer player, @NotNull String target) {
        UUID targetUUID = UtilPlayer.findUUID(target);
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (targetUUID == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

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

        if (!LegendControlFactory.PlayerProvider.addPlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemove(@NotNull ServerPlayer player, @NotNull String target) {
        UUID targetUUID = UtilPlayer.findUUID(target);
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (targetUUID == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotPlayerTrust()));
            return 0;
        }

        if (!LegendControlFactory.PlayerProvider.removePlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemoveAll(@NotNull ServerPlayer player) {
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        if (!LegendControlFactory.PlayerProvider.clearPlayersTrust(player.getUUID())) {
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getRemoveAllTrust()));
        return 1;
    }

    private static int executeList(@NotNull ServerPlayer player) {
        Set<UUID> trustedPlayers = LegendControlFactory.PlayerProvider.getPlayersTrust(player.getUUID());
        LocaleConfig localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

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

    private static int executeReload(@NotNull CommandSourceStack source) {
        LegendControlDefender.getInstance().loadConfig();
        LegendControlDefender.getInstance().loadStorage();

        source.sendSystemMessage(UtilChat.formatMessage(LegendControlDefender.getInstance().getLocaleConfig().getReload()));
        return 1;
    }
}