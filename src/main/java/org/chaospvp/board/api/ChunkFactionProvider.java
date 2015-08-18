package org.chaospvp.board.api;

import org.bukkit.entity.Player;

public interface ChunkFactionProvider {
    ChunkFaction getChunkFactionFor(ChunkInfo ci, Player player);
}
