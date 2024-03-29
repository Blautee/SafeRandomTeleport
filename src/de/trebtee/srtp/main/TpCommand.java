package de.trebtee.srtp.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command label, String command, String[] args) {
		String p = Settings.lang_prefix;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission(Settings.user_perms)) {
				if (player.hasPermission(Settings.admin_perms)) {
					if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
						Settings.reloadConfig();
						player.sendMessage(p + Settings.lang_reload);
						return true;
					} else if (args.length == 1 && args[0].equalsIgnoreCase("center")) {
						Main.getPlugin().getConfig().set("settings.center", player.getLocation());
						Main.getPlugin().saveConfig();
						Settings.reloadConfig();
						player.sendMessage(p + Settings.lang_center_set);
						return true;
					}
				}
				if (args.length != 0) {
					//wrong use
					if (Settings.feedback) {
						sender.sendMessage(p + Settings.lang_wrong_use);
					}
					return false;
				}
				
				Location loc = generateLocation(player);
				if (loc == null) {
					if (Settings.feedback) {
						player.sendMessage(p + Settings.lang_failed);
					}
					return false;
				} else {
					player.teleport(loc.add(.5, -2, .5));
					if (Settings.feedback) {
						player.sendMessage(p + Settings.lang_success);
					}
					return true;
				}
			} else {
				if (!Settings.lang_no_perms.equalsIgnoreCase("")) {
					player.sendMessage(p + Settings.lang_no_perms);
				}
				return false;
			}
		} else {
			if (args.length == 1) {
				Player player = Bukkit.getPlayer(args[0]);
				if (player == null) {
					//no such player
					sender.sendMessage(p + "no such player...");
					return false;
				} else {
					Location loc = generateLocation(player);
					if (loc == null) {
						//no adequate location found
						sender.sendMessage(p + "teleport fail via console!");
						if (Settings.feedback) {
							player.sendMessage(p + Settings.lang_failed);
						}
						return false;
					} else {
						player.teleport(loc.add(.5, -2, .5));
						if (Settings.feedback) {
							sender.sendMessage(p + "teleport success via console!");
						}
						player.sendMessage(p + Settings.lang_success);
						return true;
					}
				}
			} else {
				//wrong use
				if (Settings.feedback) {
					sender.sendMessage(p + Settings.lang_wrong_use);
				}
				return false;
			}
		}
	}

	public Location generateLocation(Player player) {
		int x = 0;
		int z = 0;
		
		int y = 0;
		
		Location center = Settings.center;
		
		if (center == null) {
			center = new Location(player.getWorld(), 0, 100, 0);
		}
		
		int minX = Settings.min_disatnce;
		int minZ = Settings.min_disatnce;

		int maxX = Settings.max_distance;
		int maxZ = Settings.max_distance;
		
		// TODO Check Worldborder, compare to max

		int minDistance = Settings.min_player_distance;
		int maxTries = Settings.max_tries;

		Random random = new Random();
		List<Material> blacklist = Settings.material_blacklist;
		List<Boolean> userInRange = new ArrayList<>();
		
		for (int i = 0; i < maxTries; i++) {
			
			x = random.nextInt(maxX - minX) + minX;
			if (random.nextBoolean()) {
				x = -x;
			}
			x = center.getBlockX() + x;

			z = random.nextInt(maxZ - minZ) + minZ;
			if (random.nextBoolean()) {
				z = -z;
			}
			z = center.getBlockZ() + z;
			
			y = player.getWorld().getHighestBlockYAt(x, z);
			
			Location loc = new Location(player.getWorld(), x, y, z);

			Block block = loc.getBlock();

			if (!blacklist.contains(block.getType()) || !blacklist.contains(loc.subtract(0, 1, 0).getBlock().getType())) {
				if (loc.add(0, 2, 0).getBlock().getType() == Material.AIR && loc.add(0, 1, 0).getBlock().getType() == Material.AIR) {
					userInRange.clear();
					for (Player p : Bukkit.getOnlinePlayers()) {
						if (!player.getName().equalsIgnoreCase(p.getName())) {
						    if (p.getWorld().getName().equals(player.getWorld().getName())) {
						        userInRange.add(p.getLocation().distance(loc) <= minDistance);
						    }
						}
					}
					if (!userInRange.contains(true)) {
						Bukkit.getLogger().log(Level.INFO, "Random Location for " + player.getName() + " found in " + i + " tries!");
						return loc;
					} else {
						//this area was not free...
						
					}
				} else {
					//not enough space
				}
			} else {
				//block not safe
			}
			//no free area found in max tries
		}
		Bukkit.getLogger().log(Level.INFO, "No random Location found for " + player.getName() + "!");
		return null;
	}
}
