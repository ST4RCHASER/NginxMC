package me.starchaser.nginxmc.bukkit;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class mvdwpapi {
    public static void registerMCdWAPI() {
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_level", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return np.getLevel().getStr();
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_xp", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getLevel().getXP());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_level_raw", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getLevel().get_Int());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_xp_percent", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getLevel().getXPPercent());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_xp_max", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getLevel().getMaxXP());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_xp_bar", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return np.getLevel().getXPBar();
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_id", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getId());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_coins", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getCoins());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_ooc", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getOOC_Count());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_title_str", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return np.getTitle().getStr();
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_title_id", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getTitle().getId());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_paid_points", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getPaid_points());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_reward_points", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getReward_points());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_lobby_id", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return String.valueOf(np.getLobby_Number());
            }
        });
        PlaceholderAPI.registerPlaceholder(core.getNginxMC, "nginx_rank_animate_frame", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np == null || e.getPlayer() == null) return "§r §7§o(Loading...) §r";
                return np.getRankAnimateCurrentFrame();
            }
        });
    }
}
