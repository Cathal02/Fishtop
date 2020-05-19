package org.cathal02.fishtop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GUIHandler {

    public Fishtop plugin;
    Inventory inventory;
    List<PlayerData> cachedData;

    public int currentPage;

    public GUIHandler(Fishtop instance)
    {
        plugin = instance;
    }

    public void openGUI(Player player)
    {
        //TODO: Message in config
        inventory = Bukkit.createInventory(player, 27, plugin.toChatColor(plugin.getConfig().getString("inventoryName")));
        if(!plugin.inventories.containsKey(player.getUniqueId()))
        {
            plugin.inventories.put(player.getUniqueId(), this);
            cachedData = plugin.util.getFishCaughtLeaderboard(player);
        }

        setupCallerItem(player);
        updateGUI(0);
        player.openInventory(inventory);

    }

    private void setupCallerItem(Player player) {
        int callerSlot = 22;
        PlayerData data = cachedData.get(cachedData.size()-1);

        if(data.uuid.equalsIgnoreCase(player.getUniqueId().toString()))
        {
            ItemStack skull = getSkullItem(data.rank, data);
            inventory.setItem(callerSlot, skull);
            cachedData.remove(data);
        }

    }

    public void updateGUI(int currentPage)
    {
        this.currentPage = currentPage;
        boolean lastPage = false;

        for(int i = 0; i < 9; i++ )
        {
            int currentDataIndex = i+(currentPage*9);
            ItemStack skull = new ItemStack(Material.SKULL_ITEM);

            System.out.println(currentDataIndex);
            if(currentDataIndex <= cachedData.size()-1)
            {
                PlayerData data = cachedData.get(currentDataIndex);
                skull = getSkullItem(data.rank, data);

            }
            else
            {
                ItemMeta meta = skull.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "No Player");
                skull.setItemMeta(meta);
                lastPage = true;
            }

            inventory.setItem(i, skull);
        }

        createArrows(lastPage);
    }

    private void createArrows( boolean lastPage) {
        ItemStack leftArrow = new ItemStack(Material.ARROW);
        ItemStack rightArrow= new ItemStack(Material.ARROW);

        if(currentPage == 0)
        {
            leftArrow = new ItemStack(Material.BARRIER);
            leftArrow.setItemMeta(getNoMorePagesLeft(leftArrow));
        }
        else
        {
            ItemMeta meta = leftArrow.getItemMeta();;
            String displayName = plugin.getConfig().getString("leftArrowDisplayName").replaceAll("%pageNumber", Integer.toString(currentPage));
            meta.setDisplayName(plugin.toChatColor(displayName));
            leftArrow.setItemMeta(meta);
        }

        if(lastPage)
        {
            rightArrow = new ItemStack(Material.BARRIER);
            rightArrow.setItemMeta(getNoMorePagesLeft(rightArrow));
        }
        else
        {
            ItemMeta meta = rightArrow.getItemMeta();
            String displayName = plugin.getConfig().getString("rightArrowDisplayName").replaceAll("%pageNumber", Integer.toString(currentPage+2));
            meta.setDisplayName(plugin.toChatColor(displayName));
            rightArrow.setItemMeta(meta);
        }

        inventory.setItem(21, leftArrow);

        inventory.setItem(23, rightArrow);
    }

    private ItemMeta getNoMorePagesLeft(ItemStack arrow) {
        ItemMeta meta = arrow.getItemMeta();
        String displayName = ChatColor.RED + "" +  ChatColor.BOLD + "Invalid Page.";
        meta.setDisplayName(displayName);

        return meta;
    }

    private  ItemStack getSkullItem(Integer rank, PlayerData data)
    {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM);
        skull.setDurability((short)3);

        String displayName = plugin.getConfig().getString("itemDisplayName");
        if(displayName != null && data.name != null)
        {
            displayName = displayName.replaceAll("%playername", data.name);
            displayName = plugin.toChatColor(displayName);
        }
        else
        {
            displayName = "";
        }

        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.setOwner(data.name);
        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
        List<String> itemLore = plugin.getConfig().getStringList("itemLore");

        if(itemLore.size() > 0)
        {
           for(String text : itemLore)
           {
               text = text.replaceAll("%rank", rank.toString());
               text = text.replaceAll("%fishCaught", data.fishCaught.toString());
               lore.add(plugin.toChatColor(text));
           }
        }
        else
        {
            lore.add(ChatColor.DARK_PURPLE + "Rank: " + ChatColor.WHITE + rank);
            lore.add(ChatColor.DARK_PURPLE + "Caught: " + ChatColor.WHITE + data.fishCaught);
        }


        meta.setLore(lore);
        skull.setItemMeta(meta);

        return  skull;
    }






}
