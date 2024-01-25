package com.vecoo.legendcontrol.providers;

import com.vecoo.legendcontrol.LegendControl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerTrust {
    private UUID player;
    private List<UUID> playerList;

    public PlayerTrust(UUID uuid) {
        this.player = uuid;
        this.playerList = new ArrayList<>();
        LegendControl.getInstance().getTrustProvider().updatePlayerTrust(this);
    }

    public UUID getPlayer() {
        return player;
    }

    public List<UUID> getPlayerList() {
        return playerList;
    }

    public void addPlayerList(UUID uuid) {
        playerList.add(uuid);
        LegendControl.getInstance().getTrustProvider().updatePlayerTrust(this);
    }

    public void removePlayerList(UUID uuid) {
        playerList.remove(uuid);
        LegendControl.getInstance().getTrustProvider().updatePlayerTrust(this);
    }
}
