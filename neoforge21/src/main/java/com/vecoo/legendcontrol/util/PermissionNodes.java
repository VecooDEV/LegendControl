package com.vecoo.legendcontrol.util;

import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public class PermissionNodes {
    public static List<PermissionNode<?>> permissionList = new ArrayList<>();

    public static PermissionNode<Boolean> CHECKLEGENDARY_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.checkleg",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> CHECKLEGENDARY_MODIFY_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.checkleg.modify",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);


    public static PermissionNode<Boolean> LEGENDCONTROL_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.lc",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);
}
