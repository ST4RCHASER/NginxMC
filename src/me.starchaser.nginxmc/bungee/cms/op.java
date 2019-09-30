package me.starchaser.nginxmc.bungee.cms;

import me.starchaser.nginxmc.bungee.core;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;

import static me.starchaser.nginxmc.bungee.core.getBungeeDeluxe;

public class op extends Command implements Listener {
    public op() {
        super("op");
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§7NginxMC: §cSorry! /op not allow in games, please use in console!");
        for (ProxiedPlayer pp : getBungeeDeluxe.getProxy().getPlayers()) {
            int sender_class = core.getClassID(pp);
            if (sender_class >= 6) {
                pp.sendMessage("§7NginxMC: §c" + sender.getName() + " Try to use /op in game!");
            }
        }
    }
}
