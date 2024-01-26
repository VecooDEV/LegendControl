package com.vecoo.legendcontrol.utils.data;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataTrust {

    public HashMap<UUID, List<UUID>> dataTrust = new HashMap<>();

    public HashMap<UUID, List<UUID>> getDataTrust() {
        return dataTrust;
    }

    public void setDataTrust(HashMap<UUID, List<UUID>> data) {
        this.dataTrust = data;
    }
}
