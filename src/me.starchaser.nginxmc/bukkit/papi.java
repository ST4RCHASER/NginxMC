package me.starchaser.nginxmc.bukkit;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;

public class papi extends EZPlaceholderHook {

    public papi(core plu) {
        super(plu, "nginx");
    }
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(player == null) {
            return "";
        }
        NginxPlayer np = NginxPlayer.getNginxPlayer(player);
        if (np == null) return "§r §7§o(Loading...) §r";
        if(identifier.equals("level")) {return np.getLevel().getStr();}
        if(identifier.equals("xp")) {return String.valueOf(np.getLevel().getXP());}
        if(identifier.equals("level_raw")) {return String.valueOf(np.getLevel().get_Int());}
        if(identifier.equals("xp_percent")) {return String.valueOf(np.getLevel().getXPPercent());}
        if(identifier.equals("xp_max")) {return String.valueOf(np.getLevel().getMaxXP());}
        if(identifier.equals("xp_bar")) {return np.getLevel().getXPBar();}
        if(identifier.equals("id")) {return String.valueOf(np.getId());}
        if(identifier.equals("coins")) {return String.valueOf(np.getCoins());}
        if(identifier.equals("ooc")) {return String.valueOf(np.getOOC_Count());}
        if(identifier.equals("title_str")) {return np.getTitle().getStr();}
        if(identifier.equals("title_id")) {return String.valueOf(np.getTitle().getId());}
        if(identifier.equals("paid_points")) {return String.valueOf(np.getPaid_points());}
        if(identifier.equals("reward_points")) {return String.valueOf(np.getReward_points());}
        if(identifier.equals("lobby_id")) {return String.valueOf(np.getLobby_Number());}
        if(identifier.equals("rank_animate_frame")) {return String.valueOf(np.getRankAnimateCurrentFrame());}
        return "";
    }
}
