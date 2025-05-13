package com.vecoo.legendcontrol.api;

public class LegendSourceName {
    public LegendSourceName(String name) {
        this.id = name;
    }

    private final String id;
    public static final LegendSourceName PIXELMON = new LegendSourceName("pixelmon");
    public static final LegendSourceName PLAYER_AND_CONSOLE = new LegendSourceName("player_and_console");

    public String getId() {
        return this.id;
    }
}
