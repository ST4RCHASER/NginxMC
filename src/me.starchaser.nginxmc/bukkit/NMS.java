package me.starchaser.nginxmc.bukkit;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {
	void removeArrowonPlayer(Player p);
	ItemStack getPlayerMainHand(Player p);
	Sound getSound(String s);
}
