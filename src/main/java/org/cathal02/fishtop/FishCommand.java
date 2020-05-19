package org.cathal02.fishtop;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;

import javax.print.DocFlavor;

public class FishCommand implements CommandExecutor {

    public Fishtop  plugin;

    public FishCommand(Fishtop instance)
    {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "You must be a player to peform this command!");
            return true;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("fishtop.use"))
        {

            //TODO: Message in config
            player.sendMessage(ChatColor.RED+ " You do not have permission to use this command");
            return true;
        }

       new GUIHandler(plugin).openGUI(player);
        return true;
    }
}
