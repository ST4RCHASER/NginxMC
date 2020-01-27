package me.starchaser.nginxmc.bungee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


public class evt extends Plugin implements Listener{
    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        connected_player.add(event.getPlayer());
        ProxyServer.getInstance().getScheduler()
                .runAsync(core.getBungeeDeluxe,
                        new Runnable() {
                            @Override
                            public void run() {
                                ProxyServer.getInstance().getLogger().info(ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.GREEN + " has connected the network. (" + ProxyServer.getInstance().getPlayers().size() + ")");
                                try {
                                    ResultSet result_player = core.getGetBungeeConn().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + event.getPlayer().getName() + "'");
                                    if (!result_player.isBeforeFirst()) {
                                        core.getBungeeDeluxe.getProxy().getConsole().sendMessage("§f[§bPlayerManager§f] §bAccount §7" + event.getPlayer().getName() + "§b not found Creating...");
                                        CreateAccount(event.getPlayer());
                                    } else {
                                        result_player.next();
                                        int wp_count = result_player.getInt("wp");
                                        if (wp_count > 4) {
                                            TextComponent t1 = new TextComponent(
                                                    "§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c* \n" +
                                                            "§7สวัสดี §f\"§e" + event.getPlayer().getName() + "§f\"\n" +
                                                            "§7ตัวละครของคุณอยู่ในสถานะ §f\"§cถูกระงับการใช้งาน§f\"\n" +
                                                            "§7เพราะคุณได้กระทำความผิดเกินที่เซิฟเวอร์ได้กำหนดใว้ ซื่งในที่นี้ก็คือ §e3 §7ครั้งขึ้นไป\n" +
                                                            "\n§7หากพบข้อสงสัยหรือสอบถามใดๆ โปรดติตต่อที่เพจ §6Minecraft Siamcraft\n" +
                                                            "\nhttps://goo.gl/nR49Zp\n" +
                                                            "" +
                                                            "\n§7*จำนวน §cwarnpoint §7ของคุณตอนนี้: §f" + wp_count + "\n" +
                                                            "§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*"
                                            );
                                            event.getPlayer().disconnect(new ComponentBuilder(t1).create());
                                        }
                                    }
                                } catch (SQLException var3) {
                                    var3.printStackTrace();
                                    core.getBungeeDeluxe.getProxy().getConsole().sendMessage("§f[§bPlayerManager§f] §cError on get player data... (TASK: evt.PlayerJoinEvent) [" + event.getPlayer().getName() + "]");
                                    return;
                                }
                            }
                        }
                );
    }
    public static void CreateAccount(ProxiedPlayer player) {
        ProxyServer.getInstance().getScheduler()
                .runAsync(core.getBungeeDeluxe,
                        new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    core.getGetBungeeConn().createStatement().executeUpdate("INSERT INTO `nginxmc`.`players` (`id`, `username`, `ooc`, `level`, `xp`, `title`, `coins`, `feather`, `wp`) VALUES (NULL, '" + player.getName() + "', '0', '1', '0', '0', '0', '0', '0');");
                                    core.getBungeeDeluxe.getProxy().getConsole().sendMessage("§f[§bPlayerManager§f] §aAccount Created! [" + player.getName() + "]");
                                } catch (SQLException var2) {
                                    var2.printStackTrace();
                                    core.getBungeeDeluxe.getProxy().getConsole().sendMessage("§f[§bPlayerManager§f] §cError on create account... (TASK: evt.createaccount) [" + player.getName() + "]");
                                }
                            }
                        }
                );
    }
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxyServer.getInstance().getLogger().info(ChatColor.YELLOW + event.getPlayer().getName() + ChatColor.RED + " has disconnected the network. (" + (ProxyServer.getInstance().getPlayers().size() - 1) + ")");
    }

    public static ArrayList<ProxiedPlayer> connected_player = new ArrayList<>();
    @EventHandler
    public void onSwitch(ServerConnectEvent event) {
        if (event.getTarget() == null || event.getPlayer() == null || event.getPlayer().getServer() == null) return;
        if (connected_player.contains(event.getPlayer())) {
            event.setCancelled(false);
            connected_player.remove(event.getPlayer());
            return;
        }else {
            event.setCancelled(true);
        }
        ServerInfo target_server = event.getTarget();
        ProxiedPlayer target = event.getPlayer();
        ProxyServer.getInstance().getScheduler()
                .runAsync(core.getBungeeDeluxe,
                        new Runnable() {
                            @Override
                            public void run() {
                                Server sv = event.getPlayer().getServer();
                                if (sv != null) {
                                    ServerInfo info = event.getPlayer().getServer().getInfo();
                                    if (info != null && info.getName().toLowerCase().contains("lobby")) {
                                        try {
                                            ResultSet result = core.getGetBungeeConn().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + event.getPlayer().getName() + "'");
                                            if (result.next()) {
                                                int wp_count = result.getInt("wp");
                                                if (wp_count > 2) {
                                                    TextComponent t1 = new TextComponent(
                                                            "§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c* \n" +
                                                                    "§7ตัวละครของคุณอยู่ในสถานะ §f\"§cถูกระงับการใช้งาน§f\"\n" +
                                                                    "§7เพราะคุณได้กระทำความผิดเกินที่เซิฟเวอร์ได้กำหนดใว้ ซื่งในที่นี้ก็คือ §e3 §7ครั้งขึ้นไป\n" +
                                                                    "\n" +
                                                                    "§7หากพบข้อสงสัยหรือสอบถามใดๆ โปรดติตต่อที่เพจ §6");
                                                    TextComponent t2 = new TextComponent("Minecraft Siamcraft");
                                                    t2.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://goo.gl/nR49Zp"));
                                                    t2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("คลิกที่นี่เพื่อเข้าสู่เพจของเซิฟเวอร์").create()));
                                                    TextComponent t3 = new TextComponent("\n§7*จำนวน §cwarnpoint §7ของคุณตอนนี้: §f" + wp_count + "\n" +
                                                            "§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*");

                                                    target.sendMessage(new ComponentBuilder(t1).append(t2).create());
                                                    target.sendMessage(new ComponentBuilder(t3).create());
                                                    return;
                                                }
                                            }
                                        } catch (SQLException var7) {
                                            var7.printStackTrace();
                                        }
                                    }

                                }
                                connected_player.add(target);
                                core.sendToServer(target , target_server);
                            }
                        }
                );
    }
}
