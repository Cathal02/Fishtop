package org.cathal02.fishtop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class FishListener implements Listener {
    Fishtop plugin;
    public FishListener(Fishtop fishtop) {
        plugin = fishtop;
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) throws SQLException {
        if(e.getState() == PlayerFishEvent.State.CAUGHT_FISH)
        {
            UUID uuid = e.getPlayer().getUniqueId();

            plugin.util.updateFishCaught(uuid.toString(), e.getPlayer().getName());

        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(e.getView().getTitle().equals(plugin.toChatColor(plugin.getConfig().getString("inventoryName"))))
        {
            e.setCancelled(true);
            if(e.getRawSlot() == 23 && e.getCurrentItem().getType().equals(Material.ARROW))
            {
                if(plugin.inventories.containsKey(e.getWhoClicked().getUniqueId()))
                {
                    GUIHandler handler = plugin.inventories.get(e.getWhoClicked().getUniqueId());
                    handler.updateGUI(handler.currentPage+1);
                }
            }

            if(e.getRawSlot() == 21 && e.getCurrentItem().getType().equals(Material.ARROW))
            {
                if(plugin.inventories.containsKey(e.getWhoClicked().getUniqueId()))
                {
                    GUIHandler handler = plugin.inventories.get(e.getWhoClicked().getUniqueId());
                    handler.updateGUI(handler.currentPage-1);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e)
    {
        if(e.getView().getTitle().equals(plugin.toChatColor(plugin.getConfig().getString("inventoryName"))))
        {
            plugin.inventories.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        plugin.util.updateFishCaught(e.getPlayer().getUniqueId().toString(), e.getPlayer().getName());
    }


}
