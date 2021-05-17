package dev.cuteray.arrest.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import dev.cuteray.arrest.Main;
import dev.cuteray.arrest.whenarrest.WhenArrested;
import net.md_5.bungee.api.ChatColor;

public class Events implements Listener {
	
	Main plugin;
	WhenArrested wa;
	
	public Events(Main main) {
		main.getServer().getPluginManager().registerEvents(this, main);
		plugin = main;
		wa = main.WhenArrested;
	}
	
	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
		
		Player p = e.getPlayer();
		if (e.getRightClicked().getType().equals(EntityType.PLAYER) && p.getInventory().getItemInMainHand().getType().equals(Material.MAGMA_CREAM)) {
			EquipmentSlot slot = e.getHand();
		    	if (slot.equals(EquipmentSlot.HAND)) {
		       	Player arrestedP = (Player) e.getRightClicked();
		       	wa.ArrestPlayer(p, arrestedP);
		       }
		}
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		boolean doPlayerInConfig = plugin.config.getConfigurationSection("ArrestedPlayers").getKeys(false).contains(e.getPlayer().getUniqueId().toString().toLowerCase());
		if (plugin.config.getConfigurationSection("ArrestedPlayers").getKeys(false) == null) {
			e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "An error occured when you joined the server.");
			for (OfflinePlayer p : Bukkit.getServer().getOperators()) {
				if (Bukkit.getPlayer(p.getName()) != null){
					p.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "An error occured when a player join the server. Please restart the server.");
				}
			}
			return;
		}
		if (!doPlayerInConfig){
			String playerUUID = e.getPlayer().getUniqueId().toString().toLowerCase();
			plugin.config.set("ArrestedPlayers." + playerUUID + ".isArrested", false); 
			plugin.saveConfig();
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		String playerUUID = e.getPlayer().getUniqueId().toString().toLowerCase();
		if (plugin.config.getBoolean("ArrestedPlayers." + playerUUID + ".isArrested")) {
			Player arrester = (Player) plugin.config.get("ArrestedPlayers." + playerUUID + ".arrestGuy");
			String arresterUUID = arrester.getUniqueId().toString().toLowerCase();
			plugin.config.set("ArrestedPlayers." + playerUUID + ".isArrested", false);
			plugin.config.set("ArrestedPlayers." + playerUUID + ".arrestGuy", null);
			plugin.config.set("Arresters." + arresterUUID + ".arrested", null);
			arrester.sendMessage(plugin.prefix + ChatColor.RED + "The arrested player is disconnected.");
			return;
		}
		if (plugin.config.getConfigurationSection("Arresters").getKeys(false).contains(playerUUID)) {
			Player arrested = (Player) plugin.config.get("Arresters" + playerUUID + ".arrested");
			String arrestedUUID = arrested.getUniqueId().toString().toLowerCase();
			plugin.config.set("ArrestedPlayers." + arrestedUUID + ".arrestGuy", null);
			plugin.config.set("ArrestedPlayers." + arrestedUUID + ".isArrested", false);
			plugin.config.set("Arresters." + playerUUID + ".arrested", null);
			arrested.sendMessage(plugin.prefix + ChatColor.RED + "You have succesfully released because the arrester has disconnected.");
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		String playerUUID = e.getPlayer().getUniqueId().toString().toLowerCase();
		if (plugin.config.getBoolean("ArrestedPlayers." + playerUUID + ".isArrested")) {
			Player arrestHuman = (Player) plugin.config.get("ArrestedPlayers." + playerUUID + ".arrestGuy");
			if (Bukkit.getPlayer(arrestHuman.getName()) == null) {
				e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "You have succesfully released because the arrester has disconnected.");
				plugin.config.set("ArrestedPlayers." + playerUUID + ".arrestGuy", null);
				plugin.config.set("ArrestedPlayers." + playerUUID + ".isArrested", false);
				plugin.config.set("Arresters." + arrestHuman.getUniqueId().toString().toLowerCase() + ".arrested", null);
				return;
			}
			Location arrestLocation = arrestHuman.getLocation();
			Location playerLocation = e.getPlayer().getLocation();
			double difX = Math.abs(arrestLocation.getX() - playerLocation.getX());
			double difY = Math.abs(arrestLocation.getY() - playerLocation.getY());
			double difZ = Math.abs(arrestLocation.getZ() - playerLocation.getZ());
			
			if (difX > 4 || difY > 4 || difZ > 4) {
				e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "You are under arrested and you cannot go to far from the arrester.");
				e.getPlayer().teleport(arrestHuman.getLocation());
			}
			return;
		}
		else {
			if (plugin.config.getConfigurationSection("Arresters").getKeys(false).contains(playerUUID)) {
				Player arrested = (Player) plugin.config.get("Arresters." + playerUUID + ".arrested");
				if (arrested == null) return;
				if (Bukkit.getPlayer(arrested.getName()) == null) {
					e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "The arrested player is disconnected.");
					plugin.config.set("ArrestedPlayers." + arrested.getUniqueId().toString().toLowerCase() + ".arrestGuy", null);
					plugin.config.set("ArrestedPlayers." + arrested.getUniqueId().toString().toLowerCase() + ".isArrested", false);
					plugin.config.set("Arresters." + e.getPlayer().getUniqueId().toString().toLowerCase() + ".arrested", null);
					return;
				}
				double difX = Math.abs(e.getPlayer().getLocation().getX() - arrested.getLocation().getX());
				double difY = Math.abs(e.getPlayer().getLocation().getY() - arrested.getLocation().getY());
				double difZ = Math.abs(e.getPlayer().getLocation().getZ() - arrested.getLocation().getZ());
				if (difX > 4 || difY > 4 || difZ > 4) {
					arrested.teleport(e.getPlayer().getLocation());
					arrested.sendMessage(plugin.prefix + ChatColor.RED + "You are under arrested and you cannot go to far from the arrester.");
				}
				return;
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			String playerUUID = p.getUniqueId().toString().toLowerCase();
			if (plugin.config.getBoolean("ArrestedPlayers." + playerUUID + ".isArrested")) {
				e.setCancelled(true);
				p.sendMessage(plugin.prefix + ChatColor.RED + "You are under arrested and you cannot hit entitities.");
			}
			return;
		}
	}
		
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		String playerUUID = e.getPlayer().getUniqueId().toString().toLowerCase();
		if (plugin.config.getBoolean("ArrestedPlayers." + playerUUID + ".isArrested")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "You are under arrested and you cannot mine blocks.");
		}
		return;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		String playerUUID = e.getPlayer().getUniqueId().toString().toLowerCase();
		if (plugin.config.getBoolean("ArrestedPlayers." + playerUUID + ".isArrested")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(plugin.prefix + ChatColor.RED + "You are under arrested and you cannot place blocks.");
		}
		return;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			String playerUUID = p.getUniqueId().toString().toLowerCase();
			if (plugin.config.getBoolean("ArrestedPlayers." + playerUUID + ".isArrested")) {
				e.setCancelled(true);
				p.sendMessage(plugin.prefix + ChatColor.RED + "You are under arrested and you cannot be hurt.");
			}
		}
		return;
	}
	
}
	

