package me.starchaser.nginxmc.bukkit.NMSManage;

import me.starchaser.nginxmc.bukkit.NMS;
import me.starchaser.nginxmc.bukkit.core;
import me.starchaser.nginxmc.bukkit.starchaser;
import net.md_5.bungee.api.plugin.Listener;
import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;

import static me.starchaser.nginxmc.bukkit.starchaser.servergamemode;

public class v1_12_R1 implements NMS {
	@Override
	public void removeArrowonPlayer(Player p) {
		Bukkit.getScheduler().runTaskLater(core.getNginxMC,() -> ((CraftPlayer)p).getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b),0), 5L);
	}
	@Override
	public ItemStack getPlayerMainHand(Player p){
		return p.getInventory().getItemInMainHand();
	}

	@Override
	public Sound getSound(String s) {
		String f = s;
		switch (s){
			case "IRONGOLEM_THROW": f = "ENTITY_IRONGOLEM_ATTACK";
		}
		return Sound.valueOf(f);
	}
}