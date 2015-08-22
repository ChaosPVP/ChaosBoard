package org.chaospvp.board.scoreboard;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.chaospvp.board.ChaosBoard;
import org.chaospvp.board.api.ChunkFaction;
import org.chaospvp.board.api.ChunkFactionProvider;
import org.chaospvp.board.api.ChunkInfo;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardUpdateTask extends BukkitRunnable {
    private ChunkFactionProvider provider;
    private Map<UUID, SimpleScoreboard> scoreboards = new HashMap<>();
    private int currentBucket = 0;
    private final int numBuckets = 5;
    private final int delta = 6;

    public ScoreboardUpdateTask(ChunkFactionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        currentBucket++;
        if (currentBucket == numBuckets) currentBucket = 0;
        Set<UUID> currentOnlineUuids = Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
        Set<UUID> toRemove = new HashSet<>(scoreboards.keySet());
        toRemove.removeAll(currentOnlineUuids);
        toRemove.forEach(scoreboards::remove);
        currentOnlineUuids.stream().filter(ChaosBoard.getInstance().applyUsers::contains)
                .forEach(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            updateScoreboard(p);
        });
    }

    private void updateScoreboard(Player p) {
        String chaos = ChatColor.WHITE + "" + ChatColor.BOLD + "Chaos"
                + ChatColor.DARK_RED + "" + ChatColor.BOLD + "PVP";
        SimpleScoreboard scoreboard = scoreboards.get(p.getUniqueId());
        boolean forceCreate = false;
        if (scoreboard == null) {
            scoreboard = new SimpleScoreboard(chaos);
            scoreboards.put(p.getUniqueId(), scoreboard);
            forceCreate = true;
            p.setScoreboard(scoreboard.getScoreboard());
        }
        int radius = 4;
        if (Math.floorMod(p.getName().hashCode(), numBuckets) == currentBucket || forceCreate) {
            List<List<ChunkFaction>> cfRange = new ArrayList<>();
            ChunkInfo base = new ChunkInfo(p.getLocation());
            for (int dz = -radius; dz <= radius; dz++) {
                List<ChunkFaction> row = new ArrayList<>();
                for (int dx = -radius; dx <= radius; dx++) {
                    ChunkInfo next = base.addAndCreate(dx, dz);
                    row.add(provider.getChunkFactionFor(next, p));
                }
                cfRange.add(row);
            }
            List<String> lines = new ArrayList<>();
            for (int i = 0; i < cfRange.size(); i++) {
                List<ChunkFaction> row = cfRange.get(i);
                if (i == 0) {
                    row.remove(row.size() - 1);
                }
                lines.add(generateLine(row));
            }
            for (int i = 0; i < lines.size(); i++) {
                String append;
                if (i == 0) {
                    append = "&7" + DirectionUtils.getDirectionArrow(p);
                } else {
                    append = "&" + (i + 1);
                }
                scoreboard.add(lines.get(i) + append, lines.size() - 1 - i + delta);
            }
        } else {
            int topScore = 2 * radius + delta;
            String top = scoreboard.get(topScore, "");
            top = removeArrow(top) + "&7" + DirectionUtils.getDirectionArrow(p);
            scoreboard.add(top, topScore);
        }
        // Factions
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);
        String facName = fPlayer.getFaction().getTag();
        scoreboard.add(ChatColor.GRAY + "Faction", 6);
        scoreboard.add(ChatColor.RED + facName, 5);
        int money = (int) ChaosBoard.getInstance().getEconomy().getBalance(p);
        scoreboard.add(ChatColor.GRAY + "Money", 4);
        scoreboard.add(ChatColor.RED + "" + money, 3);
        scoreboard.update();
    }

    private static String removeArrow(String str) {
        return str.substring(0, str.lastIndexOf("7") - 1);
    }

    private String generateLine(List<ChunkFaction> row) {
        char box = '\u2B1B';
        ChunkFaction previous = null;
        StringBuilder sb = new StringBuilder();
        for (ChunkFaction cf : row) {
            if (cf != previous) {
                sb.append(cf);
                previous = cf;
            }
            sb.append(box);
        }
        return sb.toString();
    }
}
