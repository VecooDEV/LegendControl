package com.vecoo.legendcontrol.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TaskTickListener {

    private static boolean active;
    private static List<Task> tasks = new ArrayList<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Task task : new ArrayList<>(tasks)) {
                task.tick();
                if (task.isExpired()) {
                    tasks.remove(task);
                }
            }
        }
    }

    static void addTask(@Nonnull Task task) {
        if (!active) {
            MinecraftForge.EVENT_BUS.register(TaskTickListener.class);
            active = true;
        }
        tasks.add(task);
    }
}
