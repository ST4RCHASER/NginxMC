package me.starchaser.nginxmc.bukkit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import static me.starchaser.nginxmc.bukkit.core.AnimemateRankList;
import static me.starchaser.nginxmc.bukkit.core.debug;

public class NginxPlayer {
    final String name;
    private NginxPlayer.NginxTitle title;
    private NginxPlayer.PlayerClass playerClass;
    private NginxPlayer.PlayerLevel player_level;
    private int id;
    private int coins;
    private String is_enter_password;
    private String server_target;
    private int feather_points;
    private int paid_points = 0;
    private int ooc_count = 0;
    private int reward_points = 0;
    private boolean show_title;
    private boolean force_hide_title = false;
    private boolean chat_pop = false;
    private int Lobby_Number = 0;
    private boolean is_removed = false;
    private Team player_sb_team;
    NginxPlayer(int id, final String name, int class_id, int level, int xp, int title_id, int coins, boolean show_title_on_head, int feather_point) {
        this.id = id;
        this.name = name;
        this.coins = coins;
        this.playerClass = new PlayerClass(class_id);
        this.title = new NginxTitle(title_id);
        this.player_level = new PlayerLevel(level, xp, getName());
        this.feather_points = feather_point;
        this.show_title = show_title_on_head;
        setChatPOP(core.server_chat_pop);
        if (starchaser.servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            //FillLobby();
            JoinLobby(1);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getPlayer() == null || is_removed) {
                    this.cancel();
                    return;
                }
                paid_points = getNewPaidPoints();
                ooc_count = getNewOOC_Count();
                reward_points = getNewRewardPoints();
            }
        }.runTaskTimerAsynchronously(core.getNginxMC , 1,2400);
    }
    public NginxPlayer.PlayerClass getPlayerClass() {
        return this.playerClass;
    }

    public int getLobby_Number() {
        return Lobby_Number;
    }

    public void setLobby_Number(int lobby_Number) {
        Lobby_Number = lobby_Number;
        if (getPlayer() != null) {
            //getPlayer().sendMessage("§7Lobby: §aคุณอยู่ใน Lobby: §e" + getLobby_Number());
            //getPlayer().playSound(getPlayer().getLocation(), Sound.LEVEL_UP,3.0F, 3.533F);
        }
    }
    public void JoinLobby(int lobby_Number) {
        if (starchaser.getPlayerLobby(lobby_Number).size() >= starchaser.virtual_lobby_player_size) {
            getPlayer().sendMessage("§7Lobby: §cล๊อบบี้นี้เต็มแล้ว!");
        }else {
            setLobby_Number(lobby_Number);
        }
    }
    public void FillLobby() {
        int a = new Random().nextInt((starchaser.gamemode_virtual_lobby_size - 1) + 1) + 1;
        while (starchaser.getPlayerLobby(a).size() >= starchaser.virtual_lobby_player_size) {
            a = new Random().nextInt((starchaser.gamemode_virtual_lobby_size - 1) + 1) + 1;
        }
        JoinLobby(a);
    }

    private int getNewPaidPoints(){
        try {
            ResultSet res_point = core.getSqlConnectionAuthme().createStatement().executeQuery("SELECT * FROM `authme` WHERE `username` LIKE '" + getName() + "'");
            res_point.next();
            return  res_point.getInt("mcshop_points");
        }catch (Exception exx){
            exx.printStackTrace();
            return -1;
        }
    }
    private int getNewRewardPoints(){
        try {
            ResultSet res_point = core.getSqlConnectionAuthme().createStatement().executeQuery("SELECT * FROM `authme` WHERE `username` LIKE '" + getName().toLowerCase() + "'");
            res_point.next();
            return  res_point.getInt("mcshop_rp");
        }catch (Exception exx){
            exx.printStackTrace();
            return -1;
        }
    }
    public void setReward_points(int value){
        try {
            core.getSqlConnectionAuthme().createStatement().executeQuery("UPDATE `siamcraft`.`authme` SET `mcshop_rp`='"+value+"' WHERE `username`="+getName()+";");
            reward_points = getNewRewardPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addRedward_points(int value){
        reward_points = getNewRewardPoints();
        setReward_points(reward_points+value);
    }
    public void takeReward_points(int value){
        reward_points = getNewRewardPoints();
        setReward_points(reward_points-value);
    }
    public void setPaid_points(int value){
        try {
            core.getSqlConnectionAuthme().createStatement().executeQuery("UPDATE `siamcraft`.`authme` SET `mcshop_points`='"+value+"' WHERE `username`="+getName()+";");
            paid_points = getNewPaidPoints();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addPaid_points(int value){
        paid_points = getNewPaidPoints();
        setPaid_points(paid_points+value);
    }
    public void takePaid_points(int value){
        paid_points = getNewPaidPoints();
        setPaid_points(paid_points-value);
    }
    public NginxPlayer.NginxTitle getTitle() {
        return this.title;
    }

    public void setTitle(int id) {
        this.title = new NginxTitle(id);
    }
    public void setHideTitle(Boolean value ){
        force_hide_title = value;
    }

    public int getFeather_points() {
        return this.feather_points;
    }

    public int getPaid_points() {
        return this.paid_points;
    }

    public int getReward_points() {
        return reward_points;
    }

    public boolean isShow_title() {
        return this.show_title;
    }
    public NginxPlayer.PlayerLevel getLevel() {
        return this.player_level;
    }

    public int getOOC_Count() {
        return ooc_count;
    }
    private int getNewOOC_Count(){
        try {
            ResultSet res = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `id` = " + this.getId() + ";");
            res.next();
            return res.getInt("ooc");
        } catch (SQLException var3) {
            var3.printStackTrace();
            starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "Error on geting ooc count for player " + this.getName());
            return -1;
        }
    }

    public boolean setOOC_Count(int count) {
        try {
            core.getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `ooc` = '" + count + "' WHERE `players`.`id` = " + this.getId() + ";");
            ooc_count = getNewOOC_Count();
            return true;
        } catch (SQLException var3) {
            var3.printStackTrace();
            return false;
        }
    }

    public void addOOC_Count(int num , boolean send_message) {
        if (getBukkitPlayer() != null && send_message) {
            getBukkitPlayer().sendMessage("§7OOC: §eคุณได้รับ OOC เป็นจำนวน §6" + num + "§e หน่วย");
        }
        ooc_count = getNewOOC_Count();
        setOOC_Count(num+ooc_count);
    }
    public void takeOOC_Count(int num) {
        ooc_count = getNewOOC_Count();
        setOOC_Count(ooc_count-num);
    }

    public void setCoins(int num) {
        this.coins = num;
    }

    public void addCoins(int num, boolean send_message) {
        if (getBukkitPlayer() != null && send_message) {
            getBukkitPlayer().sendMessage("§7Coins: §eคุณได้รับ Coins เป็นจำนวน §6" + num + "§e เหรียญ");
        }
        setCoins(this.coins + num);
    }
    public void takeCoins(int num) {
        setCoins(this.coins - num);
    }
    public void setChatPOP(Boolean value){
        chat_pop = value;
    }
    public boolean isChatPOPEnabled(){
        return chat_pop;
    }
    public String isEnterPassword() {
        return this.is_enter_password;
    }

    public void setEnterPassword(String b) {
        this.is_enter_password = b;
    }

    public String getTargetServer() {
        return this.server_target;
    }

    public void setTargetServer(String b) {
        this.server_target = b;
    }

    public int getId() {
        return this.id;
    }

    public int getCoins() {
        return this.coins;
    }

    public Player getPlayer() {
        Player ppz = Bukkit.getPlayer(name);
        return ppz;
    }
    public String getName(){
        return name;
    }

    public static NginxPlayer getNginxPlayer(Player p) {
        for (NginxPlayer nginxPlayer : core.PlayerRef) {
            if (nginxPlayer.getName().equalsIgnoreCase(p.getName())) {
                return nginxPlayer;
            }
        }
        return null;
    }

    public static void addNginxPlayer(NginxPlayer dp) {
        core.PlayerRef.add(dp);
    }

    public static void removeNginxPlayer(Player p) {
        NginxPlayer npz = null;
            for (NginxPlayer np : core.PlayerRef) {
                if (np.getName().equalsIgnoreCase(p.getName())) {
                    npz = np;
                }
            }
            if (npz != null) {
                removeNginxPlayer(npz);
            }
    }

    public void setRemoved() {
        is_removed = true;
    }
    public static void removeNginxPlayer(NginxPlayer np) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    np.setRemoved();
                    np.player_sb_team.unregister();
                }catch (Exception exc) {
                    if (debug) {
                        exc.printStackTrace();
                    }
                }
                core.PlayerRef.remove(np);
                starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§fPlayer memory removed! [§e" + np.getName() + "§f]");
            }
        }.runTaskAsynchronously(core.getNginxMC);
    }
    public class NginxTitle {
        private int title_id;
        private String title_data;

        NginxTitle(Integer id) {
            this.title_id = id;
            try {
                ResultSet res = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `title_data` WHERE `id` = " + this.title_id + "");
                if (res.isBeforeFirst()) {
                    this.title_data = "ERROR_TITLE_ID_"+title_id+"_NOT_FOUND";
                }
                res.next();
                this.title_data = res.getString("name").replaceAll("&", "§");
            } catch (Exception var4) {
                var4.printStackTrace();
                starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§cError on get title data... (TASK: NginxTitle.NginxTitle) [ID:" + this.title_id + "]");
                this.title_data = "§r";
            }

        }

        public String getStr() {
            return this.title_data;
        }

        public int getId() {
            return this.title_id;
        }
    }
    public Player getBukkitPlayer()  {
        return Bukkit.getPlayerExact(getName());
    }
    public class PlayerClass{
        private int id;
        public void reloadClassID() {
            this.id = starchaser.getClassID(getBukkitPlayer());
            updateRankLine(false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    updateRankLine(false);
                }
            }.runTaskLaterAsynchronously(core.getNginxMC , 20L);
        }

        public void updateRankLine(Boolean ministr_enable) {
            String ministr;
            if (ministr_enable) {
                if (NginxPlayer.this.playerClass.getId() > 0) {
                    ministr = NginxPlayer.this.playerClass.getStr().substring(3) + "§f";
                } else {
                    ministr = "§b§lSC §7";
                }
            }else {
                if (NginxPlayer.this.playerClass.getId() > 0) {
                    ministr = playerClass.getStr().replaceFirst(" " , "") + "§7";
                } else {
                    ministr = "§b§lSC §7";
                }
            }
            ministr = ministr.replaceAll(" §r" , " ").replaceAll("§r " , "");
            try {
                if (player_sb_team != null) {
                    player_sb_team.setPrefix(ministr);
                }
            }catch (Exception exa) {

            }
        }
        public int getId() {
            return this.id;
        }
        public String getStr() {
            String p_class_str;
            if (this.id == 9) {
                p_class_str = " §c§lADMIN ";
            } else if (this.id == 7) {
                p_class_str = " §c§lYOU§f§lTUBER ";
            } else if (this.id == 6) {
                p_class_str = " §5§lSTAFF ";
            } else if (this.id == 5) {
                p_class_str = " §c§lSUPREME ";
            } else if (this.id == 4) {
                p_class_str = " §a§lLEGEND ";
            } else if (this.id == 3) {
                p_class_str = " §6§lMASTER ";
            } else if (this.id == 2) {
                p_class_str = " §d§lHERO ";
            } else if (this.id == 1) {
                p_class_str = " §b§lTITAN ";
    } else if (this.id == 0) {
        p_class_str = " §b§lSC ";
    } else {
        p_class_str = "§r §cERROR_CLASS_ID_" + this.id + "_NOT_FOUND §r";
    }

            return p_class_str;
}

        PlayerClass(int id) {
            this.id = id;
        }
    }

    public class PlayerLevel {
        int level;
        int xp;
        String owner;

        PlayerLevel(int level, int xp, String dp_name) {
            this.level = level;
            this.xp = xp;
            this.owner = dp_name;
        }

        public void add_level(int level) {
            NginxPlayer.this.getLevel().set(level);
        }

        public void take_level(int level) {
            NginxPlayer.this.getLevel().set(level);
        }

        public int setXP(int xp) {
            return this.xp = xp;
        }

        public int getXP() {
            return this.xp;
        }

        public void give_xp(int xp, boolean send_message) {
            this.add_xp(xp);
            if (getBukkitPlayer() != null && send_message) {
                getBukkitPlayer().sendMessage("§7Level: §eคุณได้รับ XP เป็นจำนวน §6" + xp + "§e หน่วย");
            }
            this.level_up_task();
        }

        public void add_xp(int xp) {
            this.setXP(this.getXP() + xp);
        }

        public void take_xp(int xp) {
            this.setXP(NginxPlayer.this.getLevel().getXP() - xp);
            if (NginxPlayer.this.getLevel().getXP() < 0) {
                NginxPlayer.this.getLevel().setXP(0);
            }

        }

        public String getOwner() {
            return this.owner;
        }

        public void level_up_task() {
            final NginxPlayer dp = NginxPlayer.getNginxPlayer(Bukkit.getPlayerExact(this.getOwner()));
            int c_xp = dp.getLevel().getXP();
            if (c_xp >= getMaxXP()) {
                c_xp -= getMaxXP();
                int c_level = dp.getLevel().get_Int();
                this.add_level(c_level + 1);
                this.setXP(c_xp);
                (new BukkitRunnable() {
                    public void run() {
                        Player p = Bukkit.getPlayerExact(dp.getName());
                        int given_ooc = NginxPlayer.this.getLevel().get_Int() / 27;
                        int coins_given = (int)((double)NginxPlayer.this.getLevel().get_Int() * 1.6D);
                        if (p != null) {
                            p.sendMessage("§r");
                            p.sendMessage("§b§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬§8( §6§lSiamCraft §f» §e§lLevel §8)§b§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                            p.sendMessage("§r");
                            p.sendMessage("        §8[§b❆§8] §fยินดีด้วยคุณเลเวลอัพเเล้ว ! §7( §c" + (NginxPlayer.this.getLevel().get_Int() - 1) + "§6✭ §6➞ §a" + NginxPlayer.this.getLevel().get_Int() + "§6✭ §7) §8[§b❆§8]");
                            p.sendMessage("§r");
                            p.sendMessage("§a● §eคุณได้รับไอเทม : ");
                            if (given_ooc > 0) {
                                p.sendMessage("    §6➥ §dOOC     §7(§ax" + given_ooc + "§7)");
                            }
                            p.sendMessage("    §6➥ §6Coins   §7(§ax" + coins_given + "§7) §6⛁");
                            p.sendMessage("§r");
                            p.sendMessage("§b§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                            p.sendMessage("§r");
                        }

                        NginxPlayer.this.addOOC_Count(given_ooc , true);
                        NginxPlayer.this.addCoins(coins_given , true);
                        starchaser.BoardCastMsg("§7Level: §a" + dp.getName() + " ได้บรรลุเลเวล §f" + NginxPlayer.this.getLevel().get_Int() + " §aแล้ว");
                        this.cancel();
                    }
                }).runTaskTimer(core.getNginxMC, 100L, 100L);
            }

        }

        public int get_Int() {
            return this.level;
        }

        public int set(int level) {
            return this.level = level;
        }

        public String getXPBar() {
            int percent = this.getXPPercent();
            String bar;
            if (percent >= 97) {
                bar = "§4■§c■§6■§e■§a■§2■§3■§b■§d■§5■§f";
            } else if (percent >= 90) {
                bar = "§4■§c■§6■§e■§a■§2■§3■§b■§d■§7■§f";
            } else if (percent >= 80) {
                bar = "§4■§c■§6■§e■§a■§2■§3■§b■§7■■§f";
            } else if (percent >= 70) {
                bar = "§4■§c■§6■§e■§a■§2■§3■§7■■■§f";
            } else if (percent >= 60) {
                bar = "§4■§c■§6■§e■§a■§2■§7■■■■§f";
            } else if (percent >= 50) {
                bar = "§4■§c■§6■§e■§a■§7■■■■■§f";
            } else if (percent >= 40) {
                bar = "§4■§c■§6■§e■§7■■■■■■§f";
            } else if (percent >= 30) {
                bar = "§4■§c■§6■§7■■■■■■■§f";
            } else if (percent >= 20) {
                bar = "§4■§c■§7■■■■■■■■§f";
            } else if (percent >= 10) {
                bar = "§4■§7■■■■■■■■■§f";
            } else {
                bar = "§7■■■■■■■■■■§f";
            }

            return bar;
        }

        public int getXPPercent() {
            int percentx = (int)((float)this.getXP() * 100.0F / (float) getMaxXP());
            return percentx;
        }
        public int getMaxXP(){
            return level*273;
        }

        public String getStr() {
            int level = this.level;
            int star_style;
            if (level >= 500) {
                star_style = 0;
                level = level - 500;
            }else if (level >= 400) {
                star_style = 4;
                level = level - 400;
            }else if (level >= 300) {
                star_style = 3;
                level = level - 300;
            }else if (level >= 200) {
                star_style = 2;
                level = level - 200;
            }else if (level >= 100){
                star_style = 1;
                level = level - 100;
            }else {
                star_style = 0;
            }
                String color_level;
            if (level >= 80) {
                color_level = "d";
            } else if (level >= 70) {
                color_level = "c";
            } else if (level >= 60) {
                color_level = "a";
            } else if (level >= 50) {
                color_level = "2";
            } else if (level >= 40) {
                color_level = "6";
            } else if (level >= 30) {
                color_level = "3";
            } else if (level >= 20) {
                color_level = "b";
            } else if (level >= 10) {
                color_level = "f";
            } else {
                color_level = "7";
            }

            String newword;
            if (this.level >= starchaser.max_level) {
                newword = "§b§l§k:§d§lM§e§lA§a§lX§b§l§k:§r";
            } else {
                newword = "§" + color_level + level;
            }
            if (star_style == 4) {
                newword = "§c✮✮ " + newword + " §c✮✮§r";
            }
            if (star_style == 3) {
                newword = "§c✮✮ " + newword + " §c✮§r";
            }
            if (star_style == 2) {
                newword = "§c✮ " + newword + " §c✮§r";
            }
            if (star_style == 1) {
                newword = "§c✮ " + newword;
            }

            return newword;
        }
    }
    public String getRankAnimateCurrentFrame() {
        for (AnimateRank animateRank : AnimemateRankList) {
            if (animateRank.getClassID() == getPlayerClass().getId()) {
                return animateRank.getCurrentDisplay();
            }
        }
        return "";
    }
}