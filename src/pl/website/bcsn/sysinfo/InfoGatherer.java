package pl.website.bcsn.sysinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.ChatColor;

public class InfoGatherer {
	public final static char degSign = '\u00B0';
	public final static char asciiBlock = '\u2588';
	public static enum infoType{
		SYSTEM_ARCH,
		SYSTEM_NAME, 
		SYSTEM_VERSION,
		BUKKIT_VERSION,
		PLAYERS_NOW,
		PLAYERS_MAX,
		PLAYERS_INFO,
		SERVER_IP,
		SERVER_PORT,
		SERVER_EXTERN_MOTD,
		MACHINE_CPU_USAGE,   //NOT IMPLEMENTED
		MACHINE_CPU_CORES,
		MACHINE_TEMP, //with ACPI support only
		MACHINE_RAM_USAGE,
		MACHINE_RAM_GRAPH,
		MACHINE_RAW_RAM_USAGE,
		MACHINE_UPLOAD,
		MACHINE_DOWNLOAD
	};

	public static String getInfo(infoType type){
		switch(type){
		case SYSTEM_ARCH: return System.getProperty("os.arch");
		case SYSTEM_NAME:  return System.getProperty("os.name");
		case SYSTEM_VERSION: return System.getProperty("os.version");

		case BUKKIT_VERSION: return Sysinfo.server.getBukkitVersion();

		case PLAYERS_NOW: return Sysinfo.server.getOnlinePlayers().length + ""; // + "" is a fast toString()
		case PLAYERS_MAX: return Sysinfo.server.getMaxPlayers() + "";
		case PLAYERS_INFO: return Util.getPlayerSessions();
		
		case SERVER_IP: return Sysinfo.server.getIp();
		case SERVER_PORT: return Sysinfo.server.getPort() + "";
		case SERVER_EXTERN_MOTD: return Sysinfo.server.getMotd();

		case MACHINE_CPU_USAGE: return ChatColor.translateAlternateColorCodes('&', "&a&lCPU usage: " + "&2&o&lNot implemented yet.");
		case MACHINE_CPU_CORES: return Runtime.getRuntime().availableProcessors() + "X";
		case MACHINE_TEMP: return getTemp();
		
		case MACHINE_RAM_GRAPH: return getRamUsageBar();
		case MACHINE_RAM_USAGE: return getRamUsage();
		case MACHINE_RAW_RAM_USAGE: return getRawRamUsageAsStr();


		default: return "error";
		}


	}

	private static String getRawRamUsageAsStr() {
		String s = "";
		int[] arr = getRawRamUsage();
		for(int i : arr){
			s += i + "|";
		}
		return s;
	}

	private static String getTemp() {
		/*    0   |1 | 2 | 3  |   4   |5
		 * Thermal 0: ok, 88.0 degrees C
		 * Thermal 1: ok, 88.0 degrees C
		 * Sample acpi -t output
		 */
		String usage = "";
		try {
			Process proc = Runtime.getRuntime().exec("acpi -t");
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(proc.getInputStream()));

			// read the output from the command
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				usage += s + "\n";
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			return ChatColor.translateAlternateColorCodes('&', "&c&l Error while getting value. Please try again");
		}
		if(!System.getProperty("os.name").equals("Linux")){
			return ChatColor.translateAlternateColorCodes('&', "&c&l Not available on " + System.getProperty("os.name"));
		}
		//return usage;
		String out = "&b";
		try{
			String[] lines = usage.split("\n");
			for(String s : lines){
				String[] words = s.split(" ");
				out += "T" + words[1] + " " + words[3] + degSign+"C ["+words[2]+"]|";
			}
		}catch(Throwable tr){
			out = "&cNot supported on machine";
			//tr.printStackTrace();
		}
		return out;
	}


	private static String getRamUsage() {
		Runtime runtime = Runtime.getRuntime();
		long freemem = runtime.freeMemory()/1048576; //in MB
		long maxmem = runtime.maxMemory()/1048576; //in MB
		long usedmem = maxmem-freemem;
		float percent = ((float) usedmem/maxmem)*100;
		String ret = "&b[&9&l"+Math.floor(percent)+"%&b][&9&l"+usedmem+"MB/"+maxmem+"MB&b]";
		//int[] arr = {(int)Math.floor(percent), (int) usedmem, (int) maxmem};
		return ret;
	}
	
	
	private static String getRamUsageBar(){
		Runtime runtime = Runtime.getRuntime();
		long freemem = runtime.freeMemory()/1048576; //in MB
		long maxmem = runtime.maxMemory()/1048576; //in MB
		long usedmem = maxmem-freemem;
		float percent = ((float) usedmem/maxmem)*100;
		int steps = (int) (percent/5);
		String membar = "&r&2";
		//rendering membar
		char color = 'c'; //red color code (sybolizes used mem)
		for(int i = 0; i <= 20; i++){
			if(i >= steps){
				color='a'; //green color code (sybolizes unused mem)
			}
			membar += "&"+color+asciiBlock;
		}
		return "&b["+membar+"&b]&9&l" + (float) percent + "%";
	}
	
	/*
	 * Array format:
	 * 0 = percentage
	 * 1 = used memory
	 * 2 = total memory
	 */
	public static int[] getRawRamUsage(){
		Runtime runtime = Runtime.getRuntime();
		long freemem = runtime.freeMemory()/1048576; //in MB
		long maxmem = runtime.maxMemory()/1048576; //in MB
		long usedmem = maxmem-freemem;
		float percent = ((float) usedmem/maxmem)*100;
		//String ret = "&b[&9&l"+Math.floor(percent)+"%&b][&9&l"+usedmem+"MB/"+maxmem+"MB&b]";
		int[] arr = {(int)Math.floor(percent), (int) usedmem, (int) maxmem};
		return arr;
		
		
	}
}



