package me.starchaser.nginxmc.bukkit;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

public class starchaser {
    public static SERVERGAMEMODE servergamemode = SERVERGAMEMODE.Lobby;
    public static int max_level = 500;


    public static void giveItemLobby(Player p) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compass_meta = compass.getItemMeta();
        compass_meta.setDisplayName("§bรายชื่อเซิร์ฟเวอร์ §7(คลิกขวา)");
        compass_meta.setLore(Arrays.asList("§fคลิกขวาเพื่อเปิดรายชื่อเซิร์ฟเวอร์่ที่เปิดอยู่"));
        compass.setItemMeta(compass_meta);
        p.getInventory().setItem(0, compass);

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta sword_meta = compass.getItemMeta();
        sword_meta.setDisplayName("§7§k: §cดาบสังหาร §7§k:");
        sword_meta.setLore(Arrays.asList("§b§nเมื่อถือแล้วจะคุณจะเข้าสู่โหมดนักล่าและสังหารผู้อื่นที่ถือดาบด้วยกันได้!"));
        sword.setItemMeta(sword_meta);
        p.getInventory().setItem(5, sword);
    }
    public enum SERVERGAMEMODE{
        Lobby,Minigames,MINIGAMES_HOOK,Disabled
    }
    public static enum LOG_TYPE {
        PLAYER,
        CHAT,
        SQL,
        NONE,
        DEFAULT,
        COMMAND,
        GAME,
        BC,
        DEBUG,
        WORLD,
        PLUGIN;
    }
    public static void BoardCastMsg(String str) {
        Logger(starchaser.LOG_TYPE.BC, str);
        Iterator var1 = Bukkit.getOnlinePlayers().iterator();

        while(var1.hasNext()) {
            Player p = (Player)var1.next();
            p.sendMessage(str);
        }

    }
    public static void Logger(final starchaser.LOG_TYPE lt, final String message) {
        (new BukkitRunnable() {
            public void run() {
                if (!core.debug && lt == starchaser.LOG_TYPE.DEBUG) {
                } else {
                    String prefix = "§7Nginx: §a";
                    if (lt.equals(starchaser.LOG_TYPE.PLAYER)) {
                        prefix = "§f[§bPlayerManager§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.PLUGIN)) {
                        prefix = "§f[§aPluginManager§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.DEBUG)) {
                        prefix = "§f[§6DEBUG§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.SQL)) {
                        prefix = "§f[§5SQLManager§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.CHAT)) {
                        prefix = "§f[§bChatManager§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.COMMAND)) {
                        prefix = "§f[§eCommandManager§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.GAME)) {
                        prefix = "§f[§aGameManager§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.NONE)) {
                        prefix = "§r";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.BC)) {
                        prefix = "§f[§cServerMessage§f] §f";
                    }

                    if (lt.equals(starchaser.LOG_TYPE.WORLD)) {
                        prefix = "§f[§dWorldManager§f] §f";
                    }

                    Bukkit.getConsoleSender().sendMessage(prefix + message);
                }
            }
        }).runTask(core.getNginxMC);
    }
    public static int getClassID(Player p) {
        int id = 0;
        for (PermissionAttachmentInfo rawperm : p.getEffectivePermissions()) {
            String perm = rawperm.getPermission();
            if (perm.startsWith("nginx.class.grant")) {
                try {
                    if (Integer.parseInt(perm.substring(18)) > id) {
                        id = Integer.parseInt(perm.substring(18));
                        starchaser.Logger(LOG_TYPE.CHAT, "ID: " + id);
                    }
                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
        }
        return id;
    }
    public static boolean CreateAccount(Player player) {
        try {
            core.getSqlConnection().createStatement().executeUpdate("INSERT INTO `nginxmc`.`players` (`id`, `username`, `ooc`, `level`, `xp`, `title`, `coins`, `feather`, `wp`) VALUES (NULL, '" + player.getName() + "', '0', '1', '0', '0', '0', '0', '0');");
            Logger(starchaser.LOG_TYPE.PLAYER, "§aAccount Created! [" + player.getName() + "]");
            player.sendMessage("§7Account: §aสร้างบัญชีเรียบร้อยแล้ว");
            return true;
        } catch (Exception var2) {
            var2.printStackTrace();
            Logger(starchaser.LOG_TYPE.PLAYER, "§cError on create account... (TASK: starchaser.createaccount) [" + player.getName() + "]");
            return false;
        }
    }
    public static boolean getPlayerData(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    int class_id = getClassID(player);
                    ResultSet resultSet = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + player.getName() + "'");
                    resultSet.next();
                    NginxPlayer dp = new NginxPlayer(resultSet.getInt("id"), resultSet.getString("username"), class_id, resultSet.getInt("level"), resultSet.getInt("xp"), resultSet.getInt("title"), resultSet.getInt("coins"), true, resultSet.getInt("feather"));
                    NginxPlayer.addNginxPlayer(dp);
                    Logger(starchaser.LOG_TYPE.DEBUG, "ID:" + dp.getId());
                    Logger(starchaser.LOG_TYPE.DEBUG, "String: " + dp.getName());
                    Logger(starchaser.LOG_TYPE.DEBUG, "Level:" + dp.getLevel().get_Int());
                    Logger(starchaser.LOG_TYPE.DEBUG, "XP: " + dp.getLevel().getXP());
                    Logger(starchaser.LOG_TYPE.PLAYER, "§aPlayer data get!... [" + player.getName() + "]");
                    player.sendMessage("§7Account: §eเรียบร้อยแล้ว!");
                    dp.getPlayerClass().updateNTE(false);
                } catch (Exception var6) {
                    var6.printStackTrace();
                    Logger(starchaser.LOG_TYPE.PLAYER, "§cError on get player data... [" + player.getName() + "]");
                }
            }
        }.runTaskAsynchronously(core.getNginxMC);
        return true;
    }

//    public static NginxPlayer MakePlayerData(String name) {
//        try {
//            int point = 0;
//            Player p = Bukkit.getPlayerExact(name);
//            int class_id = 0;
//            if (p != null) {
//                class_id = getClassID(p);
//            }
//            ResultSet resultSet = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + name + "'");
//            resultSet.next();
//            NginxPlayer dp = new NginxPlayer(resultSet.getInt("id"), resultSet.getString("username"), class_id, resultSet.getInt("level"), resultSet.getInt("xp"), resultSet.getInt("title"), resultSet.getInt("coins"), false, point, resultSet.getInt("feather"));
//            return dp;
//        } catch (Exception var5) {
//            var5.printStackTrace();
//            Logger(starchaser.LOG_TYPE.PLAYER, "§cError on get custom player data... [" + name + "]");
//            return null;
//        }
//    }

    public static boolean sendPlayerData(Player player) {
        NginxPlayer dp = NginxPlayer.getNginxPlayer(player);
        return sendPlayerData(dp);
    }

    public static boolean sendPlayerData(NginxPlayer dp) {
        if (dp == null) return false;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    core.getSqlConnection().createStatement().executeUpdate("UPDATE `nginxmc`.`players` SET `level` = '" + dp.getLevel().get_Int() + "', `xp` = '" + dp.getLevel().getXP() + "', `title` = '" + dp.getTitle().getId() + "', `coins` = '" + dp.getCoins() + "' WHERE `players`.`id` = " + dp.getId() + ";");
                    Logger(starchaser.LOG_TYPE.PLAYER, "§aPlayer data sent! [" + dp.getName() + "]");
                } catch (Exception var2) {
                    var2.printStackTrace();
                    Logger(starchaser.LOG_TYPE.PLAYER, "§cError on send player data... (TASK: starchaser.getplayerdata) [" + dp.getName() + "]");
                }
            }
        }.runTaskAsynchronously(core.getNginxMC);
    return true;
    }
    public static void updateNewNameTag(Player p , String value){
       if (p == null) {
            throw new IllegalArgumentException("Nginx error! (Player cannot be null)");
        }
        if (NametagEdit.getApi() == null) {
            throw new IllegalArgumentException("Nginx error! (NTE-API cannot be null)");
        }
        starchaser.Logger(LOG_TYPE.DEBUG, "New nametag update request! " + p.getName() + " value [" + value + "]");
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender() , "nte player " + p.getName() + " prefix " + value);
                NametagEdit.getApi().setPrefix(p, value);
                starchaser.Logger(LOG_TYPE.DEBUG, "New nametag for player " + p.getName() + " update! [" + value + "]");
            }
        }.runTaskLater(core.getNginxMC,10L);
    }
    public static class popupchat {
        final Player player;
        final String chat;
        boolean force_remove;

        popupchat(final Player player, final String chat) {
            this.player = player;
            this.chat = chat;
            if (servergamemode == SERVERGAMEMODE.Minigames && player.getLocation().getWorld() != core.main_world) return;
            final Hologram hologram = HologramsAPI.createHologram(core.getNginxMC, player.getLocation().add(0.0D, 2.0D, 0.0D));
            hologram.appendTextLine("§b" + chat);
            (new BukkitRunnable() {
                int ticksRun;
                int out_tricks = chat.length() * 5;
                int is_out_ing = 0;
                Location loc = player.getLocation();
                public void run() {
                    try {
                        ++this.ticksRun;
                        if (player != null) {
                            loc  = player.getLocation();
                        }
                        if (this.out_tricks < 100) {
                            this.out_tricks = 100;
                        }
                        if (player == null || !player.isOnline() || popupchat.this.force_remove) {
                            hologram.teleport(loc.clone().add(0.0D, 4.1D, 0.0D));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    hologram.delete();
                                    this.cancel();
                                }
                            }.runTaskLater(core.getNginxMC , 3L);
                        this.cancel();
                        return;
                        }
                        if (this.ticksRun > this.out_tricks) {
                            hologram.teleport(loc.clone().add(0.0D, 4.1D, 0.0D));
                        } else {
                            hologram.teleport(loc.clone().add(0.0D, 2.9D, 0.0D));
                        }

                        if (this.ticksRun > this.out_tricks + 3) {
                            hologram.delete();
                            this.cancel();
                        }

                    } catch (IllegalArgumentException var2) {
                        this.cancel();
                    }
                }
            }).runTaskTimerAsynchronously(core.getNginxMC, 1L, 1L);
        }

        public Player getOwner() {
            return this.player;
        }

        public void setForce_remove(boolean force_remove) {
            this.force_remove = force_remove;
        }
    }
    public static String getSaltStringSet(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefthijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
