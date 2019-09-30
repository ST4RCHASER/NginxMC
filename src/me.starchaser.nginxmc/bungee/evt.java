package me.starchaser.nginxmc.bungee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
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
                                    if (info != null && info.equals(ProxyServer.getInstance().getServerInfo("Lobby"))) {
                                        try {
                                            ResultSet result = core.getGetBungeeConn().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + event.getPlayer().getName() + "'");
                                            if (result.next()) {
                                                int wp_count = result.getInt("wp");
                                                if (wp_count > 2) {
                                                    target.sendMessage("§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*");
                                                    target.sendMessage(ChatColor.WHITE + "ตัวละครของคุณอยู่ในสถานะ ''ถูกระงับการใช้งาน'' นั้นอาจจะเป็นเพราะว่าคุณได้กระทำผิดเกิน 3 ครั้งจึงถูกระงับ หากพบข้อสงสัยหรือสอบถามใดๆ โปรดติตต่อที่เพจ Siamcraft https://goo.gl/nR49Zp สามารถคลิกได้ที่นี้ ขอบคุณครับ (จำนวน warnpoint ของคุณตอนนี้: " + wp_count + " )");
                                                    target.sendMessage("§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*§f-§c*");
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
