package org.chaospvp.board.scoreboard;

import com.google.common.collect.ImmutableMap;
import org.bukkit.entity.Player;

import java.util.Map;

public class DirectionUtils {
    private static Map<String, String> chars = ImmutableMap.<String, String>builder()
            .put("N", "\u2B06")
            .put("S", "\u2B07")
            .put("E", "\u27A1")
            .put("W", "\u2B05")
            .put("NW", "\u2B09")
            .put("NE", "\u2B08")
            .put("SW", "\u2B0B")
            .put("SE", "\u2B0A")
            .put("X", "")
            .build();

    private static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw() + 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return "X";
        }
    }

    public static String getDirectionArrow(Player player) {
        return chars.get(getCardinalDirection(player));
    }
}
