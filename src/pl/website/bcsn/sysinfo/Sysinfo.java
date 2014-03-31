package pl.website.bcsn.sysinfo;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Sysinfo extends JavaPlugin {
	public static Server server;
	

	/* A COMMENT
	 * (non-Javadoc)
	 * 
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable() PERMISSION NODES:
	 * sysinfo.sysinfo - access /sysinfo
	 */

	public void onEnable() {
		server = getServer();
		server.getLogger().fine("This is Sysinfo by TheKiwi5000");

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("sysinfo")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
					("&6&lSystem architecture: &c&o" + System
							.getProperty("os.arch"))));

			sender.sendMessage(ChatColor.translateAlternateColorCodes(
					'&',
					("&6&lOperatring system: &c&o"
							+ System.getProperty("os.name")
							+ " &6&lversion &c&o" + System
							.getProperty("os.version"))
					)
					);

			sender.sendMessage(ChatColor.translateAlternateColorCodes(
					'&',
					("&6&lBukkit version: &c&o" + server.getBukkitVersion())));
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes(
					'&',
					("&6&lPlayers: &c&o" + server.getOnlinePlayers().length + " &6&lof max &c&o" + server.getMaxPlayers())));
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes(
					'&',
					("&6&lServer IP: &c&o" + server.getIp()+"&6&l:&c&o"+server.getPort())));
			
			sender.sendMessage(ChatColor.translateAlternateColorCodes(
					'&',
					("&6&lClient serverlist MOTD: " + server.getMotd())));

			return true;
		}

		return false;
	}
}
