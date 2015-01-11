package pl.website.bcsn.sysinfo;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener{
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent ple){
		Sysinfo.playerSessions.put(ple.getPlayer().getName(), 0);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogout(PlayerQuitEvent pqe){
		Sysinfo.playerSessions.remove(pqe.getPlayer().getName());
	}
}
