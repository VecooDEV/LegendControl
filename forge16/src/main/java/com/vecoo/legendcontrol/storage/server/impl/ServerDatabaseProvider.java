package com.vecoo.legendcontrol.storage.server.impl;

import com.vecoo.extralib.database.UtilDatabase;
import com.vecoo.legendcontrol.LegendControl;
import com.vecoo.legendcontrol.storage.server.ServerProvider;
import com.vecoo.legendcontrol.storage.server.ServerStorage;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class ServerDatabaseProvider implements ServerProvider {
    private ServerStorage serverStorage;

    public ServerDatabaseProvider() {
    }

    @Override
    public ServerStorage getServerStorage() {
        if (this.serverStorage == null) {
            new ServerStorage(LegendControl.getInstance().getConfig().getBaseChance(), "None");
        }

        return this.serverStorage;
    }

    @Override
    public void updateServerStorage(ServerStorage storage) {
        this.serverStorage = storage;

        write(storage).thenAccept(success -> {
            if (!success) {
                init();
                LegendControl.getLogger().error("[LegendControl] Failed to write ServerStorage, attempting reload...");
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> write(ServerStorage storage) {
        UtilDatabase database = LegendControl.getInstance().getDatabase();

        return database.supplyAsync(() -> {
            try (Connection connection = database.getDataSource().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("REPLACE INTO legendcontrol (id, chance_legend, last_legend) VALUES (1, ?, ?)")) {

                preparedStatement.setFloat(1, storage.getChanceLegend());
                preparedStatement.setString(2, storage.getLastLegend());
                preparedStatement.executeUpdate();
                return true;
            } catch (SQLException e) {
                LegendControl.getLogger().error("[LegendControl] Error update ServerStorage.", e);
                return false;
            }
        });
    }

    @Override
    public void init() {
        try (Connection connection = LegendControl.getInstance().getDatabase().getDataSource().getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE TABLE IF NOT EXISTS legendcontrol (id INT PRIMARY KEY, chance_legend FLOAT NOT NULL, last_legend VARCHAR(36) NOT NULL)");

            try (ResultSet resultSet = statement.executeQuery("SELECT chance_legend, last_legend FROM legendcontrol WHERE id = 1")) {
                while (resultSet.next()) {
                    this.serverStorage = new ServerStorage(resultSet.getFloat("chance_legend"), resultSet.getString("last_legend"));
                }
            }
        } catch (SQLException e) {
            LegendControl.getLogger().error("[LegendControl] Error initializing ServerStorage.", e);
        }
    }
}
