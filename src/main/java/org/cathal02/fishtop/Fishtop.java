package org.cathal02.fishtop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Fishtop extends JavaPlugin {
    public SQLUtil util;
    public HashMap<UUID, GUIHandler> inventories = new HashMap<>();

    @Override
    public void onEnable()
    {
        util = new SQLUtil(this);

        getCommand("fishtop").setExecutor(new FishCommand(this));

        Bukkit.getServer().getPluginManager().registerEvents(new FishListener(this), this);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public String toChatColor(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }





}
