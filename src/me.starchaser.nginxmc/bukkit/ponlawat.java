package me.starchaser.nginxmc.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ponlawat {
	public static void sendLobby(Player p, String serverName) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try
		{
			out.writeUTF("Connect");
			out.writeUTF(serverName);
			starchaser.Logger(starchaser.LOG_TYPE.BC,"send " + p.getName() + " to server: " + serverName);
		}
		catch(IOException eee){}
		p.sendPluginMessage(core.getNginxMC, "BungeeCord", b.toByteArray());
	}
}

class serverCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender cs, Command command, String s, String[] strings) {
		if(strings.length != 0) {
			if (strings[0].toLowerCase().equalsIgnoreCase("connect")) {
				if (strings.length == 3) {
					Player ps = null;
					if (Bukkit.getPlayer(strings[2]) != null) ps = Bukkit.getPlayer(strings[1]);
					if (ps != null) {
						ponlawat.sendLobby(ps, strings[1]);
					} else {
						cs.sendMessage("§7Server: §cthat player doesn't exist");
					}
				} else if (strings.length == 2) {
					ponlawat.sendLobby(((Player) cs), strings[1]);
				} else {
					cs.sendMessage("§7Server: §einvlid command.");
				}
			} else {
				cs.sendMessage("§7Server: §einvlid command.");
			}
		} else {
			cs.sendMessage("Unknown command. Type \"/help for help.\"");
		}
		return true;
	}
}

class lobbyevents implements Listener {
	@EventHandler
	public void interact(InventoryClickEvent e){
		if(e.getWhoClicked().getGameMode() == GameMode.ADVENTURE && (e.getSlotType() == InventoryType.SlotType.QUICKBAR || e.getSlotType() == InventoryType.SlotType.ARMOR)) e.setResult(Event.Result.DENY);
	}
	@EventHandler
	public void interact(PlayerInteractEvent e){
		if(e.getPlayer().getGameMode() == GameMode.ADVENTURE && e.getClickedBlock() != null && !(e.getAction() == Action.LEFT_CLICK_BLOCK && e.getItem().getType() == Material.BOW)) e.setCancelled(true);
	}
	@EventHandler
	public void blocks(BlockBreakEvent e){
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	@EventHandler
	public void blocks(BlockPlaceEvent e){
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	@EventHandler
	public void blocks(BlockDamageEvent e){
		if(e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
	}
	@EventHandler
	public void fly(PlayerToggleFlightEvent e){
		Player p = e.getPlayer();
		if(NginxPlayer.getNginxPlayer(p).getPlayerClass().getId() > 3 && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.ENTITY_IRONGOLEM_ATTACK, 10.0F, -10.0F);
			p.playEffect(p.getLocation(), org.bukkit.Effect.MOBSPAWNER_FLAMES, 10);

			Vector v = p.getLocation().getDirection().multiply(1).setY(1);
			p.setVelocity(v);
			p.setFlying(false);
			p.setAllowFlight(false);
		}
	}
	@EventHandler
	public void movement(PlayerMoveEvent e){
		Player p = e.getPlayer();
		if(NginxPlayer.getNginxPlayer(p) != null){
			if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
				//checkdoublejump
				if (NginxPlayer.getNginxPlayer(p).getPlayerClass().getId() > 3 && p.isOnGround() && !p.getAllowFlight()){
					p.setAllowFlight(true);
				}
			}
		}
	}
}