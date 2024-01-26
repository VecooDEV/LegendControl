package com.vecoo.legendcontrol.utils.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vecoo.legendcontrol.LegendControl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UtilsTrust {

    static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HashMap<UUID, List<UUID>> dataMap;

    public static void writeNewGSON() throws IOException {
        FileWriter gsonWriter = new FileWriter(LegendControl.fileTrust);
        DataTrust datajson = new DataTrust();
        HashMap<UUID, List<UUID>> dataInfoMap = new HashMap<>();
        datajson.setDataTrust(dataInfoMap);
        gsonWriter.write(gson.toJson(datajson));
        gsonWriter.close();
    }

    public static void readFromGSON() throws IOException {
        FileReader gsonReader = new FileReader(LegendControl.fileTrust);
        dataMap = gson.fromJson(gsonReader, DataTrust.class).getDataTrust();
        gsonReader.close();
    }

    public static HashMap<UUID, List<UUID>> getDataMap() throws IOException {
        FileReader gsonReader = new FileReader(LegendControl.fileTrust);
        dataMap = gson.fromJson(gsonReader, DataTrust.class).getDataTrust();
        gsonReader.close();
        return dataMap;
    }

    public static void writeToGSON(UUID playerUUID, UUID newUUID, boolean removeUUID) throws IOException {
        FileWriter gsonWriter = new FileWriter(LegendControl.fileTrust);
        FileReader gsonReader = new FileReader(LegendControl.fileTrust);
        DataTrust data = gson.fromJson(gsonReader, DataTrust.class);
        if (data != null) dataMap = data.getDataTrust();
        HashMap<UUID, List<UUID>> dataInfoMap;
        if (dataMap != null) {
            dataInfoMap = dataMap;
        } else {
            dataInfoMap = new HashMap<>();
        }
        DataTrust datajson = new DataTrust();

        List<UUID> uuidList;
        if (dataInfoMap.get(playerUUID) == null) {
            uuidList = new ArrayList<>();

        } else {
            uuidList = dataInfoMap.get(playerUUID);
        }


        if (newUUID != null && !removeUUID) {
            uuidList.add(newUUID);
        }
        if (removeUUID) {
            uuidList.remove(newUUID);
        }
        dataInfoMap.put(playerUUID, uuidList);
        datajson.setDataTrust(dataInfoMap);
        gsonWriter.write(gson.toJson(datajson));
        gsonWriter.close();
    }
}
