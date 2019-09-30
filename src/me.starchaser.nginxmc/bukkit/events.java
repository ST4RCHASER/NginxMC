package me.starchaser.nginxmc.bukkit;

import com.comphenix.protocol.PacketType;
import com.nametagedit.plugin.NametagEdit;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static me.starchaser.nginxmc.bukkit.starchaser.getClassID;
import static me.starchaser.nginxmc.bukkit.starchaser.servergamemode;

public class events implements Listener {
    public static ArrayList<starchaser.popupchat> chat_history = new ArrayList();
    static String kick_msg = "§7Error: §cเกิดข้อผิดพลาดในการตรวจสอบข้อมูลของท่านโปรเลองใหม่ภายหลังหรือติตต่อแอดมิน!";

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        if (servergamemode == starchaser.SERVERGAMEMODE.Minigames || servergamemode == starchaser.SERVERGAMEMODE.Lobby){
            evt.setJoinMessage(null);
            if (evt.getPlayer() != null && NametagEdit.getApi() != null) {
                starchaser.updateNewNameTag(evt.getPlayer() , "§b§lWELCOME! §7");
            }
            evt.getPlayer().getInventory().clear();
            FastJoinTask(evt.getPlayer());
        }
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby){
            evt.getPlayer().teleport(core.spawn_point);
    }
    }
    @EventHandler
    public void onPlayerItemHoldEvent(PlayerItemHeldEvent e){
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && e.getPlayer().getWorld().equals(core.main_world)) {
            if (e.getNewSlot() == 5) {
                if (true) {
                    e.getPlayer().getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                    e.getPlayer().getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    e.getPlayer().getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    e.getPlayer().getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    e.getPlayer().sendMessage("§7PVP: §aได้เข้าสู่โหมด PVP แล้ว!");
                }
            }else if (e.getPreviousSlot() == 5) {
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
                    }
                } catch (SQLException var3) {
                    var3.printStackTrace();
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
        }

    }

    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent evt) {
        evt.setQuitMessage((String) null);
        starchaser.sendPlayerData(evt.getPlayer());
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
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "bs open gui " + e.getPlayer().getName());
            }
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
                            evt.getEntity().teleport(core.spawn_point);
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
    public void PlayerMoveEvt(PlayerMoveEvent evt) {
        if (evt.getTo().getY() < -5) {
            if (evt.getPlayer().getWorld().getName().equalsIgnoreCase(core.main_world.getName())) {
                evt.getPlayer().teleport(core.spawn_point);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent evt) {
        NginxPlayer npe = NginxPlayer.getNginxPlayer(evt.getPlayer());
        if (npe == null) {
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
                abc.sendMessage("§8। §f"+nginxPlayer.getLevel().getStr()+" §8। §r"+nginxPlayer.getTitle().getStr()+nginxPlayer.getPlayerClass().getStr()+"§7"+nginxPlayer.getName()+":§b "+evt.getMessage()+"");
            }
        }
        starchaser.Logger(starchaser.LOG_TYPE.CHAT, "§7Chat: §a" + evt.getPlayer().getName() + ": §f" + evt.getMessage());
        if (servergamemode == starchaser.SERVERGAMEMODE.Lobby && core.world_scam != "#NONE#" && core.world_scam != "#WIN#" && core.world_scam.equals(evt.getMessage())) {
            starchaser.BoardCastMsg("§7Reaction: §e" + evt.getPlayer().getName() + " §aได้รับ exp " + (core.world_scam.length() + core.world_scam.length()) + " และได้รับ " + ((core.world_scam.length() / 2) + 5) + " coins จากการชนะ ChatReaction");
            NginxPlayer.getNginxPlayer(evt.getPlayer()).getLevel().give_xp(((core.world_scam.length() + core.world_scam.length())) , false);
            NginxPlayer.getNginxPlayer(evt.getPlayer()).addCoins(((core.world_scam.length() / 2) + 5) , false);
            evt.setMessage(evt.getMessage().replaceAll(core.world_scam, "§8§l[ §d§l" + core.world_scam + " §8§l]§r"));
            core.world_scam = "#WIN#";
        }
        Boolean allow = true;
        NginxPlayer np = NginxPlayer.getNginxPlayer(evt.getPlayer());
        for (PotionEffect pot : evt.getPlayer().getActivePotionEffects()) {
            if (pot.getType().equals(PotionEffectType.INVISIBILITY)) allow = false;
        }
        if (evt.getPlayer().getWorld().equals(core.main_world)) {
            if (core.server_chat_pop && evt.getPlayer().getGameMode() != GameMode.SPECTATOR && allow) {
                if (np.isChatPOPEnabled()) {
                    for (starchaser.popupchat popupchat : chat_history) {
                        if (popupchat.getOwner() == evt.getPlayer()) {
                            popupchat.setForce_remove(true);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    chat_history.remove(popupchat);
                                }
                            }.runTaskLaterAsynchronously(core.getNginxMC, 10L);
                        }
                    }
                    chat_history.add(new starchaser.popupchat(evt.getPlayer(), evt.getMessage()));
                }
            }
        }

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
