package me.starchaser.nginxmc.bukkit;

import me.starchaser.nginxmc.MySQL;
import me.starchaser.nginxmc.YamlReader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
public class core extends JavaPlugin {
    public static Plugin getNginxMC;
    public static String path;
    public static boolean debug = false;
    public static ArrayList<NginxPlayer> PlayerRef;
    public static ArrayList<AnimateRank> AnimemateRankList;
    public static Connection SQL_CONNECTION;
    public static Connection SQL_CONNECTION_SC;
    public static MySQL sql = new MySQL("sql.siamcraft.net", "3306", "nginxmc", "siamcraft_plugin", "v6gAKopaMeK73ET78uCis7G2cib3wo");
    public static MySQL sql_SC = new MySQL("sql.siamcraft.net", "3306", "siamcraft", "siamcraft_plugin", "v6gAKopaMeK73ET78uCis7G2cib3wo");
    public static String world_scam = "#NONE#";
    public static World main_world = null;
    public static Location spawn_point = null;
    public static boolean server_chat_pop = false;
    public static boolean manage_chat = false;
    public static boolean void_spawn = true;
    public static boolean clear_on_join = true;
    public static int keepalivetimerandom = 60;
    @Override
    public void onEnable() {
        path = this.getDataFolder().getAbsoluteFile().getParentFile().getParentFile().getAbsolutePath() + File.separator;
        getNginxMC = this;
        File f = new File(path+".mc-deluxe/nginx.yml");
        PlayerRef = new ArrayList<>();
        AnimemateRankList = new ArrayList<>();
        try {
            SQL_CONNECTION = sql.openConnection();
            SQL_CONNECTION_SC = sql_SC.openConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
            getServer().getConsoleSender().sendMessage("§f[§bSQLManager§f] §cERROR! on SQL connection please check error!");
            getServer().getConsoleSender().sendMessage("§f[§bServerManager§f] §cProxy is stoping due to critical error!");
            Bukkit.getServer().shutdown();
        }
        if (!f.exists()) {
            Bukkit.getConsoleSender().sendMessage("§7NginxMC: §cError on get configuration file please create one default first!");
            Bukkit.getPluginManager().disablePlugin(this);
        }else {
            YamlReader config = new YamlReader(path+".mc-deluxe/nginx.yml");
            manage_chat = config.getBoolean("override_chat");
            server_chat_pop = !config.getBoolean("disable_chat_pop");
            void_spawn = config.getBoolean("main_world.void_spawn");
            clear_on_join = !config.getBoolean("disable_clear_inventory");
            try{
                starchaser.servergamemode = starchaser.SERVERGAMEMODE.valueOf(config.getString("server_gamemode"));
            }catch (Exception | Error ex) {
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("§7NginxMC: §cCloud not load default server gamemode, please check config and try again!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            try{
                starchaser.newMethodloadPlayer = config.getBoolean("newMeth_loadPlayerData.enabled");
                if(starchaser.newMethodloadPlayer){
                    starchaser.newMethodloadPlayer_period = config.getInt("newMeth_loadPlayerData.tick_period");
                }
            }catch (Exception | Error ex) {
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("§7NginxMC: §cCloud not load loadPlayerData method, please check config and try again!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            try{
                main_world = Bukkit.getWorld(config.getString("main_world.world_name"));
                if (main_world == null) {
                    Bukkit.getConsoleSender().sendMessage("§7NginxMC: §cCloud not load main world, please check name or folder name and try again!");
                    Bukkit.getPluginManager().disablePlugin(this);
                    return;
                }else {
                    spawn_point = new Location(main_world , config.getInt("main_world.spawn_point.x") , config.getInt("main_world.spawn_point.y") , config.getInt("main_world.spawn_point.z") , config.getInt("main_world.spawn_point.yaw"), config.getInt("main_world.spawn_point.pitch"));
                }
                main_world.getEntities();
            }catch (Exception | Error ex) {
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage("§7NginxMC: §cCloud not load main world please check name or folder name and try again!");
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            AnimemateRankList.add(new AnimateRank(9,2));
            AnimemateRankList.add(new AnimateRank(7,3));
            AnimemateRankList.add(new AnimateRank(6,4));
            AnimemateRankList.add(new AnimateRank(5,2));
            AnimemateRankList.add(new AnimateRank(4,2));
            AnimemateRankList.add(new AnimateRank(3,2));
            AnimemateRankList.add(new AnimateRank(2,3));
            AnimemateRankList.add(new AnimateRank(1,3));
            AnimemateRankList.add(new AnimateRank(0,2));
            try{
                if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null && Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")) {
                    getLogger().info("Starting hook MVdWPlaceholderAPI!");
                    mvdwpapi.registerMCdWAPI();
                    getLogger().info("MVdWPlaceholderAPI hooked!");
                }
            }catch (Exception ex){}
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (AnimateRank animateRank : AnimemateRankList) {
                        animateRank.renderFrame();
                    }
                }
            }.runTaskTimerAsynchronously(core.getNginxMC , 1L,1L);

            if (starchaser.servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
                new BukkitRunnable() {
                    int timer = 0;
                    int auto_save_task = 60;
                    @Override
                    public void run() {
                        if (Bukkit.getOnlinePlayers().size() <= 3) {
                            this.timer = 0;
                            core.world_scam = "#NONE#";
                        } else {
                            if (core.world_scam == "#NONE#" && this.timer == 30) {
                                core.world_scam = String.valueOf(starchaser.getSaltStringSet(new Random().nextInt(24 - 1 + 1) + 1));
                                starchaser.BoardCastMsg("§7Reaction: §aใครพิมพ์ §7" + core.world_scam + "§a ก่อนคนนั้นชนะ!");
                            }

                            if (core.world_scam != "#NONE#" && this.timer > 80) {
                                this.timer = 0;
                                core.world_scam = "#NONE#";
                                starchaser.BoardCastMsg("§7Reaction: §eไม่มีใครพิมพ์ได้ทันเวลาที่กำหนด!");
                            }

                            if (core.world_scam == "#WIN#") {
                                this.timer = 0;
                                core.world_scam = "#NONE#";
                            }
                            ++this.timer;
                        }
                        if (auto_save_task < 1) {
                            auto_save_task = 600;
                            if (Bukkit.getOnlinePlayers().size() > 0) {
                                starchaser.Logger(starchaser.LOG_TYPE.PLAYER , "Starting auto save tasks...");
                                try{
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        starchaser.sendPlayerData(p);
                                    }
                                }catch (NullPointerException ex) {
                                    ex.printStackTrace();
                                    starchaser.Logger(starchaser.LOG_TYPE.PLAYER , "ERROR ON SAVING PLAYER DATA | PLEASE SCREENSHOT ERROR AND CONTACT '_SC'");
                                }
                                starchaser.Logger(starchaser.LOG_TYPE.PLAYER , "Complete!");
                            }
                        }else {
                            auto_save_task--;
                        }
                        NginxPlayer np_remove = null;
                        for (NginxPlayer nginxPlayer : PlayerRef) {
                            if (nginxPlayer.getPlayer() == null) np_remove = nginxPlayer;
                        }
                        if (np_remove != null) NginxPlayer.removeNginxPlayer(np_remove);

//                        try {
//                            for (Player pz : Bukkit.getOnlinePlayers()) {
//                                NginxPlayer pznx = NginxPlayer.getNginxPlayer(pz);
//                                new BukkitRunnable() {
//                                    @Override
//                                    public void run() {
//                                        for(Player target : Bukkit.getOnlinePlayers()) {
//                                            NginxPlayer pzTarget = NginxPlayer.getNginxPlayer(target);
//                                            if (pznx == null || pzTarget == null) {
//                                                pz.hidePlayer(target);
//                                                target.hidePlayer(pz);
//                                            }else {
//                                                if (pznx.getLobby_Number() == pzTarget.getLobby_Number()) {
//                                                    pz.showPlayer(target);
//                                                    target.showPlayer(pz);
//                                                }else {
//                                                    pz.hidePlayer(target);
//                                                    target.hidePlayer(pz);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }.runTask(core.getNginxMC);
//                            }
//                        }catch (Exception exc) {
//                            if (core.debug) {
//                                exc.printStackTrace();
//                            }
//                        } //TODO: MULTI LOBBY SYSTEM
                    }
                }.runTaskTimerAsynchronously(getNginxMC, 20L, 20L);
            }
        }

        try {
            if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new papi().register();
            }
        }catch (Exception ex) {}
        for (Player pp : Bukkit.getOnlinePlayers()) {
            events.FastJoinTask(pp);
            if(starchaser.newMethodloadPlayer){
                pp.sendMessage("§7Account: §eกำลังโหลดข้อมูล...");
                ponlawat.join_playerdata.add(pp.getName());
            }
        }

        (new BukkitRunnable(){
            @Override
            public void run() {
                try{
                    runSelPinging();
                } catch (SQLException ex){
                    if(ex.getErrorCode() == 0){
                        try{
                            runSelPinging();
                        } catch (SQLException e){}
                    } else {
                        ex.printStackTrace();
                        getNginxMC.getServer().getConsoleSender().sendMessage("§f[§bSQLManager§f] §cERROR! cannot Pinging to sql server!");
                    }
                }
            }
        }).runTaskTimerAsynchronously(core.getNginxMC, core.keepalivetimerandom*3L, core.keepalivetimerandom*3L);

        Bukkit.getPluginManager().registerEvents(new events(), this);
        if (starchaser.servergamemode == starchaser.SERVERGAMEMODE.Lobby) Bukkit.getPluginManager().registerEvents(new lobbyevents(), this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getCommand("server").setExecutor(new serverCommand());
        if(starchaser.newMethodloadPlayer) events.loadJoinSQL();
    }
    public static void runSelPinging() throws SQLException{
        ResultSet res = core.getSqlConnection().createStatement().executeQuery("/* ping */ SELECT 1;"); res.next();
        ResultSet resn = core.getSqlConnection().createStatement().executeQuery("/* ping */ SELECT 1;"); resn.next();
    }
    public static Connection getSqlConnection(){
        try {
            SQL_CONNECTION = sql.openConnection();
            return SQL_CONNECTION;
        } catch (Exception ex) {
            ex.printStackTrace();
            getNginxMC.getServer().getConsoleSender().sendMessage("§f[§bSQLManager§f] §cERROR! on get SQL Connection for use!");
        }
        return null;
    }
    public static Connection getSqlConnectionAuthme(){
        try {
            SQL_CONNECTION_SC = sql_SC.openConnection();
            return SQL_CONNECTION_SC;
        } catch (Exception ex) {
            ex.printStackTrace();
            getNginxMC.getServer().getConsoleSender().sendMessage("§f[§bSQLManager§f] §cERROR! on get SQL SC Connection for use!");
        }
        return null;
    }
    @Override
    public void onDisable() {
        for (Player pp : Bukkit.getOnlinePlayers()) {
            starchaser.sendPlayerData(pp);
        }
        for (Player pp : Bukkit.getOnlinePlayers()) {
            NginxPlayer.removeNginxPlayer(pp);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName() == "coin" || command.getName() == "coins" || command.getName() == "money") {
            sender.sendMessage("§7Coins: §eเหรียญคงเหลือ §b" + NginxPlayer.getNginxPlayer((Player) sender).getCoins() + " §eเหรียญ");
            return true;
        }

        NginxPlayer nginxPlayer = null;
        if (sender instanceof Player) {
            nginxPlayer = NginxPlayer.getNginxPlayer((Player) sender);
        }
        if (command.getName().equalsIgnoreCase("nginx") || command.getName().equalsIgnoreCase("nginxmc")) {
            if (args.length < 1 || sender instanceof Player && nginxPlayer == null && nginxPlayer.getPlayerClass().getId() < 6){
                sender.sendMessage("§7Nginx: §bNginxMC "+getNginxMC.getDescription().getVersion()+" by _StarChaser");
                sender.sendMessage("§7Nginx: §aThis server is runing under: §c" + starchaser.servergamemode.toString() + "§a mode");
                return true;
            }else {
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("debug")) {
                        debug = !debug;
                        sender.sendMessage("§7Nginx: Console debug has been set to: §c" + Boolean.toString(debug));
                        starchaser.Logger(starchaser.LOG_TYPE.PLUGIN , sender.getName()  + " has set debug value to " + Boolean.toString(debug));
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("lobby")) {
                        if (starchaser.servergamemode != starchaser.SERVERGAMEMODE.Lobby) {
                            sender.sendMessage("§7Nginx: §cThis command is disable with server gamemode is " + starchaser.servergamemode.toString());
                        }else {
                            if (args.length < 2) {
                                sender.sendMessage("§7Usage: §c/nginx lobby join <lobby_id>");
                            }else {
                                nginxPlayer.setLobby_Number(Integer.parseInt(args[2]));
                            }
                        }
                    }
                    if (args[0].equalsIgnoreCase("coins")) {
                        if (args.length < 4) {
                            sender.sendMessage("§7Usage: §c/nginx coins give <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx coins take <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx coins set <player> <count>");
                            return true;
                        }
                        Player player;
                        NginxPlayer NginxPlayer = null;
                        if (args[1].equalsIgnoreCase("give")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }
                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.setCoins(NginxPlayer.getCoins() + Integer.parseInt(args[3]));
                            sender.sendMessage("§7Coins: §aComplete give to " + args[2] + " (" + args[3] + ")");
                        }

                        if (args[1].equalsIgnoreCase("take")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }

                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.setCoins(NginxPlayer.getCoins() - Integer.parseInt(args[3]));
                            sender.sendMessage("§7Coins: §aComplete take coins to " + args[2] + " (" + args[3] + ")");
                        }

                        if (args[1].equalsIgnoreCase("set")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }

                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.setCoins(Integer.parseInt(args[3]));
                            sender.sendMessage("§7Coins: §aComplete set coins to " + args[2] + " (" + args[3] + ")");
                        }

                        return true;
                    }


                    if (args[0].equalsIgnoreCase("level")) {
                        if (args.length < 4) {
                            sender.sendMessage("§7Usage: §c/nginx level give <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx level take <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx level set <player> <count>");
                            return true;
                        }

                        Player player;
                        NginxPlayer NginxPlayer = null;
                        if (args[1].equalsIgnoreCase("give")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }

                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.getLevel().set(NginxPlayer.getLevel().get_Int() + Integer.parseInt(args[3]));
                            sender.sendMessage("§7Level: §aComplete give level to " + args[2] + " (" + args[3] + ")");
                        }

                        if (args[1].equalsIgnoreCase("take")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }

                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.getLevel().set(NginxPlayer.getLevel().get_Int() - Integer.parseInt(args[3])) ;
                            sender.sendMessage("§7Level: §aComplete take level to " + args[2] + " (" + args[3] + ")");
                        }

                        if (args[1].equalsIgnoreCase("set")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }
                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.getLevel().set(Integer.parseInt(args[3])) ;
                            sender.sendMessage("§7Level: §aComplete set level to " + args[2] + " (" + args[3] + ")");
                        }

                        return true;
                    }


                    if (args[0].equalsIgnoreCase("xp")) {
                        if (args.length < 4) {
                            sender.sendMessage("§7Usage: §c/nginx xp give <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx xp take <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx xp set <player> <count>");
                            return true;
                        }

                        Player player;
                        NginxPlayer NginxPlayer = null;
                        if (args[1].equalsIgnoreCase("give")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }

                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.getLevel().give_xp(Integer.parseInt(args[3]) , true);
                            sender.sendMessage("§7XP: §aComplete give xp to " + args[2] + " (" + args[3] + ")");
                        }

                        if (args[1].equalsIgnoreCase("take")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }

                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.getLevel().setXP(NginxPlayer.getLevel().getXP() - Integer.parseInt(args[3])) ;
                            sender.sendMessage("§7XP: §aComplete take xp to " + args[2] + " (" + args[3] + ")");
                        }

                        if (args[1].equalsIgnoreCase("set")) {
                            player = Bukkit.getPlayerExact(args[2]);
                            if (player == null) {
                                sender.sendMessage("§7ERR: §cPlayer is not online " + args[2]);
                                return true;
                            }
                            NginxPlayer = NginxPlayer.getNginxPlayer(player);
                            NginxPlayer.getLevel().setXP(Integer.parseInt(args[3])) ;
                            sender.sendMessage("§7XP: §aComplete set xp to " + args[2] + " (" + args[3] + ")");
                        }

                        return true;
                    }



                    int new_wp;
                    if (args[0].equalsIgnoreCase("wp")) {
                        if (args.length < 4) {
                            sender.sendMessage("§7Usage: §c/nginx wp add <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx wp take <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx wp set <player> <count>");
                            return true;
                        }

                        ResultSet resultSet;
                        int wp_count;
                        int id;
                        if (args[1].equalsIgnoreCase("add")) {
                            try {
                                resultSet = getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + args[2] + "'");
                                if (!resultSet.isBeforeFirst()) {
                                    sender.sendMessage("§7ERR: §cNot found this player " + args[2]);
                                    return true;
                                }

                                resultSet.next();
                                wp_count = resultSet.getInt("wp");
                                id = resultSet.getInt("id");
                                new_wp = Integer.parseInt(args[3]) + wp_count;
                                getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `wp` = '" + new_wp + "' WHERE `players`.`id` = " + id + ";");
                                sender.sendMessage("§7WP: §aComplete add wp to " + args[2] + " old_value: " + wp_count + " new_value: " + new_wp);
                            } catch (SQLException var13) {
                                var13.printStackTrace();
                                sender.sendMessage("§7ERR: §cCan't executeQuery");
                                return true;
                            }
                        }

                        if (args[1].equalsIgnoreCase("take")) {
                            try {
                                resultSet = getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + args[2] + "'");
                                if (!resultSet.isBeforeFirst()) {
                                    sender.sendMessage("§7ERR: §cNot found this player " + args[2]);
                                    return true;
                                }

                                resultSet.next();
                                wp_count = resultSet.getInt("wp");
                                id = resultSet.getInt("id");
                                new_wp = wp_count - Integer.parseInt(args[3]);
                                getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `wp` = '" + new_wp + "' WHERE `players`.`id` = " + id + ";");
                                sender.sendMessage("§7WP: §aComplete take wp to " + args[2] + " old_value: " + wp_count + " new_value: " + new_wp);
                            } catch (SQLException var12) {
                                var12.printStackTrace();
                                sender.sendMessage("§7ERR: §cCan't executeQuery");
                                return true;
                            }
                        }

                        if (args[1].equalsIgnoreCase("set")) {
                            try {
                                resultSet = getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + args[2] + "'");
                                if (!resultSet.isBeforeFirst()) {
                                    sender.sendMessage("§7ERR: §cNot found this player " + args[2]);
                                    return true;
                                }

                                resultSet.next();
                                wp_count = resultSet.getInt("wp");
                                id = resultSet.getInt("id");
                                new_wp = Integer.parseInt(args[3]);
                                getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `wp` = '" + new_wp + "' WHERE `players`.`id` = " + id + ";");
                                sender.sendMessage("§7WP: §aComplete set wp to " + args[2] + " old_value: " + wp_count + " new_value: " + new_wp);
                            } catch (SQLException var11) {
                                var11.printStackTrace();
                                sender.sendMessage("§7ERR: §cCan't executeQuery");
                                return true;
                            }
                        }

                        return true;
                    }
                    int new_ooc = 0;
                    if (args[0].equalsIgnoreCase("ooc")) {
                        if (args.length < 4) {
                            sender.sendMessage("§7Usage: §c/nginx ooc add <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx ooc take <player> <count>");
                            sender.sendMessage("§7Usage: §c/nginx ooc set <player> <count>");
                            return true;
                        }

                        ResultSet resultSet;
                        int ooc_count;
                        int id;
                        if (args[1].equalsIgnoreCase("add")) {
                            try {
                                resultSet = getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + args[2] + "'");
                                if (!resultSet.isBeforeFirst()) {
                                    sender.sendMessage("§7ERR: §cNot found this player " + args[2]);
                                    return true;
                                }

                                resultSet.next();
                                ooc_count = resultSet.getInt("ooc");
                                id = resultSet.getInt("id");
                                new_ooc = Integer.parseInt(args[3]) + ooc_count;
                                getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `ooc` = '" + new_ooc + "' WHERE `players`.`id` = " + id + ";");
                                sender.sendMessage("§7ooc: §aComplete add ooc to " + args[2] + " old_value: " + ooc_count + " new_value: " + new_ooc);
                            } catch (SQLException var13) {
                                var13.printStackTrace();
                                sender.sendMessage("§7ERR: §cCan't executeQuery");
                                return true;
                            }
                        }

                        if (args[1].equalsIgnoreCase("take")) {
                            try {
                                resultSet = getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + args[2] + "'");
                                if (!resultSet.isBeforeFirst()) {
                                    sender.sendMessage("§7ERR: §cNot found this player " + args[2]);
                                    return true;
                                }

                                resultSet.next();
                                ooc_count = resultSet.getInt("ooc");
                                id = resultSet.getInt("id");
                                new_ooc = ooc_count - Integer.parseInt(args[3]);
                                getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `ooc` = '" + new_ooc + "' WHERE `players`.`id` = " + id + ";");
                                sender.sendMessage("§7ooc: §aComplete take ooc to " + args[2] + " old_value: " + ooc_count + " new_value: " + new_ooc);
                            } catch (SQLException var12) {
                                var12.printStackTrace();
                                sender.sendMessage("§7ERR: §cCan't executeQuery");
                                return true;
                            }
                        }

                        if (args[1].equalsIgnoreCase("set")) {
                            try {
                                resultSet = getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + args[2] + "'");
                                if (!resultSet.isBeforeFirst()) {
                                    sender.sendMessage("§7ERR: §cNot found this player " + args[2]);
                                    return true;
                                }

                                resultSet.next();
                                ooc_count = resultSet.getInt("ooc");
                                id = resultSet.getInt("id");
                                new_ooc = Integer.parseInt(args[3]);
                                getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `ooc` = '" + new_ooc + "' WHERE `players`.`id` = " + id + ";");
                                sender.sendMessage("§7ooc: §aComplete set ooc to " + args[2] + " old_value: " + ooc_count + " new_value: " + new_ooc);
                            } catch (SQLException var11) {
                                var11.printStackTrace();
                                sender.sendMessage("§7ERR: §cCan't executeQuery");
                                return true;
                            }
                        }

                        return true;
                    }



                }
            }
        }
        return true;
    }
}
