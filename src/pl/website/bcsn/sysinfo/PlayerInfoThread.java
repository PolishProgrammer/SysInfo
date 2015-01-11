package pl.website.bcsn.sysinfo;

import org.bukkit.scheduler.BukkitRunnable;

public class PlayerInfoThread extends BukkitRunnable {
	@Override
	public void run() {
		for (String s : Sysinfo.playerSessions.keySet()) {
			Sysinfo.playerSessions.put(s, Sysinfo.playerSessions.get(s)+1);
		}
	}
}
