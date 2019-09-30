package me.starchaser.nginxmc.bungee;

import me.starchaser.nginxmc.MySQL;
import me.starchaser.nginxmc.bungee.cms.gbc;
import me.starchaser.nginxmc.bungee.cms.ooc;
import me.starchaser.nginxmc.bungee.cms.op;
import me.starchaser.nginxmc.bungee.cms.report;
import me.starchaser.nginxmc.bungee.cms.punisher;
import me.starchaser.nginxmc.bungee.cms.hub_fun;
import me.starchaser.nginxmc.bungee.cms.lobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class core extends Plugin {
    public static Connection SQL_CONNECTION;
    public static Plugin getBungeeDeluxe;
    public static MySQL sql = new MySQL("localhost", "3306", "nginxmc", "siamcraft_plugin", "v6gAKopaMeK73ET78uCis7G2cib3wo");

    @Override
    public void onEnable() {
        getBungeeDeluxe = this;
        getProxy().getConsole().sendMessage(new TextComponent("§f[§bServerManager§f] §eStarting plugin at 'Bungee Mode'"));
        this.getProxy().getPluginManager().registerListener(this, new evt());
        this.getProxy().getPluginManager().registerCommand(this, new report());
        this.getProxy().getPluginManager().registerCommand(this, new punisher());
        this.getProxy().getPluginManager().registerCommand(this, new ooc());
        this.getProxy().getPluginManager().registerCommand(this, new gbc());
        this.getProxy().getPluginManager().registerCommand(this, new op());
        this.getProxy().getPluginManager().registerCommand(this, new lobby());
        this.getProxy().getPluginManager().registerCommand(this, new hub_fun());
        try {
            SQL_CONNECTION = sql.openConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            getProxy().getConsole().sendMessage(new TextComponent("§f[§bSQLManager§f] §cERROR! on SQL connection please check error!"));
            getProxy().getConsole().sendMessage(new TextComponent("§f[§bServerManager§f] §cProxy is stoping due to critical error!"));
            ProxyServer.getInstance().stop();
        }

        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().getScheduler()
                        .runAsync(core.getBungeeDeluxe,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            int count;
                                            Statement statement = core.getGetBungeeConn().createStatement();
                                            ResultSet res = statement.executeQuery("SELECT * FROM `report_log` WHERE status = 0");
                                            res.beforeFirst();
                                            for (count = 0; res.next(); ++count) {}
                                            if (count < 1) return;
                                            for (ProxiedPlayer pp : getBungeeDeluxe.getProxy().getPlayers()) {
                                                int sender_class = core.getClassID(pp);
                                                if (sender_class >= 6) {
                                                    pp.sendMessage("§7Report: §cมีรายงานที่ค้างอยู่ §7" + count + "§c รายการ สามารถตรวจสอบได้โดยการพิมพ์: §7§o'/report admin view'");
                                                }
                                            }
                                        } catch (Exception eex) {
                                        }
                                    }
                                });
            }
        }, 1, 10, TimeUnit.MINUTES);
    }

    public static Connection getGetBungeeConn() {
        try {
            SQL_CONNECTION = sql.openConnection();
            return SQL_CONNECTION;
        } catch (Exception ex) {
            ex.printStackTrace();
            getBungeeDeluxe.getProxy().getConsole().sendMessage(new TextComponent("§f[§bSQLManager§f] §cERROR! on get SQL Connection for use!"));
        }
        return null;
    }

    public static void sendToServer(ProxiedPlayer p, ServerInfo sv) {
        try {
            p.connect(sv);
            p.sendMessage(ChatColor.GRAY + "Portal: " + ChatColor.YELLOW + "คุณถูกย้าย " + p.getServer().getInfo().getName() + " > " + sv.getName());
        } catch (Exception ee) {

        }
    }

    public static void sendhelpinfo(ProxiedPlayer p) {
        p.sendMessage(ChatColor.GRAY + "Nginx: " + ChatColor.YELLOW + "This server run as §aNginxMC §fv§a" + getBungeeDeluxe.getDescription().getVersion());
        p.sendMessage(ChatColor.GRAY + "Nginx: " + ChatColor.YELLOW + " by _StarChaser w/ siamcraft mode");
    }

    public static int getClassID(ProxiedPlayer p) {
        int id = 0;
        for (int i = 0; i < 10; i++) {
            if (p.hasPermission("nginx.class.grant." + i)) {
                if (i > id) id = i;
            }
        }
        return id;
    }
}
