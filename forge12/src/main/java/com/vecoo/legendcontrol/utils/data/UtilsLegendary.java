package com.vecoo.legendcontrol.utils.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vecoo.legendcontrol.LegendControl;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class UtilsLegendary {

    static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static HashMap<String, Integer> dataMap;

    public static void writeNewGSON() throws IOException {
        FileWriter gsonWriter = new FileWriter(LegendControl.fileLegendary);
        DataLegendary datajson = new DataLegendary();
        HashMap<String, Integer> dataInfoMap = new HashMap<>();
        datajson.setDataLegendary(dataInfoMap);
        gsonWriter.write(gson.toJson(datajson));
        gsonWriter.close();
    }

    public static void readFromGSON() throws IOException {
        FileReader gsonReader = new FileReader(LegendControl.fileLegendary);
        dataMap = gson.fromJson(gsonReader, DataLegendary.class).getDataLegendary();
        gsonReader.close();
    }

    public static HashMap<String, Integer> getDataMap() throws IOException {
        FileReader gsonReader = new FileReader(LegendControl.fileLegendary);
        dataMap = gson.fromJson(gsonReader, DataLegendary.class).getDataLegendary();
        gsonReader.close();
        return dataMap;
    }

    public static void writeToGSON(String argument, Integer num) throws IOException {
        FileWriter gsonWriter = new FileWriter(LegendControl.fileLegendary);
        FileReader gsonReader = new FileReader(LegendControl.fileLegendary);
        DataLegendary data = gson.fromJson(gsonReader, DataLegendary.class);
        if (data != null) dataMap = data.getDataLegendary();
        HashMap<String, Integer> dataInfoMap;
        if (dataMap != null) {
            dataInfoMap = dataMap;
        } else {
            dataInfoMap = new HashMap<>();
        }
        DataLegendary datajson = new DataLegendary();

        dataInfoMap.put(argument, num);
        datajson.setDataLegendary(dataInfoMap);
        gsonWriter.write(gson.toJson(datajson));
        gsonWriter.close();
    }
}
