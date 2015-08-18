package org.chaospvp.board.api;

import org.bukkit.ChatColor;

public enum ChunkFaction {
    SAFEZONE(ChatColor.GOLD),
    WARZONE(ChatColor.DARK_RED),
    ALLY(ChatColor.DARK_PURPLE),
    ENEMY(ChatColor.RED),
    NEUTRAL(ChatColor.WHITE),
    SELF(ChatColor.GREEN),
    CURRENT(ChatColor.GRAY),
    NONE(ChatColor.DARK_GREEN);

    private String colorString;

    ChunkFaction(ChatColor color) {
        this.colorString = color.toString().replace(ChatColor.COLOR_CHAR, '&');
    }

    @Override
    public String toString() {
        return colorString;
    }
}