package de.trebtee.srtp.main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;

public class Settings {
	public static String user_perms;
	public static String admin_perms;
	
	public static int max_distance;
	public static int min_disatnce;
	public static int min_player_distance;
	
	public static int max_tries;
	
	public static List<Material> material_blacklist = new ArrayList<Material>();
	
	public static String lang_prefix;
	public static String lang_no_perms;
	public static String lang_success;
	public static String lang_failed;

	
	public static void reloadConfig() {
		user_perms = Main.getPlugin().getConfig().getString("settings.user_perms");
		admin_perms = Main.getPlugin().getConfig().getString("settings.admin_perms");
		
		max_distance = Main.getPlugin().getConfig().getInt("settings.max_distance");
		min_disatnce = Main.getPlugin().getConfig().getInt("settings.min_distance");
		min_player_distance = Main.getPlugin().getConfig().getInt("settings.player_distance");
		
		max_tries = Main.getPlugin().getConfig().getInt("settings.max_tries");
		
		for (Object s : Main.getPlugin().getConfig().getList("settings.material_blacklist")) {
			if (Material.valueOf(s.toString()) != null) {
				material_blacklist.add(Material.valueOf(s.toString()));
			}
		}
		
		
		lang_prefix = applyColorCodeTranslate("lang.prefix");
		lang_no_perms = applyColorCodeTranslate("lang.no_perms");
		lang_success = applyColorCodeTranslate("lang.success");
		lang_failed = applyColorCodeTranslate("lang.failed");
	}
	
	public static String applyColorCodeTranslate(String s) {
		String out;
		try {
			out = ChatColor.translateAlternateColorCodes('&', Main.getPlugin().getConfig().getString(s));
			if (out == null) {
				out = "Configuration Error!";
			}
			return out;
		} catch (Exception e) {
			out = Main.getPlugin().getConfig().getString(s);
			if (out == null) {
				out = "Configuration Error!";
			}
			return out;
		}
	}
	
}
