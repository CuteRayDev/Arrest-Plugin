package dev.cuteray.arrest.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import dev.cuteray.arrest.Main;
import dev.cuteray.arrest.whenarrest.WhenArrested;

public class Command implements CommandExecutor{

	Main plugin;
	WhenArrested wa;
	
	public Command(Main main) {
		plugin = main;
		main.getCommand("arrest").setExecutor(this);
		wa = main.WhenArrested;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length >= 1) {
				switch(args[0].toLowerCase()) {
				case "arrest":
					if (args.length >= 2) {
						Player arrestedP = Bukkit.getPlayer(args[1]);
						if (arrestedP != null) {
							wa.ArrestPlayer(p, arrestedP);
							break;
						}
						else {
							p.sendMessage(plugin.prefix + ChatColor.RED + args[1] + " is not online right now!");
							break;
						}
					}
					else {
						p.sendMessage(plugin.prefix + ChatColor.RED + "Wrong usage: /arrest arrest [Player]");
						break;
					}
				case "handcuff":
					p.sendMessage(plugin.prefix + ChatColor.GREEN + "You have recieved your handcuff! Right click to arrest someone.");
					List<String> handcuffLore = new ArrayList<String>();
					handcuffLore.add(ChatColor.GRAY + "Right click to arrest someone");
					ItemStack handcuff = createItem(new ItemStack(Material.MAGMA_CREAM), ChatColor.GOLD + "Handcuff", handcuffLore);
					p.getInventory().addItem(handcuff);
					break;
				default:
					helpMsg(p);
					break;
				}
				return true;
			}
			helpMsg(p);
			return true;
		}
		return true;
	}
	
	public void helpMsg(Player p) {
		p.sendMessage("");
		p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Arrest Plugin Help");
		p.sendMessage("");
		p.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "/arrest arrest [Player]");
		p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "- Arrest a player with commands");
		p.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "/arrest handcuff");
		p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "- Recieve a handcuff to arrest someone else");
		p.sendMessage("");
	}
	
	public ItemStack createItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
}
