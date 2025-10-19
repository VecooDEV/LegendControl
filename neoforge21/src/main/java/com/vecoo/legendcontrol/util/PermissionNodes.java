package com.vecoo.legendcontrol.util;

import com.vecoo.extralib.permission.UtilPermission;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    public static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();

    public static PermissionNode<Boolean> CHECKLEGENDARY_COMMAND = UtilPermission.getPermissionNode("minecraft.command.checkleg");
    public static PermissionNode<Boolean> CHECKLEGENDARY_MODIFY_COMMAND = UtilPermission.getPermissionNode("minecraft.command.checkleg.modify");
    public static PermissionNode<Boolean> LEGENDCONTROL_COMMAND = UtilPermission.getPermissionNode("minecraft.command.lc");

    public static void registerPermission(@NotNull PermissionGatherEvent.Nodes event) {
        PERMISSION_LIST.add(CHECKLEGENDARY_COMMAND);
        PERMISSION_LIST.add(CHECKLEGENDARY_MODIFY_COMMAND);
        PERMISSION_LIST.add(LEGENDCONTROL_COMMAND);

        for (PermissionNode<?> node : PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }
}
