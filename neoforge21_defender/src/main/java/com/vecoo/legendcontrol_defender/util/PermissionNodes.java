package com.vecoo.legendcontrol_defender.util;

import com.vecoo.extralib.permission.UtilPermission;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    public static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();

    public static PermissionNode<Boolean> LEGENDARYTRUST_COMMAND = UtilPermission.getPermissionNode("minecraft.command.ltrust");
    public static PermissionNode<Boolean> LEGENDARYTRUST_RELOAD_COMMAND = UtilPermission.getPermissionNode("minecraft.command.ltrust.reload");

    public static void registerPermission(@NotNull PermissionGatherEvent.Nodes event) {
        PERMISSION_LIST.add(LEGENDARYTRUST_COMMAND);
        PERMISSION_LIST.add(LEGENDARYTRUST_RELOAD_COMMAND);

        for (PermissionNode<?> node : PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }
}
