package com.route.model;

import lombok.Data;

@Data
public class Stop {
    private String stopId;
    private int x;
    private int y;

    public Stop(String stopId, int x, int y) {
        this.stopId = stopId;
        this.x = x;
        this.y = y;
    }
}
