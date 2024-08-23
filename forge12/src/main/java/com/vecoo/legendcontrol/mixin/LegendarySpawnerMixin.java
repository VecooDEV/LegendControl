package com.vecoo.legendcontrol.mixin;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.RandomHelper;
import com.pixelmonmod.pixelmon.api.events.spawning.LegendarySpawnEvent;
import com.pixelmonmod.pixelmon.api.spawning.SpawnAction;
import com.pixelmonmod.pixelmon.api.spawning.SpawnerCoordinator;
import com.pixelmonmod.pixelmon.api.spawning.archetypes.spawners.TickingSpawner;
import com.pixelmonmod.pixelmon.config.PixelmonConfig;
import com.pixelmonmod.pixelmon.spawning.LegendarySpawner;
import com.pixelmonmod.pixelmon.util.helpers.CollectionHelper;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerFactory;
import com.vecoo.legendcontrol.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(value = LegendarySpawner.class, remap = false)
public abstract class LegendarySpawnerMixin extends TickingSpawner {

    public LegendarySpawnerMixin(String name) {
        super(name);
    }

    @Shadow
    public static void fillNearby(ArrayList<EntityPlayerMP> allPlayers, ArrayList<EntityPlayerMP> cluster, EntityPlayerMP focus) {
        throw new AssertionError();
    }

    @Shadow
    public List<SpawnAction<?>> doLegendarySpawn(EntityPlayerMP target) {
        throw new AssertionError();
    }

    @Shadow
    public List<SpawnAction<?>> possibleSpawns;

    @Shadow
    public boolean firesChooseEvent;

    /**
     * @author Vecoo
     * @reason Change logic choose player.
     */
    @Overwrite
    public void forcefullySpawn(EntityPlayerMP onlyFocus) {
        ArrayList<ArrayList<EntityPlayerMP>> clusters = new ArrayList<>();
        ArrayList<EntityPlayerMP> players = new ArrayList<>(ExtraLib.getInstance().getServer().getPlayerList().getPlayers());
        if (onlyFocus == null) {
            while (!players.isEmpty()) {
                ArrayList<EntityPlayerMP> cluster = new ArrayList<>();
                EntityPlayerMP focus = players.remove(0);
                if (this.firesChooseEvent) {
                    if (!ServerFactory.getPlayersBlacklist().contains(focus.getUniqueID()) && !LegendControl.getInstance().getConfig().getBlockedWorld().contains(focus.getEntityWorld().provider.getDimension())) {
                        Utils.updatePlayerIP(focus);
                        if (!ServerFactory.getPlayersIP().containsValue(focus.getPlayerIP()) || LegendControl.getInstance().getConfig().getMaxPlayersIP() == 0) {
                            if (Utils.playerCountIP(focus) <= LegendControl.getInstance().getConfig().getLockPlayerIP() || LegendControl.getInstance().getConfig().getLockPlayerIP() == 0) {
                                cluster.add(focus);
                                fillNearby(players, cluster, focus);
                                clusters.add(cluster);
                            }
                        }
                    }
                } else {
                    cluster.add(focus);
                    fillNearby(players, cluster, focus);
                    clusters.add(cluster);
                }
            }
        }


        this.isBusy = true;
        SpawnerCoordinator.PROCESSOR.execute(() -> {
            if (onlyFocus != null) {
                this.possibleSpawns = this.doLegendarySpawn(onlyFocus);
            } else {
                Collections.shuffle(clusters);

                while (clusters.size() > 0) {
                    for (int i = 0; i < clusters.size(); ++i) {
                        EntityPlayerMP player = CollectionHelper.getRandomElement(clusters.get(i));
                        (clusters.get(i)).remove(player);
                        if ((clusters.get(i)).isEmpty()) {
                            clusters.remove(i--);
                        }

                        if (this.firesChooseEvent) {
                            LegendarySpawnEvent.ChoosePlayer event = new LegendarySpawnEvent.ChoosePlayer((LegendarySpawner) (Object) this, player, clusters);
                            if (Pixelmon.EVENT_BUS.post(event) || event.player == null) {
                                continue;
                            }

                            player = event.player;
                        }

                        this.possibleSpawns = this.doLegendarySpawn(player);
                        if (this.possibleSpawns != null) {
                            this.isBusy = false;
                            return;
                        }
                    }
                }
            }

            this.isBusy = false;
        });
    }

    /**
     * @author Vecoo
     * @reason Change logic spawn chance.
     */
    @Overwrite
    public List<SpawnAction<? extends Entity>> getSpawns(int pass) {
        if (pass == 0) {
            this.possibleSpawns = null;
            int numPlayers = ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount();
            int baseSpawnTicks = this.firesChooseEvent ? PixelmonConfig.legendarySpawnTicks : PixelmonConfig.bossSpawnTicks;
            int spawnTicks = (int) ((float) baseSpawnTicks / (1.0F + (float) (numPlayers - 1) * PixelmonConfig.spawnTicksPlayerMultiplier));
            this.spawnFrequency = RandomHelper.getRandomNumberBetween(0.6F, 1.4F) * (1200.0F / (float) spawnTicks);
            if (this.firesChooseEvent) {
                Utils.timeDoLegend = RandomHelper.getRandomNumberBetween(LegendControl.getInstance().getConfig().getRandomTimeSpawnMin(), LegendControl.getInstance().getConfig().getRandomTimeSpawnMax());
            }
            if (this.firesChooseEvent && !RandomHelper.getRandomChance(LegendControl.getInstance().getServerProvider().getServerStorage().getLegendaryChance() / 100.0F) || !this.firesChooseEvent && !RandomHelper.getRandomChance(PixelmonConfig.bossSpawnChance)) {
                if (this.firesChooseEvent && ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount() > 0) {
                    ServerFactory.addLegendaryChance(LegendControl.getInstance().getConfig().getStepSpawnChance());
                }
                return null;
            } else {
                if (ExtraLib.getInstance().getServer().getPlayerList().getCurrentPlayerCount() > 0) {
                    this.forcefullySpawn(null);
                }
                return null;
            }
        } else {
            return this.possibleSpawns != null && !this.possibleSpawns.isEmpty() ? this.possibleSpawns : null;
        }
    }
}