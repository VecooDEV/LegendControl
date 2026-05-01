package com.vecoo.legendcontrol_defender.command;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.util.CommandUtil;
import com.vecoo.extralib.util.PermissionUtil;
import com.vecoo.extralib.util.PlayerUtil;
import com.vecoo.extralib.util.TextUtil;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.api.service.LegendControlService;
import com.vecoo.legendcontrol_defender.util.PermissionNodes;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LegendaryTrustCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : Sets.newHashSet("legendarytrust", "ltrust")) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.LEGENDARYTRUST_COMMAND))
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
                            .requires(p -> PermissionUtil.hasPermission(p, PermissionNodes.LEGENDARYTRUST_RELOAD_COMMAND))
                            .executes(e -> executeReload(e.getSource()))));
        }
    }

    private static int executeAdd(@NotNull ServerPlayer player, @NotNull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getCantSelfTrust()));
            return 0;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getAlreadyTrusted()));
            return 0;
        }

        val serverConfig = LegendControlDefender.getInstance().getServerConfig();

        if (serverConfig.getTrustLimit() > 0 && trustedPlayers.size() >= serverConfig.getTrustLimit()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getTrustLimit()));
            return 0;
        }

        if (!LegendControlService.addPlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getAddTrust()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemove(@NotNull ServerPlayer player, @NotNull String target) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val targetUUID = PlayerUtil.findUUID(target);

        if (targetUUID == null) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)));
            return 0;
        }

        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        if (!trustedPlayers.contains(targetUUID)) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getNotPlayerTrust()));
            return 0;
        }

        if (!LegendControlService.removePlayerTrust(player.getUUID(), targetUUID)) {
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getRemoveTrust()
                .replace("%player%", target)));
        return 1;
    }

    private static int executeRemoveAll(@NotNull ServerPlayer player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        if (LegendControlService.getPlayersTrust(player.getUUID()).isEmpty()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        if (!LegendControlService.clearPlayersTrust(player.getUUID())) {
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getRemoveAllTrust()));
        return 1;
    }

    private static int executeList(@NotNull ServerPlayer player) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();
        val trustedPlayers = LegendControlService.getPlayersTrust(player.getUUID());

        if (trustedPlayers.isEmpty()) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getEmptyTrust()));
            return 0;
        }

        player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getListTrust()));

        for (UUID playerUUID : trustedPlayers) {
            player.sendSystemMessage(TextUtil.formatMessage(localeConfig.getTrustedPlayers()
                    .replace("%player%", PlayerUtil.getPlayerName(playerUUID))));
        }
        return 1;
    }

    private static int executeReload(@NotNull CommandSourceStack source) {
        val localeConfig = LegendControlDefender.getInstance().getLocaleConfig();

        try {
            LegendControlDefender.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getErrorReload()));
            LegendControlDefender.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSystemMessage(TextUtil.formatMessage(localeConfig.getReload()));
        return 1;
    }
}