package me.starchaser.nginxmc.api;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class NginxAPI_Bungee {
    public NginxAPI_Bungee(Plugin plugin){
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }else {
            ProxyServer.getInstance().getConsole().sendMessage("NginxBungee: accepted request api form plugin " + plugin.getDescription().getName());
        }
    }
}
