package pl.website.bcsn.sysinfo;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AlarmThread implements Runnable {
	private int interval = 60;
	private int triggerValue = 90;

	public void init() {
		interval = Sysinfo.config.getInt("ram-check-interval");
		triggerValue = Sysinfo.config.getInt("ram-alarm-level");
	}

	@Override
	public void run() {
		if (InfoGatherer.getRawRamUsage()[0] >= triggerValue){
			sendMessages();
		}
		try {
			Thread.sleep(interval*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendMessages() {
		
		String fullmsg = Sysinfo.locale.getString("alert-msg-prefix");
		String noPrefixMsg = Sysinfo.locale.getString("alert-msg-ram");
		
		noPrefixMsg = noPrefixMsg.replaceAll("@ram", InfoGatherer.getRawRamUsage()[0]+"");
		noPrefixMsg = noPrefixMsg.replaceAll("@crit-ram", triggerValue+"");
		
		fullmsg = fullmsg.replaceAll("@msg", noPrefixMsg);
		fullmsg = ChatColor.translateAlternateColorCodes('&', fullmsg);
		for (Player p : Sysinfo.server.getOnlinePlayers()) {
			if (p.hasPermission("sysinfo.alarm-recv")) {
				p.sendMessage(fullmsg);
			}
		}
		Sysinfo.server.getConsoleSender().sendMessage(fullmsg);

	}

}
