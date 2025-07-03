package com.vecoo.legendcontrol_defender.util;

import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public class PermissionNodes {
    public static List<PermissionNode<?>> PERMISSION_LIST = new ArrayList<>();

    public static PermissionNode<Boolean> LEGENDARYTRUST_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.ltrust",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> LEGENDARYTRUST_RELOAD_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.ltrust.reload",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);
}
