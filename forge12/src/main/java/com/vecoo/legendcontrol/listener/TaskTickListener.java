package com.vecoo.legendcontrol.listener;

import com.vecoo.legendcontrol.util.Task;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TaskTickListener {
    private static boolean active;
    private static List<Task> tasks = new ArrayList<>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Task task : new ArrayList<>(tasks)) {
                task.tick();
                if (task.isExpired()) {
                    tasks.remove(task);
                }
            }
        }
    }

    public static void addTask(@Nonnull Task task) {
        if (!active) {
            MinecraftForge.EVENT_BUS.register(new TaskTickListener());
            active = true;
        }
        tasks.add(task);
    }
}