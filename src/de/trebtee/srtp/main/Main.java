package de.trebtee.srtp.main;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	private static Main plugin;
	
	public void onEnable() {
		plugin = this;
		
		saveDefaultConfig();
		reloadConfig();
		
		Settings.reloadConfig();
		
		getCommand("srtp").setExecutor((CommandExecutor) new TpCommand());
	}
	
	public static Main getPlugin() {
		return plugin;
	}
	
}
