package me.starchaser.nginxmc.bukkit;

import com.comphenix.protocol.PacketType;
import gnu.trove.impl.sync.TSynchronizedRandomAccessDoubleList;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static me.starchaser.nginxmc.bukkit.core.*;
import static me.starchaser.nginxmc.bukkit.starchaser.*;

public class events implements Listener {
    public static ArrayList<starchaser.popupchat> chat_history = new ArrayList();
    static String kick_msg = "§7Error: §cเกิดข้อผิดพลาดในการตรวจสอบข้อมูลของท่านโปรเลองใหม่ภายหลังหรือติตต่อแอดมิน!";

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        Player p = evt.getPlayer();
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby){
            p.teleport(spawn_point);
        }
        if (servergamemode == starchaser.SERVERGAMEMODE.Minigames || servergamemode == starchaser.SERVERGAMEMODE.Lobby){
            evt.setJoinMessage(null);
            if (evt.getPlayer() != null) {
                starchaser.AddPlayerChatPOP(evt.getPlayer(),"§b§lWELCOME! §7");
            }
            evt.getPlayer().getInventory().clear();
            FastJoinTask(evt.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerItemHoldEvent(PlayerItemHeldEvent e){
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getPlayer().getWorld().equals(core.main_world)) {
            NginxPlayer np = NginxPlayer.getNginxPlayer(e.getPlayer());
            if (e.getNewSlot() == 5) {
                if (true) {
                    if (np != null) {
                        np.setHideTitle(true);
                    }
                    e.getPlayer().getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    e.getPlayer().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    e.getPlayer().getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    e.getPlayer().sendMessage("§7PVP: §aได้เข้าสู่โหมด PVP แล้ว!");
                }
            }else if (e.getPreviousSlot() == 5) {
                if (np != null) {
                    np.setHideTitle(false);
                }
                e.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setChestplate(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setLeggings(new ItemStack(Material.AIR));
                e.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
                e.getPlayer().sendMessage("§7PVP: §cออกจากโหมด PVP แล้ว!");
            }
        }
    }

    public static void FastJoinTask(Player p) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            starchaser.giveItemLobby(p);
            p.getInventory().setHeldItemSlot(0);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    ResultSet result_player = core.getSqlConnection().createStatement().executeQuery("SELECT * FROM `players` WHERE `username` LIKE '" + p.getName() + "'");
                    if (!result_player.isBeforeFirst()) {
                        p.sendMessage("§7Account: §aเข้าเล่นครั้งแรกระบบกำลังสร้างบัญชี กรุณารอสักครู่...");
                        starchaser.Logger(starchaser.LOG_TYPE.PLAYER, "§bAccount §7" + p.getName() + "§b not found Creating...");
                        starchaser.CreateAccount(p);
                        p.sendMessage("§7Account: §eกำลังอ่านโปรไฟล์...");
                        (new BukkitRunnable() {
                            public void run() {
                                if (!starchaser.getPlayerData(p)) {
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
                                    if (!starchaser.getPlayerData(p)) {
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
        }.runTaskAsynchronously(core.getNginxMC);
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
            }catch (Exception exc) {
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
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getPlayer().getItemInHand() != null) {
            if (e.getPlayer().getItemInHand().getType() == Material.COMPASS && e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§bรายชื่อเซิร์ฟเวอร์ §7(คลิกขวา)")) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "dm open server " + e.getPlayer().getName());
            }
            if (e.getPlayer().getItemInHand().getType() == Material.HOPPER && e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§eรายชื่อล๊อบบี้ §7(คลิกขวา)")) {
                if (NginxPlayer.getNginxPlayer(e.getPlayer()) == null) return;
                Inventory inventory = Bukkit.createInventory(null,36,"§a§l▶ §b§nรายชื่อล๊อบบี้§r §a§l◀");
                ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE,1,(byte)1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§r");
                is.setItemMeta(im);

                for (int i = 0; i < 9; i++) {
                    inventory.setItem(i,is);
                }
                for (int i = 27; i < 36; i++) {
                    inventory.setItem(i,is);
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
        if (e.getPlayer().getItemInHand().getType() == Material.HOPPER && e.getPlayer().getItemInHand().getItemMeta().getDisplayName() != null && e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§eรายชื่อล๊อบบี้ §7(คลิกขวา)")){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void PlayerDamage(EntityDamageEvent evt) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            evt.setCancelled(true);
            if (evt.getEntity() instanceof Player) {
                if (((Player) evt.getEntity()).getInventory().getHeldItemSlot() == 5){
                    if (evt.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                        evt.setCancelled(false);
                        return;
                    }
                }
            }
        }
    }
    @EventHandler
    public void PlayerDamagePVP(EntityDamageByEntityEvent evt) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby) {
            if (evt.getEntity().getWorld().equals(core.main_world)) {
                if (evt.getEntity() instanceof Player && evt.getDamager() instanceof Player) {
                    if (((Player) evt.getEntity()).getInventory().getHeldItemSlot() == 5 && ((Player) evt.getDamager()).getInventory().getHeldItemSlot() == 5){
                        if (((Player) evt.getEntity()).getHealth() <= evt.getDamage()) {
                            starchaser.BoardCastMsg("§7PVP: §b" + evt.getEntity().getName() + " §cถูกสังหารโดย §b" + evt.getDamager().getName());
                            NginxPlayer damager_np = NginxPlayer.getNginxPlayer((Player) evt.getDamager());
                            damager_np.getLevel().give_xp(10 , false);
                            ((Player) evt.getDamager()).sendMessage("§7Level: §aคุณได้รับ 10 XP จากการสังหาร §7" + evt.getEntity().getName());
                            evt.getEntity().teleport(spawn_point);
                            ((Player) evt.getEntity()).setHealth(((Player) evt.getEntity()).getMaxHealth());
                            ((Player) evt.getEntity()).getInventory().setHeldItemSlot(0);
                            ((Player) evt.getEntity()).getInventory().setBoots(new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setChestplate(new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setLeggings(new ItemStack(Material.AIR));
                            ((Player) evt.getEntity()).getInventory().setHelmet(new ItemStack(Material.AIR));
                        }
                        evt.setCancelled(false);
                    }else {
                        evt.setCancelled(true);
                    }
                }else {
                    evt.setCancelled(true);
                }
            }
        }
    }

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
    @EventHandler
    public void PlayerMoveEvt(PlayerMoveEvent evt) {
        PlayerMoveEvent e = evt;
        if (evt.getTo().getY() < -5) {
            if (evt.getPlayer().getWorld().getName().equalsIgnoreCase(core.main_world.getName())) {
                if (void_spawn) {
                    evt.getPlayer().teleport(spawn_point);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent evt) {
        NginxPlayer np = NginxPlayer.getNginxPlayer(evt.getPlayer());
        if (np == null) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage("§7Chat: §cโปรดรอสักครู่...");
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
                abc.sendMessage("§8। §f"+nginxPlayer.getLevel().getStr()+" §8। §r"+nginxPlayer.getTitle().getStr()+nginxPlayer.getPlayerClass().getStr()+"§7"+nginxPlayer.getName()+":§b "+evt.getMessage()+"");
            }
        }
        starchaser.Logger(starchaser.LOG_TYPE.CHAT, "§7Chat: §a" + evt.getPlayer().getName() + ": §f" + evt.getMessage());
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && core.world_scam != "#NONE#" && core.world_scam != "#WIN#" && core.world_scam.equals(evt.getMessage())) {
            starchaser.BoardCastMsg("§7Reaction: §e" + evt.getPlayer().getName() + " §aได้รับ exp " + (core.world_scam.length() + core.world_scam.length()) + " และได้รับ " + ((core.world_scam.length() / 2) + 5) + " coins จากการชนะ ChatReaction");
            NginxPlayer.getNginxPlayer(evt.getPlayer()).getLevel().give_xp(((core.world_scam.length() + core.world_scam.length())) , false);
            NginxPlayer.getNginxPlayer(evt.getPlayer()).addCoins(((core.world_scam.length() / 2) + 5) , false);
            evt.setMessage("§8§l[ §d§l" + core.world_scam + " §8§l]§r");
            core.world_scam = "#WIN#";
        }

       starchaser.AddPlayerChatPOP(evt.getPlayer(),evt.getMessage());

    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent evt) {
        NginxPlayer np = NginxPlayer.getNginxPlayer(evt.getPlayer());
        if (np == null){
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
