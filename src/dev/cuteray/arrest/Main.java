package dev.cuteray.arrest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cuteray.arrest.commands.Command;
import dev.cuteray.arrest.events.Events;
import dev.cuteray.arrest.whenarrest.WhenArrested;

import org.bukkit.ChatColor;

public class Main extends JavaPlugin {

	public FileConfiguration config = getConfig();
	public String prefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "Arrest" + ChatColor.GRAY + "] ";
	
	public WhenArrested WhenArrested;
	
	@Override
	public void onEnable() {
		
		config.addDefault("ArrestedPlayers.Example-UUID.isArrested", true);
		config.addDefault("Arresters.Example-UUID.arrested", "Example-UUID");
		config.options().copyDefaults(true);
        saveConfig();
        new WhenArrested(this);
        new Events(this);
        new Command(this);
	}
	
	@Override
	public void onDisable() {
		
	}
	
}
