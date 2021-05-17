package dev.cuteray.arrest.whenarrest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dev.cuteray.arrest.Main;

public class WhenArrested {
	
	Main plugin;
	
	public WhenArrested(Main main) {
		plugin = main;
		main.WhenArrested = this;
	}
	
	public void ArrestPlayer(Player p, Player arrestedP) {
		if (p.hasPermission("arrest.canArrest")) {
			if (!arrestedP.getUniqueId().equals(p.getUniqueId())) {
				String arrestedUUID = arrestedP.getUniqueId().toString().toLowerCase();
				boolean isTargetArrested = plugin.config.getBoolean("ArrestedPlayers." + arrestedUUID + ".isArrested");
				if (!isTargetArrested) {
					p.sendMessage(plugin.prefix + ChatColor.GREEN + "You have succesfully arrested " + ChatColor.DARK_GREEN + ChatColor.BOLD + arrestedP.getDisplayName());
					arrestedP.sendMessage(plugin.prefix + ChatColor.RED + "You have been arrested by " + ChatColor.GOLD + ChatColor.BOLD + p.getDisplayName());
					plugin.config.set("ArrestedPlayers." + arrestedUUID + ".isArrested", true);
					plugin.config.set("ArrestedPlayers." + arrestedUUID + ".arrestGuy", p);
					plugin.config.set("Arresters." + p.getUniqueId().toString().toLowerCase() + ".arrested", arrestedP);
					plugin.saveConfig();
					return;
				}
				else {
					p.sendMessage(plugin.prefix + ChatColor.RED + "You have succesfully released " + ChatColor.GOLD + ChatColor.BOLD + arrestedP.getDisplayName());
					arrestedP.sendMessage(plugin.prefix + ChatColor.GREEN + "You have been released by " + ChatColor.DARK_GREEN + ChatColor.BOLD + p.getDisplayName());
					plugin.config.set("ArrestedPlayers." + arrestedUUID + ".arrestGuy", null);
					plugin.config.set("ArrestedPlayers." + arrestedUUID + ".isArrested", false);
					plugin.config.set("Arresters." + p.getUniqueId().toString().toLowerCase() + ".arrested", null);
					plugin.saveConfig();
					return;
				}
			}
			else {
				p.sendMessage(plugin.prefix + ChatColor.RED + "You cannot arrest/release yourself.");
				return;
			}
		}
		else {
			p.sendMessage(plugin.prefix + ChatColor.RED + "You do not have the permission to arrest.");
			return;
		}
	}
}
