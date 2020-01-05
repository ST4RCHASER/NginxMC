package me.starchaser.nginxmc.bukkit.NMSManage;

import me.starchaser.nginxmc.bukkit.NMS;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class v1_8_R3 implements NMS {
	@Override
	public void removeArrowonPlayer(Player p) {
	}

	@Override
	public ItemStack getPlayerMainHand(Player p) {
		return p.getItemInHand();
	}

	@Override
	public Sound getSound(String s) {
		return Sound.valueOf(s);
	}
}
