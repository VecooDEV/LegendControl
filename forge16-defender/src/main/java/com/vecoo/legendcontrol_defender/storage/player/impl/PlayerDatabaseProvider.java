package com.vecoo.legendcontrol_defender.storage.player.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vecoo.extralib.database.UtilDatabase;
import com.vecoo.legendcontrol_defender.LegendControlDefender;
import com.vecoo.legendcontrol_defender.storage.player.PlayerProvider;
import com.vecoo.legendcontrol_defender.storage.player.PlayerStorage;

import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDatabaseProvider implements PlayerProvider {
    private final Map<UUID, PlayerStorage> map;

    public PlayerDatabaseProvider() {
        this.map = new ConcurrentHashMap<>();
    }

    @Override
    public Map<UUID, PlayerStorage> getMap() {
        return this.map;
    }

    @Override
    public PlayerStorage getPlayerStorage(UUID playerUUID) {
        if (this.map.get(playerUUID) == null) {
            new PlayerStorage(playerUUID, new HashSet<>());
        }

        return this.map.get(playerUUID);
    }

    @Override
    public void updatePlayerStorage(PlayerStorage storage) {
        this.map.put(storage.getUUID(), storage);

        write(storage).thenAccept(success -> {
            if (!success) {
                init();
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Failed to write PlayerStorage, attempting reload...");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> write(PlayerStorage storage) {
        UtilDatabase database = LegendControlDefender.getInstance().getDatabase();

        return database.supplyAsync(() -> {
            try (Connection connection = database.getDataSource().getConnection()) {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO legendcontrol_defender (player_uuid, trusted_player_uuids) VALUES (?, ?) ON DUPLICATE KEY UPDATE trusted_player_uuids = VALUES(trusted_player_uuids)");

                preparedStatement.setString(1, storage.getUUID().toString());
                preparedStatement.setString(2, new Gson().toJson(storage.getPlayersTrust()));
                preparedStatement.executeUpdate();

                return true;
            } catch (SQLException e) {
                LegendControlDefender.getLogger().error("[LegendControl-Defender] Error updating PlayerStorage.", e);
                return false;
            }
        });
    }

    @Override
    public void init() {
        try (Connection connection = LegendControlDefender.getInstance().getDatabase().getDataSource().getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS legendcontrol_defender (player_uuid VARCHAR(36) NOT NULL PRIMARY KEY, trusted_player_uuids JSON NOT NULL)");

            try (ResultSet resultSet = statement.executeQuery("SELECT player_uuid, trusted_player_uuids FROM legendcontrol_defender")) {
                while (resultSet.next()) {
                    UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));

                    this.map.put(playerUUID, new PlayerStorage(playerUUID, new Gson().fromJson(resultSet.getString("trusted_player_uuids"), new TypeToken<Set<UUID>>() {
                    }.getType())));
                }
            }
        } catch (SQLException e) {
            LegendControlDefender.getLogger().error("[LegendControl-Defender] Error initializing PlayerStorage.", e);
        }
    }
}
