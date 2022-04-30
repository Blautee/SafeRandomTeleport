package de.trebtee.srtp.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
					}
				}
				Location loc = generateLocation(player);
				if (loc == null) {
					//no adequate location found
					player.sendMessage(p + Settings.lang_failed);
					return false;
				} else {
					player.teleport(loc);
					player.sendMessage(p + Settings.lang_success);
					return true;
				}
			} else {
				//no perms
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
					return false;
				} else {
					Location loc = generateLocation(player);
					if (loc == null) {
						//no adequate location found
						sender.sendMessage(p + "srtp failed via console!");
						player.sendMessage(p + Settings.lang_failed);
						return false;
					} else {
						player.teleport(loc);
						sender.sendMessage(p + "srtp success via console!");
						player.sendMessage(p + Settings.lang_failed);
						return true;
					}
				}
			} else {
				//wrong use
				sender.sendMessage("wrong srtp usage!");
				return false;
			}
		}
	}
	
	public Location generateLocation(Player player) {
		int x = 0;
		int z = 0;
		
		int minX = Settings.min_disatnce;
		int minZ = Settings.min_disatnce;
		
		int maxX = Settings.max_distance;
		int maxZ = Settings.max_distance;
		
		int minDistance = Settings.min_player_distance;
		int maxTries = Settings.max_tries;
		
		boolean b = true;
		
		while (b) {
			Random random = new Random();
			x = random.nextInt(maxX - minX) + minX;
			if (random.nextBoolean()) {
				x = -x;
			}
			
			z = random.nextInt(maxZ - minZ) + minZ;
			if (random.nextBoolean()) {
				z = -z;
			}
			
			Location loc = new Location(player.getWorld(), x, player.getWorld().getHighestBlockYAt(x, z), z);
			
			Block block = loc.getBlock();
			
			List<Material> blacklist = Settings.material_blacklist;
			
			List<Boolean> userInRange = new ArrayList<Boolean>();
			for (int i = 0; i > maxTries; i++) {
				if (!blacklist.contains(block.getType())) {
					if (loc.add(0, 1, 0).getBlock().getType() == Material.AIR && loc.add(0, 2, 0).getBlock().getType() == Material.AIR) {
						userInRange.clear();
						for (Player p : Bukkit.getOnlinePlayers()) {
							userInRange.add(p.getLocation().distance(loc) <= minDistance);
						}
						if (!userInRange.contains(true)) {
							b = false;
							return loc;
						}
						//this area was not free...
					}
					//not enough space
				}
				//block not safe
			}
			//no free area found in max trys
		}
		return null;
	}
}
