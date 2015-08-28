package org.chaospvp.board.scoreboard;

import com.google.common.base.Strings;
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

public class ScoreboardUpdateTask extends BukkitRunnable {
    private ChunkFactionProvider provider;
    private Map<UUID, SimpleScoreboard> scoreboards = new HashMap<>();
    private int currentBucket = 0;
    private final int numBuckets = 5;
    private final int delta = 4;

    public ScoreboardUpdateTask(ChunkFactionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        currentBucket++;
        if (currentBucket == numBuckets) currentBucket = 0;
        Iterator<Map.Entry<UUID, SimpleScoreboard>> itr = scoreboards.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<UUID, SimpleScoreboard> entry = itr.next();
            Player p = Bukkit.getPlayer(entry.getKey());
            if (p == null) {
                itr.remove();
            } else {
                updateScoreboard(p);
            }
        }
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
            // Factions
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(p);
            String facName = ChatColor.stripColor(fPlayer.getFaction().getTag());
            if (facName.equals("Wilderness")) {
                facName = "None";
            }
            scoreboard.add("&8&m&l" + Strings.repeat("-", Math.max(11, facName.length())), 3);
            scoreboard.add(ChatColor.RED + "\u24BB " + facName, 2);
            scoreboard.add(ChatColor.AQUA + "\u273A " + fPlayer.getPowerRounded() + "/" + fPlayer.getPowerMaxRounded()
                    , 1);
            // Vault
            int money = (int) ChaosBoard.getInstance().getEconomy().getBalance(p);
            scoreboard.add(ChatColor.GREEN + "\u26C3 $" + money, 0);
        } else {
            int topScore = 2 * radius + delta;
            String top = scoreboard.get(topScore, "");
            top = removeArrow(top) + "&7" + DirectionUtils.getDirectionArrow(p);
            scoreboard.add(top, topScore);
        }
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
