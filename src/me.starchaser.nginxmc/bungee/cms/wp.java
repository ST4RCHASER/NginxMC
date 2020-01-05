package me.starchaser.nginxmc.bungee.cms;


import me.starchaser.nginxmc.bungee.core;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;

public class wp extends Command implements Listener {
    public wp() {
        super("wp");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (player.getServer().getInfo().getName().equalsIgnoreCase("lobbymaster")) {
            player.sendMessage(ChatColor.GRAY + "CORE: " + ChatColor.RED + "/wp not allow on this server!");
        } else if (player.getServer().getInfo().getName().equalsIgnoreCase("auth")) {
            player.sendMessage(ChatColor.GRAY + "CORE: " + ChatColor.RED + "/wp not allow on this server!");
        } else {
            ProxyServer.getInstance().getScheduler()
                    .runAsync(core.getBungeeDeluxe,
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ResultSet result = core.getGetBungeeConn().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + player.getName() + "'");
                                        if (result.next()) {
                                            int wp_count = result.getInt("wp");
                                            TextComponent t3 = new TextComponent("§7WarnPoints: §eจำนวน WarnPoints ของคุณตอนนี้คือ §f" + wp_count);
                                            player.sendMessage(new ComponentBuilder(t3).create());
                                            return;
                                        }
                                    } catch (SQLException var7) {
                                        var7.printStackTrace();
                                    }
                                }
                            }
                    );
        }
    }
}
