package me.starchaser.nginxmc.bukkit;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static me.starchaser.nginxmc.bukkit.core.*;
import static me.starchaser.nginxmc.bukkit.starchaser.*;

public class events implements Listener {
    public static ArrayList<starchaser.popupchat> chat_history = new ArrayList();
    static String kick_msg = "§7Error: §cเกิดข้อผิดพลาดในการตรวจสอบข้อมูลของท่านโปรดลองใหม่ภายหลัง หรือติตต่อแอดมิน!";

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) throws Exception {
        Player p = evt.getPlayer();
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            p.teleport(spawn_point);
            p.setGameMode(GameMode.ADVENTURE);
            p.setHealth(20);
            p.setFoodLevel(20);
        }
        if (servergamemode == starchaser.SERVERGAMEMODE.Minigames || servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            evt.setJoinMessage(null);
            if (evt.getPlayer() != null) {
                starchaser.AddPlayerChatPOP(evt.getPlayer(), "§e๐ §b§lWELCOME§f!§e ๐");
            }
            if (clear_on_join) {
                evt.getPlayer().getInventory().clear();
            }
            FastJoinTask(evt.getPlayer());
        }
    }

    @EventHandler
    public void ongamemodeChange(PlayerGameModeChangeEvent e) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getPlayer().getWorld().equals(core.main_world)) {
            if (e.getNewGameMode() == GameMode.CREATIVE) {
                NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
                if (np != null) {
                    np.setHideTitle(false);
                }
                if (e.getPlayer().getInventory().getHeldItemSlot() == 5 || e.getPlayer().getInventory().getHeldItemSlot() == 6) {
                    e.getPlayer().getInventory().setItem(1, new ItemStack(Material.AIR));
                    e.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
                    e.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
                    e.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
                    e.getPlayer().sendMessage("§7PVP: §cออกจากโหมด PVP แล้ว!");
                    e.getPlayer().getInventory().setHeldItemSlot(0);
                }
                e.getPlayer().getInventory().clear();
            } else if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
                e.getPlayer().getInventory().clear();
                starchaser.giveItemLobby(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void playerDroping(PlayerDropItemEvent e) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getPlayer().getGameMode() == GameMode.ADVENTURE)
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemHoldEvent(PlayerItemHeldEvent e) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getPlayer().getWorld().equals(core.main_world) && e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
            if (e.getNewSlot() == 5 || e.getNewSlot() == 6) {
                if (true) {
                    if (np != null) {
                        np.setHideTitle(true);
                    }
                    if (e.getNewSlot() == 6) {
                        ItemStack arrow = new ItemStack(Material.ARROW, 64);
                        e.getPlayer().getInventory().setItem(2, arrow);
                    } else {
                        e.getPlayer().getInventory().setItem(2, new ItemStack(Material.AIR));
                    }
                    if (e.getPreviousSlot() != 5 && e.getPreviousSlot() != 6) {
                        e.getPlayer().getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                        e.getPlayer().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        e.getPlayer().getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                        e.getPlayer().getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                        e.getPlayer().sendMessage("§7PVP: §aได้เข้าสู่โหมด PVP แล้ว!");
                    }
                }
            } else if (e.getPreviousSlot() == 5 || e.getPreviousSlot() == 6) {
                if (np != null) {
                    np.setHideTitle(false);
                }
                if (e.getPreviousSlot() == 6) e.getPlayer().getInventory().setItem(2, new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
                e.getPlayer().sendMessage("§7PVP: §cออกจากโหมด PVP แล้ว!");
            }
        }
    }

    public static void FastJoinTask(Player p) {
        if (ponlawat.getDone_playerdata(p.getName(), null) == null) {
            p.sendMessage("§7Account: §eกำลังโหลดข้อมูล...");
        }
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            starchaser.giveItemLobby(p);
        }
        p.getInventory().setHeldItemSlot(0);

        if (!newMethodloadPlayer) {
            try {
                ResultSet result_player = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + p.getName() + "'");
                if (!result_player.isBeforeFirst()) {
                    p.sendMessage("§7Account: §aเข้าเล่นครั้งแรกระบบกำลังสร้างบัญชี กรุณารอสักครู่...");
                    starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§bAccount §7" + p.getName() + "§b not found Creating...");
                    starchaser.CreateAccount(p);
                    p.sendMessage("§7Account: §eกำลังอ่านโปรไฟล์...");
                    (new BukkitRunnable() {
                        public void run() {
                            if (!starchaser.getPlayerData(p, null)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        p.kickPlayer(kick_msg);
                                    }
                                }.runTask(core.getNginxMC);
                            }
                            this.cancel();
                        }
                    }).runTaskTimerAsynchronously(core.getNginxMC, 40L, 20L);
                } else {
                    p.sendMessage("§7Account: §eกำลังอ่านโปรไฟล์...");
                    (new BukkitRunnable() {
                        public void run() {
                            if (p != null) {
                                if (!starchaser.getPlayerData(p, null)) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            p.kickPlayer(kick_msg);
                                        }
                                    }.runTask(core.getNginxMC);
                                }
                            }
                            this.cancel();
                        }
                    }).runTaskTimerAsynchronously(core.getNginxMC, 40L, 20L);
                }
            } catch (SQLException exx) {
                exx.printStackTrace();
                starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§cError on get player data... (TASK: events.onJoin) [" + p.getName() + "]");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.kickPlayer(kick_msg);
                    }
                }.runTask(core.getNginxMC);
                return;
            }
        }
    }

    @EventHandler
    public void asyncJoin(AsyncPlayerPreLoginEvent e) {
        if (newMethodloadPlayer && e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED && !ponlawat.join_playerdata.contains(e.getName())) {
            ponlawat.join_playerdata.add(e.getName());
        }
    }

    public static BukkitTask loadJoinSQL() {
        return (new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(bo ->{
                    if(NginxPlayer.getNginxPlayer(bo) == null && ponlawat.getJoin_playerdata(bo.getName()) == null && ponlawat.getCurrent_playerdata(bo.getName()) == null && ponlawat.getDone_playerdata(bo.getName(), null) == null)
                        ponlawat.join_playerdata.add(bo.getName());

                    if(!ponlawat.join_playerdata.contains(bo.getName()) && !ponlawat.current_playerdata.contains(bo.getName()) && (ponlawat.getDone_playerdata(bo.getName(), null) != null) && !(NginxPlayer.getNginxPlayer(bo) != null))
                        ponlawat.done_playerdata.remove(ponlawat.getDone_playerdata(bo.getName(), null));
                });

                loadJoinSQL_task();
            }
        }).runTaskTimerAsynchronously(core.getNginxMC, 20, newMethodloadPlayer_period);
    }

    public static void loadJoinSQL_task() {
        ArrayList<String> t_join_playerdata = ponlawat.join_playerdata;
        ArrayList<String> t_current_playerdata = ponlawat.current_playerdata;
        ArrayList<Object[]> t_done_playerdata = ponlawat.done_playerdata;
        String sqlQueue = "";
        int qid = 0;

        for (String ps : t_join_playerdata) {
            if (!t_current_playerdata.contains(ps))
                t_current_playerdata.add(ps);
        }
        for (String ps : t_current_playerdata) {
            t_join_playerdata.remove(ps);
            if (qid > 0) {
                sqlQueue = ("\"" + ps + "\",") + sqlQueue;
            } else {
                sqlQueue = "\"" + ps + "\"";
            }
            qid++;
        }
        if (qid > 0) {
            try {
                ResultSet result_player = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE MATCH`username` AGAINST('" + sqlQueue + " @8' IN BOOLEAN MODE)");
                result_player.next();
                for (String f : t_current_playerdata) {
                    if ((!result_player.isAfterLast() && !result_player.isBeforeFirst())) {
                        //id[0], username[1], level[2], xp[3], title[4], coins[5], feather[6]
                        Object[] o = {result_player.getInt("id"), result_player.getString("username"), result_player.getInt("level"), result_player.getInt("xp"), result_player.getInt("title"), result_player.getInt("coins"), result_player.getInt("feather")};
                        if (ponlawat.getDone_playerdata(o[1].toString(), t_done_playerdata) == null) {
                            t_done_playerdata.add(o);
                            ponlawat.done_playerdata.add(o);
                        }

                        Player p = Bukkit.getPlayer(o[1].toString());
                        if (p != null) {
                            if (!starchaser.getPlayerData(p, o)) {
                                Bukkit.getScheduler().runTask(getNginxMC, () -> p.sendMessage(kick_msg));
                            }
                        }

                        starchaser.Logger(LOG_TYPE.DEBUG, "join-hasData(" + t_done_playerdata.size() + ") | " + o[0] + " | " + o[1] + " | " + o[2] + " | " + o[3] + " | " + o[4] + " | " + o[5] + " | " + o[6]);
                        result_player.next();
                    }
                }
                for (Object[] f : t_done_playerdata) {
                    t_current_playerdata.remove(f[1]);
                    ponlawat.current_playerdata.remove(f[1]);
                }
                for (String f : t_current_playerdata) {
                    starchaser.Logger(LOG_TYPE.DEBUG, "join-newPlayer | " + f);

                    Player p = Bukkit.getPlayer(f);
                    if (p != null) {
                        p.sendMessage("§7Account: §aเข้าเล่นครั้งแรก กำลังสร้างโปรไฟล์ กรุณารอสักครู่...");
                        starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§bAccount §7" + p.getName() + "§b not found Creating...");
                        if (starchaser.CreateAccount(p)) {
                            if (!starchaser.getPlayerData(p, null)) {
                                Bukkit.getScheduler().runTask(getNginxMC, () -> p.kickPlayer(kick_msg));
                            } else {
                                ponlawat.done_playerdata.add(new Object[]{NginxPlayer.getNginxPlayer(p).getId(), f, 0, 0, 0, 0, 0});
                            }
                        }
                        ponlawat.current_playerdata.remove(f);
                    }

                }
            } catch (SQLException exx) {
                exx.printStackTrace();
                starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§cError on get player data... (TASK: events.loadPlayerSQL)");

                for (String f : t_current_playerdata) {
                    Player p = Bukkit.getPlayer(f);
                    if (p != null) {
                        p.sendMessage(kick_msg);
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        World ew = e.getWorld();
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.toWeatherState()) {
            e.setCancelled(true);
            ew.setThundering(false);
            ew.setWeatherDuration(0);
        }

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            e.setCancelled(true);
            if (e.getFoodLevel() < 20) {
                e.setFoodLevel(20);
            }
        }

    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent evt) {
        evt.setQuitMessage((String) null);
        starchaser.sendPlayerData(evt.getPlayer());
        for (NginxPlayer np : core.PlayerRef) {
            try {
                if (np.getName().equalsIgnoreCase(evt.getPlayer().getName())) {
                    np.setRemoved();
                }
            } catch (Exception exc) {
                if (core.debug) {
                    exc.printStackTrace();
                }
            }
        }
        (new BukkitRunnable() {
            public void run() {
                try {
                    NginxPlayer.removeNginxPlayer(evt.getPlayer());
                    this.cancel();
                } catch (NullPointerException var2) {
                    this.cancel();
                }

            }
        }).runTaskTimerAsynchronously(core.getNginxMC, 20L, 20L);
        ponlawat.join_playerdata.remove(evt.getPlayer().getName());
        ponlawat.current_playerdata.remove(evt.getPlayer().getName());
        ponlawat.done_playerdata.remove(ponlawat.getDone_playerdata(evt.getPlayer().getName(), null));
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && core.getNms().getPlayerMainHand(e.getPlayer()) != null) {
            if (core.getNms().getPlayerMainHand(e.getPlayer()).getType() == Material.COMPASS && core.getNms().getPlayerMainHand(e.getPlayer()).getItemMeta().getDisplayName() != null && core.getNms().getPlayerMainHand(e.getPlayer()).getItemMeta().getDisplayName().equals("§bรายชื่อเซิร์ฟเวอร์ §7(คลิกขวา)")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dm open server " + e.getPlayer().getName());
            }
            if (core.getNms().getPlayerMainHand(e.getPlayer()).getType() == Material.HOPPER && core.getNms().getPlayerMainHand(e.getPlayer()).getItemMeta().getDisplayName() != null && core.getNms().getPlayerMainHand(e.getPlayer()).getItemMeta().getDisplayName().equals("§eรายชื่อล๊อบบี้ §7(คลิกขวา)")) {
                if (NginxPlayer.getNginxPlayer(e.getPlayer()) == null) return;
                Inventory inventory = Bukkit.createInventory(null, 36, "§a§l▶ §b§nรายชื่อล๊อบบี้§r §a§l◀");
                ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§r");
                is.setItemMeta(im);

                for (int i = 0; i < 9; i++) {
                    inventory.setItem(i, is);
                }
                for (int i = 27; i < 36; i++) {
                    inventory.setItem(i, is);
                }
                int server_id = 1;
                for (int i = 9; i < 27; i++) {
                    is = new ItemStack(Material.RAW_CHICKEN, server_id);
                    if (getPlayerLobby(server_id).size() >= virtual_lobby_player_size)
                        is.setType(Material.COOKED_CHICKEN);
                    im = is.getItemMeta();
                    im.setDisplayName("§d§l✪ §b§lLobby §a§l" + server_id + " §d§l✪");
                    if (server_id == NginxPlayer.getNginxPlayer(e.getPlayer()).getLobby_Number()) {
                        im.addEnchant(Enchantment.DURABILITY, 1, false);
                        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    ArrayList<String> lore = new ArrayList();
                    lore.add("§r");
                    lore.add("§b§l➣ §dผู้เล่นตอนนี้: §e" + getPlayerLobby(server_id).size() + "§f/§e" + virtual_lobby_player_size);
                    lore.add("§r");
                    for (NginxPlayer np : starchaser.getPlayerLobby(server_id)) {
                        lore.add("§b" + np.getName());
                    }
                    im.setLore(lore);
                    is.setItemMeta(im);
                    inventory.setItem(i, is);
                    server_id++;
                }
                e.getPlayer().openInventory(inventory);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (core.getNms().getPlayerMainHand(e.getPlayer()).getType() == Material.HOPPER && core.getNms().getPlayerMainHand(e.getPlayer()).getItemMeta().getDisplayName() != null && core.getNms().getPlayerMainHand(e.getPlayer()).getItemMeta().getDisplayName().equals("§eรายชื่อล๊อบบี้ §7(คลิกขวา)")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDamage(EntityDamageEvent evt) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            if (evt.getEntity() instanceof Player) {
                if (evt.getCause().equals(EntityDamageEvent.DamageCause.VOID) && void_spawn)
                    evt.getEntity().teleport(spawn_point);
                if (((Player) evt.getEntity()).getInventory().getHeldItemSlot() == 5 || ((Player) evt.getEntity()).getInventory().getHeldItemSlot() == 6) {
                    if (evt.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || evt.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                        evt.setCancelled(false);
                        return;
                    }
                }
            }
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerDamagePVP(EntityDamageByEntityEvent evt) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            if (evt.getEntity().getWorld().equals(core.main_world)) {
                if ((evt.getDamager() instanceof Player || evt.getDamager() instanceof Arrow) && evt.getEntity() instanceof Player) {
                    if (
                            (((Player) evt.getEntity()).getInventory().getHeldItemSlot() == 5 || ((Player) evt.getEntity()).getInventory().getHeldItemSlot() == 6) && (evt.getDamager() instanceof Arrow || (((Player) evt.getDamager()).getInventory().getHeldItemSlot() == 5 || ((Player) evt.getDamager()).getInventory().getHeldItemSlot() == 6))
                    ) {
                        if (((Player) evt.getEntity()).getHealth() <= evt.getDamage()) {
                            String apt;
                            String damagerName;
                            Player ply;
                            if (evt.getDamager() instanceof Arrow) {
                                Arrow a = ((Arrow) evt.getDamager());
                                damagerName = ((Player) a.getShooter()).getDisplayName();
                                apt = "ลูกธนูของ";
                                ply = ((Player) a.getShooter());
                                core.getNms().removeArrowonPlayer((Player) evt.getEntity());
                            } else {
                                damagerName = evt.getDamager().getName();
                                apt = "";
                                ply = ((Player) evt.getDamager());
                            }
                            starchaser.BoardCastMsg("§7PVP: §b" + evt.getEntity().getName() + " §cถูกสังหารโดย" + apt + " §b" + damagerName);
                            NginxPlayer damager_np = NginxPlayer.getNginxPlayer(ply);
                            damager_np.getLevel().give_xp(10, false);
                            ply.sendMessage("§7Level: §aคุณได้รับ 10 XP จากการสังหาร §7" + evt.getEntity().getName());
                            evt.getEntity().teleport(spawn_point);
                            ((Player) evt.getEntity()).setHealth(((Player) evt.getEntity()).getHealthScale());
                            ((Player) evt.getEntity()).getInventory().setHeldItemSlot(0);
                            ((Player) evt.getEntity()).getInventory().setItem(1, new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setBoots(new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setChestplate(new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setLeggings(new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setHelmet(new ItemStack(Material.AIR));
                        }
                        evt.setCancelled(false);
                    } else {
                        evt.setCancelled(true);
                    }
                } else {
                    evt.setCancelled(true);
                }
            }
        }
    }

    /*
        @EventHandler
        public void onInvClick(InventoryClickEvent evt) {
            if(servergamemode == SERVERGAMEMODE.Lobby) {
                if (evt.getClickedInventory() != null && evt.getClickedInventory().getTitle() != null && evt.getClickedInventory().getTitle().equalsIgnoreCase("§a§l▶ §b§nรายชื่อล๊อบบี้§r §a§l◀")){
                    if (evt.getCurrentItem() != null && evt.getCurrentItem().getType() != Material.RAW_CHICKEN && evt.getCurrentItem().getType() != Material.COOKED_CHICKEN) {
                        evt.setCancelled(true);
                        return;
                    }else {
                        NginxPlayer.getNginxPlayer((Player) evt.getWhoClicked()).setLobby_Number(evt.getCurrentItem().getAmount());
                        evt.getWhoClicked().closeInventory();
                    }
                    evt.setCancelled(true);
                }
            }
        }
        */
    @EventHandler
    public void onPlayerChat(PlayerChatEvent evt) {
        NginxPlayer np = NginxPlayer.getNginxPlayer(evt.getPlayer());
        if (np == null) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage("§7Chat: §cโปรดรอสักครู่...");
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
                    ResultSet resultSet;
                    try {
                        resultSet = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + evt.getPlayer().getName() + "'");
                        if (resultSet.next()) {
                            int wp_count = resultSet.getInt("wp");
                            if (wp_count >= 4) {
                                evt.getPlayer().sendMessage("§7Chat: §cแชทถูกระงับการใช้งานเนื่องจากคุณมี warnpoint มากว่า 3 แล้วถ้าต้องการปลดสามารถปลดได้ที่ http://shop.siamcraft.net");
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        evt.getPlayer().kickPlayer("§7Chat: §cคุณไม่สามารถใช้แชทได้เนื่องจาก แชทถูกระงับการใช้งานเนื่องจากคุณมี warnpoint มากว่า 3 แล้วถ้าต้องการปลดสามารถปลดได้ที่ http://shop.siamcraft.net");
                                    }
                                }.runTask(core.getNginxMC);
                                evt.setCancelled(true);
                                return;
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskAsynchronously(core.getNginxMC);
        if (core.manage_chat) {
            evt.setCancelled(true);
            NginxPlayer nginxPlayer = NginxPlayer.getNginxPlayer(evt.getPlayer());
            for (Player abc : Bukkit.getOnlinePlayers()) {
                abc.sendMessage("§8। §f" + nginxPlayer.getLevel().getStr() + " §8। §r" + nginxPlayer.getTitle().getStr() + nginxPlayer.getPlayerClass().getStr() + "§7" + nginxPlayer.getName() + ":§b " + evt.getMessage() + "");
            }
        }
        starchaser.Logger(starchaser.LOG_TYPE.CHAT, "§7Chat: §a" + evt.getPlayer().getName() + ": §f" + evt.getMessage());
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && core.world_scam != "#NONE#" && core.world_scam != "#WIN#" && core.world_scam.equals(evt.getMessage())) {
            starchaser.BoardCastMsg("§7Reaction: §e" + evt.getPlayer().getName() + " §aได้รับ exp " + (core.world_scam.length() + core.world_scam.length()) + " และได้รับ " + ((core.world_scam.length() / 2) + 5) + " coins จากการชนะ ChatReaction");
            NginxPlayer.getNginxPlayer(evt.getPlayer()).getLevel().give_xp(((core.world_scam.length() + core.world_scam.length())), false);
            NginxPlayer.getNginxPlayer(evt.getPlayer()).addCoins(((core.world_scam.length() / 2) + 5), false);
            evt.setMessage("§8§l[ §d§l" + core.world_scam + " §8§l]§r");
            core.world_scam = "#WIN#";
        }

        if (!evt.isCancelled()) {
            try {
                starchaser.AddPlayerChatPOP(evt.getPlayer(), evt.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent evt) {
        NginxPlayer np = NginxPlayer.getNginxPlayer(evt.getPlayer());
        if (np == null) {
            evt.setCancelled(true);
            return;
        }
        evt.setFormat(evt.getFormat().replaceAll("_nginx_level_", np.getLevel().getStr())
                .replaceAll("_nginx_xp_", String.valueOf(np.getLevel().getXP()))
                .replaceAll("_nginx_level_raw_", String.valueOf(np.getLevel().get_Int()))
                .replaceAll("_nginx_xp_percent_", String.valueOf(np.getLevel().getXPPercent()))
                .replaceAll("_nginx_xp_bar_", np.getLevel().getXPBar())
                .replaceAll("_nginx_id_", String.valueOf(np.getId()))
                .replaceAll("_nginx_coins_", String.valueOf(np.getCoins()))
                .replaceAll("_nginx_ooc_", String.valueOf(np.getOOC_Count()))
                .replaceAll("_nginx_title_str_", np.getTitle().getStr())
                .replaceAll("_nginx_title_id_", String.valueOf(np.getTitle().getId()))
                .replaceAll("_nginx_paid_points_", String.valueOf(np.getPaid_points())
                        .replaceAll("_nginx_reward_points_", String.valueOf(np.getReward_points())
                        )));
    }
}