package pl.website.bcsn.sysinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Sysinfo extends JavaPlugin {
	public static Server server;
	public final static char asciiBlock = '\u2588';

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
		if (cmd.getName().equalsIgnoreCase("sysinfo") ) {

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

			//sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lCPU usage: " + getExecOutput("ps -A| grep java")));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lCPU usage: " + "&2&o&lNot implemented yet."));

			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lTemp: " + getExecOutput("acpi -t")));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lRAM usage" + getRamUsage()));

			return true;
		}
		//}

		return false;
	}

	private String getRamUsage() {
		Runtime runtime = Runtime.getRuntime();
		long freemem = runtime.freeMemory()/1048576; //in MB
		long totmem = runtime.maxMemory()/1048576; //in MB
		System.out.print(freemem + " " + totmem + " " + (float) freemem/totmem);
		float percent = ((float) freemem/totmem)*100;
		System.out.println("perc"+ percent);
		int steps = (int) (percent/10);
		String ret = "&b[&9&l"+percent+"%&b][&9&l"+freemem+"kB/"+totmem+"kB&b]" + freemem/totmem;
		String membar = "&r&2";
		char color = 'c';
		for(int i = 0; i <= 10; i++){
			if(i >= steps){
				color='a';
			}
			membar += "&"+color+asciiBlock;
		}
		System.out.println((float) 119/193);
		return ret + membar;
	}

	private String getExecOutput(String command) {
		String usage = "&b";
		try {
			Process proc = Runtime.getRuntime().exec(command);
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(proc.getInputStream()));

			// read the output from the command
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				usage += s + "\n";
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			return ChatColor.translateAlternateColorCodes('&', "&c&l Error while getting value. Please try Again");
		}
		if(!System.getProperty("os.name").equals("Linux")){
			return ChatColor.translateAlternateColorCodes('&', "&c&l Not available on " + System.getProperty("os.name"));
		}
		return usage;
	}
}
