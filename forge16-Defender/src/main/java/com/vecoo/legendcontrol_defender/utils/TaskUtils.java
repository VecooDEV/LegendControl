package com.vecoo.legendcontrol_defender.utils;

import com.vecoo.legendcontrol_defender.LegendControlDefender;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class TaskUtils {
    private final Consumer<TaskUtils> consumer;
    private final long interval;
    private long currentIteration;
    private final long iterations;
    private long ticksRemaining;
    private boolean expired;

    private static final List<TaskUtils> tasks = new CopyOnWriteArrayList<>();
    private static final EventHandler eventHandler = new EventHandler();

    private TaskUtils(Consumer<TaskUtils> consumer, long delay, long interval, long iterations) {
        this.consumer = consumer;
        this.interval = interval;
        this.iterations = iterations;
        this.ticksRemaining = delay;
    }

    public boolean isExpired() {
        return expired;
    }

    public void cancel() {
        this.expired = true;
        tasks.remove(this);
    }

    private void tick() {
        if (expired) return;

        if (--ticksRemaining > 0) return;

        consumer.accept(this);
        currentIteration++;

        if (iterations == -1 || currentIteration < iterations) {
            ticksRemaining = interval;
        } else {
            expired = true;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Consumer<TaskUtils> consumer;
        private long delay;
        private long interval;
        private long iterations = 1;

        public Builder execute(@Nonnull Runnable runnable) {
            this.consumer = task -> runnable.run();
            return this;
        }

        public Builder consume(@Nonnull Consumer<TaskUtils> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Builder delay(long delay) {
            if (delay < 0) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Delay must not be below 0");
                return null;
            }
            this.delay = delay;
            return this;
        }

        public Builder interval(long interval) {
            if (interval < 0) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Interval must not be below 0");
                return null;
            }
            this.interval = interval;
            return this;
        }

        public Builder iterations(long iterations) {
            if (iterations < -1) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Iterations must not be below -1");
                return null;
            }
            this.iterations = iterations;
            return this;
        }

        public Builder infinite() {
            return iterations(-1);
        }

        public TaskUtils build() {
            if (consumer == null) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender Consumer must be set");
                return null;
            }
            TaskUtils task = new TaskUtils(consumer, delay, interval, iterations);
            addTask(task);
            return task;
        }
    }

    private static synchronized void addTask(@Nonnull TaskUtils task) {
        if (tasks.isEmpty()) {
            MinecraftForge.EVENT_BUS.register(eventHandler);
        }
        tasks.add(task);
    }

    private static class EventHandler {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                for (TaskUtils task : tasks) {
                    task.tick();
                    if (task.isExpired()) {
                        tasks.remove(task);
                    }
                }
                if (tasks.isEmpty()) {
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
            }
        }
    }
}
