package org.chaospvp.board;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.chaospvp.board.impl.FactionsUUIDProvider;
import org.chaospvp.board.scoreboard.ScoreboardUpdateTask;
import org.chaospvp.board.scoreboard.SimpleScoreboard;

import java.util.*;
import java.util.stream.Collectors;

public class ChaosBoard extends JavaPlugin {
    private static ChaosBoard instance;
    private Economy economy;
    private Set<UUID> disabledPlayers = new HashSet<>();
    private static String prefix;
    private static String title;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        economy = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        SimpleScoreboard.preloadCache();
        new ScoreboardUpdateTask(new FactionsUUIDProvider()).runTaskTimer(this, 0, 4);
        disabledPlayers.addAll(getConfig().getStringList("disabled")
                .stream().map(UUID::fromString).collect(Collectors.toList()));
        new BukkitRunnable() {
            @Override
            public void run() {
                saveDisabled();
            }
        }.runTaskTimerAsynchronously(this, 20, 12000);
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix", "&8[&4&lChaos&f&lBoard&8]")
                + " ");
        title = ChatColor.translateAlternateColorCodes('&', getConfig().getString("title", "&4&lChaos&f&lPVP"));
    }

    @Override
    public void onDisable() {
        saveDisabled();
    }

    private void saveDisabled() {
        List<String> disabledList = disabledPlayers.stream().map(UUID::toString).collect(Collectors.toList());
        getConfig().set("disabled", disabledList);
        saveConfig();
    }

    public Economy getEconomy() {
        return economy;
    }

    public static ChaosBoard getInstance() {
        return instance;
    }

    public Set<UUID> getDisabledPlayers() {
        return disabledPlayers;
    }

    public static String getTitle() {
        return title;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        UUID uuid = ((Player) sender).getUniqueId();
        if (cmd.getName().equals("sbtoggle")) {
            if (disabledPlayers.contains(uuid)) {
                disabledPlayers.remove(uuid);
                sender.sendMessage(prefix + ChatColor.GREEN + "Scoreboard re-enabled.");
            } else {
                disabledPlayers.add(uuid);
                ((Player) sender).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                sender.sendMessage(prefix + ChatColor.YELLOW + "Scoreboard disabled.");
            }
            return true;
        }
        return false;
    }
}
