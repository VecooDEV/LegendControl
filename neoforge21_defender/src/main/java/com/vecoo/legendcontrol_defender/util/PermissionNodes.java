package com.vecoo.legendcontrol_defender.util;

import com.vecoo.extralib.util.PermissionUtil;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    private static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();

    public static PermissionNode<Boolean> LEGENDARYTRUST_COMMAND;
    public static PermissionNode<Boolean> LEGENDARYTRUST_RELOAD_COMMAND;

    public static void registerPermission(@NotNull PermissionGatherEvent.Nodes event) {
        LEGENDARYTRUST_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.legendarytrust",
                LegendControlDefender.getInstance().getPermissionConfig().isLegendaryTrustCommand());
        LEGENDARYTRUST_RELOAD_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.legendarytrust.reload", false);

        PERMISSION_LIST.add(LEGENDARYTRUST_COMMAND);
        PERMISSION_LIST.add(LEGENDARYTRUST_RELOAD_COMMAND);

        for (PermissionNode<?> node : PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }
}
