package org.land.skycraftclient.db;


import org.land.skycraftclient.SkyPlayer;

public abstract class Skill {
    private String name, description;
    private int id;

    private int defaultCooldown = 0;

    public Skill setDescription(String description) {
        this.description = description;
        return this;
    }

    public Skill setId(int id) {
        this.id = id;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public abstract void onUse(SkyPlayer player);

    public int getDefaultCooldown() {
        return defaultCooldown;
    }

    public Skill setDefaultCooldown(int defaultCooldown) {
        this.defaultCooldown = defaultCooldown;
        return this;
    }

    public Skill setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", defaultCooldown=" + defaultCooldown +
                '}';
    }
}


