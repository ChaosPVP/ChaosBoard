package org.chaospvp.board;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.chaospvp.board.impl.FactionsUUIDProvider;
import org.chaospvp.board.scoreboard.ScoreboardUpdateTask;
import org.chaospvp.board.scoreboard.SimpleScoreboard;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChaosBoard extends JavaPlugin {
    private static ChaosBoard instance;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        economy = getServer().getServicesManager()
                .getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
        SimpleScoreboard.precache();
        new ScoreboardUpdateTask(new FactionsUUIDProvider()).runTaskTimer(this, 0, 4);
    }


    public Economy getEconomy() {
        return economy;
    }

    public static ChaosBoard getInstance() {
        return instance;
    }
}
