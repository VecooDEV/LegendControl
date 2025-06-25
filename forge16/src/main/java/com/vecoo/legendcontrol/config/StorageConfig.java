package com.vecoo.legendcontrol.config;

import com.pixelmonmod.pixelmon.api.config.api.data.ConfigPath;
import com.pixelmonmod.pixelmon.api.config.api.yaml.AbstractYamlConfig;
import info.pixelmon.repack.org.spongepowered.objectmapping.ConfigSerializable;

@ConfigPath("config/LegendControl/storage.yml")
@ConfigSerializable
public class StorageConfig extends AbstractYamlConfig {
    private String storageType = "JSON";
    private String storagePathJson = "/%directory%/storage/LegendControl/";
    private String address = "127.0.0.1:3306";
    private String database = "minecraft";
    private String username = "root";
    private String password = "password";
    private String prefix = "legendcontrol_";
    private int maxPoolSize = 10;
    private int minimumIdle = 10;
    private long maxLifeTime = 1800000L;
    private long keepAliveTime = 60000L;
    private long connectionTimeout = 5000L;
    private boolean useSsl = true;

    public String getStorageType() {
        return this.storageType;
    }

    public String getStoragePathJson() {
        return this.storagePathJson;
    }

    public String getAddress() {
        return this.address;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public int getMinimumIdle() {
        return this.minimumIdle;
    }

    public long getMaxLifeTime() {
        return this.maxLifeTime;
    }

    public long getKeepAliveTime() {
        return this.keepAliveTime;
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public boolean isUseSSL() {
        return this.useSsl;
    }
}