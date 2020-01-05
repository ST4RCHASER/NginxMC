package me.starchaser.nginxmc.bungee;

import me.starchaser.nginxmc.MySQL;
import me.starchaser.nginxmc.bungee.cms.gbc;
import me.starchaser.nginxmc.bungee.cms.ooc;
import me.starchaser.nginxmc.bungee.cms.op;
import me.starchaser.nginxmc.bungee.cms.report;
import me.starchaser.nginxmc.bungee.cms.punisher;
import me.starchaser.nginxmc.bungee.cms.hub_fun;
import me.starchaser.nginxmc.bungee.cms.lobby;
import me.starchaser.nginxmc.bungee.cms.wp;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.sound.sampled.Line;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static me.starchaser.nginxmc.bukkit.starchaser.getPlayerLobby;
import static me.starchaser.nginxmc.bukkit.starchaser.getSaltStringSet;

public class core extends Plugin {
    public static Connection SQL_CONNECTION;
    public static Plugin getBungeeDeluxe;
    public static MySQL sql = new MySQL("sql.siamcraft.net", "3306", "nginxmc", "siamcraft_plugin", "v6gAKopaMeK73ET78uCis7G2cib3wo");

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
        this.getProxy().getPluginManager().registerCommand(this, new wp());
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
                                            for (count = 0; res.next(); ++count) {
                                            }
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
/*
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().getScheduler()
                        .runAsync(core.getBungeeDeluxe,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        /////////////////////// Meteor ////////////////////////
                                        for (ProxiedPlayer p : getProxy().getPlayers()) {
                                            if (p.hasPermission("meteor.authtest." + getSaltStringSet(16))) {
                                                try {
                                                    ResultSet rss = SQL_CONNECTION.createStatement().executeQuery("SELECT * FROM meteor_sessions WHERE `username` = '" + p.getName() + "'");
                                                    if (!rss.isBeforeFirst()) {
                                                        p.sendMessage("§7Meteor: §cFailed to check session, you are logout form METEOR in 5 seconds!");
                                                        p.disconnect("§7Meteor: §cSession error!");
                                                    } else {
                                                        rss.next();
                                                        String locker_ip = rss.getString(3);
                                                        if (p.getAddress().getHostString().contains(locker_ip)) {
                                                            core.SQL_CONNECTION.createStatement().executeUpdate("UPDATE meteor_sessions SET bungee_check = '1', timeout_bungee = '10', bungee_sv_info = '" + p.getServer().getInfo().getName() + "', `address` = '" + p.getAddress().getHostString() + "' WHERE `username` = '" + p.getName() + "'");
                                                        } else {
                                                            p.disconnect("§7Meteor: §cCould not auth user " + p.getName() + " to meteor because LockerIP is not equal §7§o(" + locker_ip + "/" + p.getAddress().getHostString());
                                                            core.SQL_CONNECTION.createStatement().executeUpdate("DELETE FROM meteor_sessions WHERE `username` = '" + p.getName() + "'");
                                                        }
                                                    }
                                                } catch (SQLException exc) {
                                                }
                                            }
                                        }
                                        try {
                                            ResultSet resultSet = SQL_CONNECTION.createStatement().executeQuery("SELECT * FROM meteor_sessions");
                                            if (resultSet.isBeforeFirst()) {
                                                while (resultSet.next()) {
                                                    String username = resultSet.getString(2);
                                                    for (ProxiedPlayer o : getProxy().getPlayers()) {
                                                        if (o.getName().equalsIgnoreCase(username)) {
                                                            core.SQL_CONNECTION.createStatement().executeUpdate("UPDATE meteor_sessions SET bungee_check = '1', timeout_bungee = '10', bungee_sv_info = '" + o.getServer().getInfo().getName() + "', `address` = '" + o.getAddress().getHostString() + "' WHERE `username` = '" + o.getName() + "'");
                                                            if (!o.getAddress().getHostString().equalsIgnoreCase(resultSet.getString(3))) {
                                                                o.sendMessage("§7Meteor: §cFailed to check LockIP, you are logout form METEOR");
                                                                SQL_CONNECTION.createStatement().executeUpdate("DELETE FROM meteor_sessions WHERE `username` = '" + username + "'");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            ResultSet rs = core.SQL_CONNECTION.createStatement().executeQuery("SELECT * FROM meteor_core");
                                            while (rs.next()) {
                                                if (rs.getString(2).equalsIgnoreCase("kick")) {
                                                    String kick_str = rs.getString(3);
                                                    String username = kick_str.split("\\+")[0];
                                                    String r = kick_str.split("\\+")[1];
                                                    for (ProxiedPlayer z : getProxy().getPlayers()) {
                                                        if (z.getName().equalsIgnoreCase(username)) {
                                                            z.disconnect("§7Meteor: §c" + r.replaceAll("&", "§"));
                                                            core.SQL_CONNECTION.createStatement().executeUpdate("DELETE FROM meteor_core WHERE `pk` = '" + rs.getString(1) + "'");
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (SQLException exc) {

                                        }


                                        /////////////////////// Restart Server ////////////////////////
                                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                                        Date date = new Date(System.currentTimeMillis());
                                        String time = formatter.format(date);
                                        if (time.contains("00:00:00") || time.contains("00:00:01") || time.contains("00:00:02") || time.contains("00:00:03") || time.contains("00:00:04") || time.contains("00:00:05") || time.contains("00:00:06")) {
                                            getProxy().stop("§cรอแปปนึงกำลังรีเซิร์ฟ");
                                            getProxy().stop();
                                        }
                                        if (getProxy().getServerInfo("Lobby/01").getPlayers().size() >= 17) {
                                            ProxiedPlayer target = getProxy().getServerInfo("Lobby/01").getPlayers().iterator().next();
                                            target.sendMessage("§7Lobby: §eคุณกำลังถูกย้ายเนื่องจากเซิร์ฟเวอร์ที่คุณอยู่กำลังจะเต็ม!");
                                            sendToServer(target, getProxy().getServerInfo("Lobby"));

                                        }
                                        if (getProxy().getServerInfo("Lobby/02").getPlayers().size() >= 17) {
                                            ProxiedPlayer target = getProxy().getServerInfo("Lobby/02").getPlayers().iterator().next();
                                            target.sendMessage("§7Lobby: §eคุณกำลังถูกย้ายเนื่องจากเซิร์ฟเวอร์ที่คุณอยู่กำลังจะเต็ม!");
                                            sendToServer(target, getProxy().getServerInfo("Lobby"));
                                        }
                                        if (getProxy().getServerInfo("Lobby/03").getPlayers().size() >= 17) {
                                            ProxiedPlayer target = getProxy().getServerInfo("Lobby/03").getPlayers().iterator().next();
                                            target.sendMessage("§7Lobby: §eคุณกำลังถูกย้ายเนื่องจากเซิร์ฟเวอร์ที่คุณอยู่กำลังจะเต็ม!");
                                            sendToServer(target, getProxy().getServerInfo("Lobby"));
                                        }
                                    }
                                }
                        );
            }
        }, 1, 1, TimeUnit.SECONDS);
*/
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
        p.connect(sv);
        p.sendMessage(ChatColor.GRAY + "Portal: " + ChatColor.YELLOW + "กำลังย้ายคุณจากเซิฟเวอร์ " + p.getServer().getInfo().getName() + " -> " + sv.getName());
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
