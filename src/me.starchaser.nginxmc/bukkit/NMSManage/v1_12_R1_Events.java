package me.starchaser.nginxmc.bukkit.NMSManage;

import me.starchaser.nginxmc.bukkit.starchaser;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

import static me.starchaser.nginxmc.bukkit.starchaser.servergamemode;

public class v1_12_R1_Events implements Listener {
	@EventHandler
	public void pickupArrow(PlayerPickupArrowEvent e) {
		if(servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getPlayer().getGameMode() == GameMode.ADVENTURE) e.setCancelled(true);
	}
}
