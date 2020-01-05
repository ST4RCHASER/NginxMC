package me.starchaser.nginxmc.bukkit;

import me.starchaser.nginxmc.bukkit.NMSManage.v1_12_R1;
import me.starchaser.nginxmc.bukkit.NMSManage.v1_12_R1_Events;
import me.starchaser.nginxmc.bukkit.NMSManage.v1_8_R3;
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
import java.sql.SQLException;
import java.util.ArrayList;

public class ponlawat {
	public static ArrayList<String> join_playerdata = new ArrayList<>();
	public static ArrayList<String> current_playerdata = new ArrayList<>();
	public static ArrayList<Object[]> done_playerdata = new ArrayList<>();
	public static String version;

	public static String getJoin_playerdata(String p) {
		for (String k : join_playerdata)
			if (k.toLowerCase().equalsIgnoreCase(p.toLowerCase())) return k;
		return null;
	}
	public static boolean removeJoin_playerdata(String p) {
		for (String k : join_playerdata)
			if (k.toLowerCase().equalsIgnoreCase(p.toLowerCase())) {
				join_playerdata.remove(k);
				return true;
			}
		return false;
	}
	public static String getCurrent_playerdata(String p) {
		for (String k : current_playerdata)
			if (k.toLowerCase().equalsIgnoreCase(p.toLowerCase())) return k;
		return null;
	}
	public static Object[] getDone_playerdata(String p, ArrayList<Object[]> customObj) {
		if (customObj != null) {
			for (Object[] k : customObj)
				if (((String) k[1]).toLowerCase().equalsIgnoreCase(p.toLowerCase())) return k;
		} else {
			for (Object[] k : done_playerdata)
				if (((String) k[1]).toLowerCase().equalsIgnoreCase(p.toLowerCase())) return k;
		}
		return null;
	}

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
	public static Boolean setUpNms() {
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

			switch (version) {
				case "v1_8_R3":
					core.nms = new v1_8_R3();
					break;
				case "v1_12_R1": case "v1_15_R1":
					core.nms = new v1_12_R1();
					Bukkit.getPluginManager().registerEvents(new v1_12_R1_Events(), core.getNginxMC);
					break;
			}
			if (core.nms != null) {
				core.getNginxMC.getLogger().info("§7Your server is running version §e" + version);
				return true;
			} else {
				core.getNginxMC.getLogger().info("§7This plugin doesn't support for §e" + version + " §7disabled...");
				Bukkit.getPluginManager().disablePlugin(core.getNginxMC);
				return false;
			}
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			core.getNginxMC.getLogger().info("§7This plugin cannot find the server version. disabled...");
			Bukkit.getPluginManager().disablePlugin(core.getNginxMC);
			return false;
		}
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
		if(e.getWhoClicked().getGameMode() == GameMode.ADVENTURE) e.setResult(Event.Result.DENY);
	}
	@EventHandler
	public void interact(InventoryInteractEvent e){
		if(e.getWhoClicked().getGameMode() == GameMode.ADVENTURE) e.setResult(Event.Result.DENY);
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
		if(NginxPlayer.getNginxPlayer(p) != null && NginxPlayer.getNginxPlayer(p).getPlayerClass().getId() > 3 && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
			e.setCancelled(true);
			p.playSound(p.getLocation(), core.getNms().getSound("IRONGOLEM_THROW"), 10.0F, -10.0F);
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