package org.cathal02.fishtop;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
    public Integer fishCaught;
    public String uuid;
    public String name;
    public Integer rank;
    public PlayerData(String uuid, Integer fishCaught, String name, Integer rank)
    {
        this.fishCaught = fishCaught;
        this.uuid = uuid;
        this.name = name;
        this.rank = rank;
    }

}
