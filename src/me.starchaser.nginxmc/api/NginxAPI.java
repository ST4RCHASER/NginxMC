package me.starchaser.nginxmc.api;

import me.starchaser.nginxmc.bukkit.NginxPlayer;
import me.starchaser.nginxmc.bukkit.core;
import me.starchaser.nginxmc.bukkit.events;
import me.starchaser.nginxmc.bukkit.starchaser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class NginxAPI {
    public NginxAPI(Plugin plugin){
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }else {
            starchaser.Logger(starchaser.LOG_TYPE.PLUGIN, "accepted request api form plugin " + plugin.getName());
        }
    }
    public NginxPlayer getNginxPlayer(Player player) {
        return NginxPlayer.getNginxPlayer(player);
    }
    public void ReloadPlayerData(Player player , Boolean save) {
        if (save) {
            NginxPlayer.removeNginxPlayer(player);
            (new BukkitRunnable() {
                public void run() {
                    events.FastJoinTask(player);
                    this.cancel();
                }
            }).runTaskTimerAsynchronously(core.getNginxMC, 1L, 1L);
        }else{
                starchaser.sendPlayerData(player);
                (new BukkitRunnable() {
                    public void run() {
                        try {
                            NginxPlayer.removeNginxPlayer(player);
                            this.cancel();
                        } catch (NullPointerException var2) {
                            this.cancel();
                        }

                    }
                }).runTaskTimerAsynchronously(core.getNginxMC, 20L, 20L);
            (new BukkitRunnable() {
                public void run() {
                    events.FastJoinTask(player);
                    this.cancel();
                }
            }).runTaskTimerAsynchronously(core.getNginxMC, 40L, 40L);
            }
        }
}
