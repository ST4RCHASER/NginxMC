package me.starchaser.nginxmc.bungee.cms;


import me.starchaser.nginxmc.bungee.core;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import java.sql.ResultSet;
import java.sql.SQLException;

public class gbc extends Command implements Listener {
    public gbc() {
        super("gbc");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        int id = 0;
        ProxiedPlayer player = (ProxiedPlayer)sender;
        if (player.getServer().getInfo().getName().equalsIgnoreCase("lobbymaster")) {
            player.sendMessage(ChatColor.GRAY + "CORE: " + ChatColor.RED + "/gbc now allow on this server!");
        } else if (player.getServer().getInfo().getName().equalsIgnoreCase("auth")) {
            player.sendMessage(ChatColor.GRAY + "CORE: " + ChatColor.RED + "/gbc now allow on this server!");
        } else {
            try {
                id = core.getClassID(player);
            } catch (Exception var6) {
                var6.printStackTrace();
            }
            if (id < 9) {
                sender.sendMessage("§7Nginx: §cSorry, you not have permission to use this command!");
            } else if (args.length == 0) {
                sender.sendMessage("§7GlobalBroadcast: §ausage: /gbc <message>");
            } else {
                ProxyServer.getInstance().broadcast(ChatColor.RESET + "");
                ProxyServer.getInstance().broadcast(ChatColor.RESET + "");
                ProxyServer.getInstance().broadcast(ChatColor.RED + "" + ChatColor.BOLD + "       System Announcement: " + ChatColor.DARK_GRAY + "(" + ChatColor.GOLD + sender.getName() + ChatColor.DARK_GRAY + ")");
                ProxyServer.getInstance().broadcast(ChatColor.RESET + "");
                ProxyServer.getInstance().broadcast(ChatColor.GOLD + "  " + String.join(" ", args).replaceAll("&", "§"));
                ProxyServer.getInstance().broadcast(ChatColor.RESET + "");
                ProxyServer.getInstance().broadcast(ChatColor.RESET + "");
            }
        }
    }
}
