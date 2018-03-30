package com.solution.plug.btcontrol;

/**
 * Created by plug on 31/3/18.
 */

public class Sensor {
    private String data;
    private String name;

    public Sensor(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.data = name;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}