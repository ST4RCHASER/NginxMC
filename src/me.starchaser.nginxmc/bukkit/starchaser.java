package me.starchaser.nginxmc.bukkit;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.neznamy.tab.platforms.bukkit.TabPlayer;
import me.neznamy.tab.platforms.bukkit.unlimitedtags.NameTagLineManager;
import me.neznamy.tab.shared.ITabPlayer;
import me.neznamy.tab.shared.NameTag16;
import me.starchaser.nginxmc.bukkit.NginxPlayer.PlayerClass;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import static me.starchaser.nginxmc.bukkit.events.chat_history;

public class starchaser {
    public static SERVERGAMEMODE servergamemode = SERVERGAMEMODE.Lobby;
    public static int max_level = 500;
    public static int gamemode_virtual_lobby_size = 18;
    public static int virtual_lobby_player_size = 1000;
    public static boolean cmiHologramAPI = false;


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

        ItemStack hopper = new ItemStack(Material.HOPPER);
        ItemMeta hopper_meta = hopper.getItemMeta();
        hopper_meta.setDisplayName("§eรายชื่อล๊อบบี้ §7(คลิกขวา)");
        hopper_meta.setLore(Arrays.asList("§fคลิกขวาเพื่อเปิดรายชื่อเล๊อบบี้เปิดอยู่"));
        hopper.setItemMeta(hopper_meta);
//        p.getInventory().setItem(8, hopper);


    }

    public enum SERVERGAMEMODE {
        Lobby, Minigames, MINIGAMES_HOOK, Disabled
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

        while (var1.hasNext()) {
            Player p = (Player) var1.next();
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
                    dp.getPlayerClass().updateRankLine(false);
                } catch (Exception var6) {
                    var6.printStackTrace();
                    Logger(starchaser.LOG_TYPE.PLAYER, "§cError on get player data... [" + player.getName() + "]");
                }
            }
        }.runTaskAsynchronously(core.getNginxMC);
        return true;
    }

    public static void AddPlayerChatPOP(Player p, String message) throws Exception {
        Boolean allow = true;
        NginxPlayer np = NginxPlayer.getNginxPlayer(p);
        for (PotionEffect pot : p.getActivePotionEffects()) {
            if (pot.getType().equals(PotionEffectType.INVISIBILITY)) allow = false;
        }
        if (p.getWorld().equals(core.main_world)) {
            if (core.server_chat_pop && p.getGameMode() != GameMode.SPECTATOR && allow) {
                Boolean is_chat_pop_enable = true;
                try {
                    is_chat_pop_enable = np.isChatPOPEnabled();
                } catch (Exception exc) {
                    if (core.debug) {
                        exc.printStackTrace();
                    }
                }
                if (is_chat_pop_enable) {
                    for (starchaser.popupchat popupchat : chat_history) {
                        if (popupchat.getOwner() == p) {
                            popupchat.setForce_remove(true);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    chat_history.remove(popupchat);
                                }
                            }.runTaskLaterAsynchronously(core.getNginxMC, 10L);
                        }
                    }
                    chat_history.add(new starchaser.popupchat(p, message));
                }
            }
        }
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

    public static class popupchat {
        final Player player;
        final String chat;
        boolean force_remove;
        Location last_loc;

        popupchat(final Player player, final String chat) throws Exception {
            this.player = player;
            last_loc = player.getLocation().add(0.0D, 2.0D, 0.0D);
            this.chat = chat;
            if (servergamemode == SERVERGAMEMODE.Minigames && player.getLocation().getWorld() != core.main_world)
                return;
            if (starchaser.cmiHologramAPI) {
                /*
                final CMIHologram hologram = new CMIHologram("cp_" + player.getName(), player.getLocation().add(0.0D, 2.0D, 0.0D));
                player.sendMessage("1");
                hologram.setLines(Arrays.asList("§b" + chat));
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (hologram != null && player != null) {
                        if (target != player && target != null)
                            if (starchaser.servergamemode == SERVERGAMEMODE.Lobby) {
                                if (!(NginxPlayer.getNginxPlayer(player) != null && NginxPlayer.getNginxPlayer(target) != null && NginxPlayer.getNginxPlayer(player).getLobby_Number() == NginxPlayer.getNginxPlayer(target).getLobby_Number())) {
                                    hologram.hide(player.getUniqueId());
                                }
                            }
                    } else {
                        hologram.hide(player.getUniqueId());
                    }
                }
                hologram.setSaveToFile(false);
                hologram.setUpdateRange(0);
                CMI.getInstance().getHologramManager().addHologram(hologram);
                (new BukkitRunnable() {
                    int ticksRun;
                    int out_tricks = chat.length() * 5;
                    Location loc = player.getLocation();

                    public void run() {
                        try {
                            ++ticksRun;
                            if (player != null) {
                                loc = player.getLocation();
                            }
                            if (this.out_tricks < 100) {
                                this.out_tricks = 100;
                            }
                            if (player == null || !player.isOnline() || popupchat.this.force_remove) {
                                CMI.getInstance().getHologramManager().getByName(hologram.getName()).setLoc(loc.clone().add(0.0D, 4.3D, 0.0D));
                                player.sendMessage("2");
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        hologram.removeFromCache(player.getUniqueId());
                                        this.cancel();
                                    }
                                }.runTaskLater(core.getNginxMC, 3L);
                                this.cancel();
                                return;
                            }
                            if (this.ticksRun > this.out_tricks) {
                                CMI.getInstance().getHologramManager().getByName(hologram.getName()).setLoc(loc.clone().add(0.0D, 4.3D, 0.0D));
                                player.sendMessage("3");
                            } else {
                                if (!(player.getLocation().getBlockX() == last_loc.getBlockX() && player.getLocation().getBlockY() == last_loc.getBlockY() && player.getLocation().getBlockZ() == last_loc.getBlockZ())) {
                                    CMI.getInstance().getHologramManager().getByName(hologram.getName()).setLoc(loc.clone().add(0.0D, 3.15D, 0.0D));
                                    player.sendMessage("4");
                                } else {
                                    last_loc = player.getLocation();
                                }
                            }

                            if (this.ticksRun > this.out_tricks + 3) {
                                CMI.getInstance().getHologramManager().getByName(hologram.getName()).hide();
                                this.cancel();
                            }
                        } catch (IllegalArgumentException var2) {
                            if (hologram != null && hologram.isEnabled() == true) {
                                CMI.getInstance().getHologramManager().getByName(hologram.getName()).hide();
                            }
                            this.cancel();
                        }
                        CMI.getInstance().getHologramManager().getByName(hologram.getName()).refresh();
                    }
                }).runTaskTimerAsynchronously(core.getNginxMC, 1L, 1L); *///TODO: CMI-Hologram ChatPop
            } else {
                final Hologram hologram = HologramsAPI.createHologram(core.getNginxMC, player.getLocation().add(0.0D, 2.0D, 0.0D));
                hologram.appendTextLine("§b" + chat);

                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (hologram != null && player != null) {
                        if (target != player && target != null)
                            if (starchaser.servergamemode == SERVERGAMEMODE.Lobby) {
                                if (NginxPlayer.getNginxPlayer(player) != null && NginxPlayer.getNginxPlayer(target) != null && NginxPlayer.getNginxPlayer(player).getLobby_Number() == NginxPlayer.getNginxPlayer(target).getLobby_Number()) {
                                    hologram.getVisibilityManager().showTo(target);
                                } else {
                                    hologram.getVisibilityManager().hideTo(target);
                                }
                            } else {
                                hologram.getVisibilityManager().showTo(target);
                            }
                    } else {
                        hologram.getVisibilityManager().hideTo(target);
                    }
                }
                (new BukkitRunnable() {
                    int ticksRun;
                    int out_tricks = chat.length() * 5;
                    Location loc = player.getLocation();

                    public void run() {
                        try {
                            ++ticksRun;
                            if (player != null) {
                                loc = player.getLocation();
                            }
                            if (this.out_tricks < 100) {
                                this.out_tricks = 100;
                            }
                            if (player == null || !player.isOnline() || popupchat.this.force_remove) {
                                hologram.teleport(loc.clone().add(0.0D, 4.3D, 0.0D));
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        hologram.delete();
                                        this.cancel();
                                    }
                                }.runTaskLater(core.getNginxMC, 3L);
                                this.cancel();
                                return;
                            }
                            if (this.ticksRun > this.out_tricks) {
                                hologram.teleport(loc.clone().add(0.0D, 4.3D, 0.0D));
                            } else {
                                if (!(player.getLocation().getBlockX() == last_loc.getBlockX() && player.getLocation().getBlockY() == last_loc.getBlockY() && player.getLocation().getBlockZ() == last_loc.getBlockZ())) {
                                    hologram.teleport(loc.clone().add(0.0D, 3.15D, 0.0D));
                                } else {
                                    last_loc = player.getLocation();
                                }
                            }

                            if (this.ticksRun > this.out_tricks + 3) {
                                hologram.delete();
                                this.cancel();
                            }
                        } catch (IllegalArgumentException var2) {
                            if (hologram != null && hologram.isDeleted() == false) {
                                hologram.delete();
                            }
                            this.cancel();
                        }
                    }
                }).runTaskTimerAsynchronously(core.getNginxMC, 1L, 1L);
            }
        }

        public Player getOwner() {
            return this.player;
        }

        public void setForce_remove(boolean force_remove) {
            this.force_remove = force_remove;
        }
    }

    public static ArrayList<NginxPlayer> getPlayerLobby(int LobbyID) {
        ArrayList<NginxPlayer> list = new ArrayList();
        for (NginxPlayer np : core.PlayerRef) {
            if (np.getLobby_Number() == LobbyID) list.add(np);
        }
        return list;
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

class AnimateRank {
    private int frame_state = 0;
    private int current_frame = 0;
    final private long frame_speed;
    private AnimateFrame animateFrame;
    private int class_id;
    private int frame_step = 0;

    AnimateRank(int class_id, long frame_speed) {
        this.frame_speed = frame_speed;
        this.animateFrame = new AnimateFrame(class_id);
        this.class_id = class_id;
        StartPlayFrame();
    }

    public void renderFrame() {
        if (frame_state == 0) {
            return;
        }
        if (frame_step >= frame_speed){
            frame_step = 1;
            setFrame(getFrame() + 1);
            if (current_frame >= animateFrame.getFrameSize()) current_frame = 0;
        }else {
            frame_step++;
        }
    }

    public void setFrame(int frame) {
        this.current_frame = frame;
    }

    public int getFrame() {
        return current_frame;
    }

    public String getCurrentDisplay() {
        return animateFrame.getFrameString(current_frame);
    }

    public void StopPlayFrame() {
        frame_state = 0;
    }

    public void StartPlayFrame() {
        frame_state = 1;
    }

    public int getClassID() {
        return class_id;
    }
}

class AnimateFrame {
    final int class_id;
    String[][] class_data = new String[][]{
            {"&6&lS&a&lC","&a&lS&6&lC"},
            {"&3&lT&b&lI&f&lT&b&lA&3&lN", "&8&lTITAN"},
            {"&5&lH&d&lE&5&lR&d&lO", "&8&lH&d&lE&5&lR&d&lO", "&5&lH&8&lE&5&lR&d&lO", "&5&lH&d&lE&8&lR&d&lO", "&5&lH&d&lE&5&lR&8&lO", "&5&lH&d&lE&5&lR&d&lO"},
            {"&6&lM&8&lA&f&lS&8&lT&e&lE&8&lR", "&8&lM&e&lA&8&lS&f&lT&8&lE&6&lR", "&6&lM&8&lA&f&lS&8&lT&e&lE&8&lR", "&8&lM&e&lA&8&lS&f&lT&8&lE&6&lR"},
            {"&8&lLEGEND", "&8&lLE&7&lGE&8&lND", "&8&lL&a&lE&7&lGE&a&lN&8&lD", "&2&lL&a&lE&7&lGE&a&lN&2&lD", "&2&lL&a&lE&7&lGE&a&lN&2&lD", "&8&lL&a&lE&7&lGE&a&lN&8&lD", "&8&lLE&7&lGE&8&lND", "&8&lLEGEND"},
            {"&c&lS&e&lU&a&lP&3&lR&b&lE&5&lM&d&lE", "&d&lS&c&lU&e&lP&a&lR&3&lE&b&lM&5&lE", "&5&lS&d&lU&c&lP&e&lR&a&lE&3&lM&b&lE", "&b&lS&5&lU&d&lP&c&lR&e&lE&a&lM&3&lE", "&3&lS&b&lU&5&lP&d&lR&c&lE&e&lM&a&lE", "&a&lS&3&lU&b&lP&5&lR&d&lE&c&lM&e&lE", "&e&lS&a&lU&3&lP&b&lR&5&lE&d&lM&c&lE"},
            {"&1&lS&9&lT&3&lA&b&lF&b&lF", "&8&lS&9&lT&3&lA&b&lF&b&lF", "&8&lS&9&lT&3&lA&b&lF&b&lF", "&1&lS&9&lT&3&lA&b&lF&b&lF", "&1&lS&8&lT&3&lA&b&lF&b&lF", "&1&lS&8&lT&3&lA&b&lF&b&lF", "&1&lS&9&lT&3&lA&b&lF&b&lF", "&1&lS&9&lT&8&lA&b&lF&b&lF", "&1&lS&9&lT&8&lA&b&lF&b&lF", "&1&lS&9&lT&3&lA&b&lF&b&lF", "&1&lS&9&lT&3&lA&8&lF&b&lF", "&1&lS&9&lT&3&lA&8&lF&b&lF", "&1&lS&9&lT&3&lA&b&lF&b&lF", "&1&lS&9&lT&3&lA&b&lF&8&lF", "&1&lS&9&lT&3&lA&b&lF&8&lF", "&1&lS&9&lT&3&lA&b&lF&b&lF"},
            {"&f&lYOUTUBER", "&f&lYOUTUBER", "&4&lY&f&lOUTUBER", "&4&lYO&f&lUTUBER", "&4&lYOU&f&lTUBER", "&4&lYOUT&f&lUBER", "&4&lYOUTU&f&lBER", "&4&lYOUTUB&f&lER", "&4&lYOUTUBE&f&lR", "&4&lYOUTUBER", "&f&lY&4&lOUTUBER", "&f&lYO&4&lUTUBER", "&f&lYOU&4&lTUBER", "&f&lYOUT&4&lUBER", "&f&lYOUTU&4&lBER", "&f&lYOUTUB&4&lER", "&f&lYOUTUBE&4&lR", "&f&lYOUTUBER", "&f&lYOUTUBE&4&lR", "&f&lYOUTUB&4&lER", "&f&lYOUTU&4&lBER", "&f&lYOUT&4&lUBER", "&f&lYOU&4&lTUBER", "&f&lYO&4&lUTUBER", "&f&lY&4&lOUTUBER", "&4&lYOUTUBER", "&4&lYOUTUBE&f&lR", "&4&lYOUTUB&f&lER", "&4&lYOUTU&f&lBER", "&4&lYOUT&f&lUBER", "&4&lYOU&f&lTUBER", "&4&lYO&f&lUTUBER", "&4&lY&f&lOUTUBER", "&4&lYOUTUBER", "&4&lYOUTUBER"},
            {"EMPTY"},
            {"&8&lADMIN", "&4&lA&8&lDMIN", "&4&lAD&8&lMIN", "&4&lADM&8&lIN", "&4&lADMI&8&lN", "&4&lADMIN", "&4&lADMIN", "&8&lA&4&lDMIN", "&8&lAD&4&lMIN", "&8&lADM&4&lIN", "&8&lADMI&4&lN", "&8&lADMIN"},

    };

    AnimateFrame(int class_id) {
        this.class_id = class_id;
    }
    public int getFrameSize() {
        return class_data[class_id].length;
    }
    public String getFrameString(int frame) {
        try {
            return class_data[class_id][frame].toUpperCase().replaceAll("&", "§") + " §b";
        } catch (ArrayIndexOutOfBoundsException exc) {
            return "§b";
        }
    }
}
