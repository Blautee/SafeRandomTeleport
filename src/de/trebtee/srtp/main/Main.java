package de.trebtee.srtp.main;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private static Main plugin;

	@Override
	public void onEnable() {
		plugin = this;

		saveDefaultConfig();
		reloadConfig();

		Settings.reloadConfig();

		getCommand("srtp").setExecutor(new TpCommand());
	}

	public static Main getPlugin() {
		return plugin;
	}

}
