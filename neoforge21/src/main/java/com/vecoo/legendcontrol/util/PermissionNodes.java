package com.vecoo.legendcontrol.util;

import com.vecoo.extralib.util.PermissionUtil;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    private static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();

    public static PermissionNode<Boolean> CHECKLEGENDARY_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.checklegendary", true);
    public static PermissionNode<Boolean> LEGENDCONTROL_COMMAND = PermissionUtil.getPermissionNode("minecraft.command.lc", false);

    public static void registerPermission(@NotNull PermissionGatherEvent.Nodes event) {
        PERMISSION_LIST.add(CHECKLEGENDARY_COMMAND);
        PERMISSION_LIST.add(LEGENDCONTROL_COMMAND);

        for (PermissionNode<?> node : PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }
}
