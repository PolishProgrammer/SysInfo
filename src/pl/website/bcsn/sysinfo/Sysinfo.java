package pl.website.bcsn.sysinfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.website.bcsn.sysinfo.InfoGatherer.infoType;

public class Sysinfo extends JavaPlugin {
	
	
	public static Sysinfo instance = new Sysinfo();
	public static Server server;
	public final static char asciiBlock = '\u2588';
	public final static char degSign = '\u00B0';
	
	public static File dataFolder;
	
	
	// Pliki konfiguracyjne (locale itp.)
	public static File localeFile;
	public static File configFile;
	public static List<String> locales_names;
	public static FileConfiguration locale;
	public static FileConfiguration config;

	public static Thread ramCheckThread;
	public static Thread recorderThread;

	@SuppressWarnings("deprecation")
	public void onDisable() {
		// saveYamls();
		ramCheckThread.stop(); // no idea how to better do it :P
		recorderThread.stop();
	}

	public void onEnable() {
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		server = getServer();
		instance = new Sysinfo();
		//System.out.println("This is Sysinfo by TheKiwi5000");
		// saveConfig();
		/*
		 * Locale.yml will be currently used file to read language. next to it
		 * there would be locale-xxx.yml files where xxx is language code (en,
		 * en-us, pl, de, fr, etc.)
		 */

		/*
		 * Thanks to DomovoiButler for his great multi-file config tutorial:
		 * https
		 * ://forums.bukkit.org/threads/bukkits-yaml-configuration-tutorial.42770
		 * /
		 */
		dataFolder = getDataFolder();
		
		
		
		localeFile = new File(getDataFolder(), "locale.yml"); // locale.yml
		configFile = new File(getDataFolder(), "config.yml");
		if (!localeFile.exists()) {
			localeFile.getParentFile().mkdirs();
			copy(getResource("locale.yml"), localeFile);
		}
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}

		locale = new YamlConfiguration();
		config = new YamlConfiguration();

		loadYamls();
		locales_names = config.getStringList("locales");
		System.out.println(locales_names);
		for (String s : locales_names) {
			System.out.println("Added " + s + "locale to list");
			copy(getResource("locale-" + s + ".yml"), new File(getDataFolder(),
					"locale-" + s + ".yml"));
		}
		
		
		
		AlarmThread aThr = new AlarmThread();
		aThr.init();
		ramCheckThread = new Thread(aThr);
		ramCheckThread.start(); // Starting RAM check thread
		System.out.println("Starting RAM check thread.");
		
		
		RecorderThread rThr = new RecorderThread();
		rThr.init();
		recorderThread = new Thread(rThr);
		recorderThread.start();

	}// end onEnable()

	public void saveYamls() {
		try {
			locale.save(localeFile);
			config.save(configFile);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadYamls() {
		try {
			locale.load(localeFile);
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	
	
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equalsIgnoreCase("sysinfo")) {
			if (args.length == 0) {
				msgSysinfo(sender);
				return true;
			}
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help")){
					msgHelp(sender);
					return true;
				}
				
				if (args[0].equalsIgnoreCase("alarm")) { // /sysinfo alarm [...]
					if (args.length == 1 && sender.hasPermission("sysinfo.alarm-recv")){ // /sysinfo alarm
						if (InfoGatherer.getRawRamUsage()[0] >= config.getInt("ram-alarm-level")){ //if over crit level
							senderMSG(locale.getString("ui.alarm.col-bad")+ locale.getString("ui.alarm.ram-usage-bad"), sender);
							return true;
						}else{ //if not
							senderMSG(locale.getString("ui.alarm.col-ok")+ locale.getString("ui.alarm.ram-usage-ok"), sender);
						}
						return true;
					}
					
					if (args.length == 2){
						if(args[1].equalsIgnoreCase("level") && sender.hasPermission("sysinfo.alarm-recv")){ // /sysinfo alarm level
							String s = locale.getString("ui.alarm.ram-level");
							s = s.replaceAll("@crit-ram", config.getString("ram-alarm-level"));
							senderMSG(s, sender);
							return true;
						}
					}
					
					if (args.length == 3){
						if(args[1].equalsIgnoreCase("level") && sender.hasPermission("sysinfo.alarm-set")){ // /sysinfo alarm level value
							config.options().configuration().set("ram-alarm-level", Integer.valueOf(args[2]));
							return true;
						}
					}else{
						senderMSG(locale.getString("error.col")+ locale.getString("error.args"), sender);
						return true;
					}
					
				}else if((args[0].equalsIgnoreCase("gc") || args[0].equalsIgnoreCase("freemem")) && sender.hasPermission("sysinfo.exec-gc") ){
					sender.sendMessage("WARNING:  Experimental feature! Please report performance information (Ram before, ram after)!");
					sender.sendMessage("BEFORE: "+InfoGatherer.getInfo(infoType.MACHINE_RAM_USAGE));
					System.gc(); //Garbage collecting
					sender.sendMessage("AFTER:  "+InfoGatherer.getInfo(infoType.MACHINE_RAM_USAGE));
					
				}
			}
		}
		// }

		return false;
	}
	
	
	
	
	public static void collectGarbage(CommandSender cs) {
		
		cs.sendMessage("WARNING:  Experimental feature! Please report performance information (Ram before, ram after)!");
		cs.sendMessage("BEFORE: "+InfoGatherer.getInfo(infoType.MACHINE_RAM_USAGE));
		System.gc(); //Garbage collecting
		cs.sendMessage("AFTER:  "+InfoGatherer.getInfo(infoType.MACHINE_RAM_USAGE));
	}
	
	/*
	 * I had to make a PASSIVE method for mesaging.
	 */
	public static void msgPlayer(String playername, String msg){
		Player p = Sysinfo.instance.getServer().getPlayerExact(playername);
		
		p.sendMessage(msg);
	}
	
	private void msgHelp(CommandSender sender) {
		String c1, c2;
		c1 = locale.getString("ui.help.col1");
		c2 = locale.getString("ui.help.col2");

		for(String s : locale.getStringList("ui.help.text")){
			s = s.replaceAll("@version", getDescription().getVersion());
			s = s.replaceAll("@command", "sysinfo");
			s = s.replaceAll("@c1", c1);
			s = s.replaceAll("@c2", c2);

			
			senderMSG(s, sender);
		}

	}

	private void msgSysinfo(CommandSender sender) {
		// Arch
		senderMSG(
				"&6" + locale.getString("sys-arch") + ":&c "
						+ InfoGatherer.getInfo(infoType.SYSTEM_ARCH), sender);

		// system name and version
		senderMSG(
				"&6" + locale.getString("sys-os") + ":&c "
						+ InfoGatherer.getInfo(infoType.SYSTEM_NAME) + " "
						+ locale.getString("sys-os-version") + " "
						+ InfoGatherer.getInfo(infoType.SYSTEM_VERSION), sender);

		// bukkit version
		senderMSG(
				"&6" + locale.getString("bukkit-version") + ":&c "
						+ InfoGatherer.getInfo(infoType.BUKKIT_VERSION), sender);

		// players x of max y
		senderMSG(
				"&6" + locale.getString("players") + ":&c "
						+ InfoGatherer.getInfo(infoType.PLAYERS_NOW) + " "
						+ locale.getString("players-of-max") + " "
						+ InfoGatherer.getInfo(infoType.PLAYERS_MAX), sender);

		// server ip:port
		senderMSG(
				"&6" + locale.getString("server-ip") + ":&c "
						+ InfoGatherer.getInfo(infoType.SERVER_IP) + ":"
						+ InfoGatherer.getInfo(infoType.SERVER_PORT), sender);

		// server motd
		senderMSG("&6"
				+ locale.getString("motd") + ":&r " // before MOTD formatting
				// must be removed!
				+ InfoGatherer.getInfo(infoType.SERVER_EXTERN_MOTD), sender);

		// temp
		senderMSG(
				"&6" + locale.getString("temp") + ":&c "
						+ InfoGatherer.getInfo(infoType.MACHINE_TEMP), sender);

		// ram usage
		senderMSG(ChatColor.translateAlternateColorCodes(
				'&',
				"&6" + locale.getString("ram-usage") + ":&c "
						+ InfoGatherer.getInfo(infoType.MACHINE_RAM_USAGE)), sender);

		// ram bar [#####000000]
		senderMSG(ChatColor.translateAlternateColorCodes(
				'&',
				"&6" + locale.getString("ram-bar") + ":&c "
						+ InfoGatherer.getInfo(infoType.MACHINE_RAM_GRAPH)), sender);
	}

	private void senderMSG(String msg, CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		
	}

	
	public static  void log(String msg){
		System.out.println("["+"DUPA"+"]"+msg);
	}
	
	
	
	
}
