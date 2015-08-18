package org.chaospvp.board.impl;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Relation;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.entity.Player;
import org.chaospvp.board.api.ChunkFaction;
import org.chaospvp.board.api.ChunkFactionProvider;
import org.chaospvp.board.api.ChunkInfo;

public class FactionsUUIDProvider implements ChunkFactionProvider {
    private WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();

    @Override
    public ChunkFaction getChunkFactionFor(ChunkInfo ci, Player player) {
        if (ci.equals(new ChunkInfo(player.getLocation()))) {
            return ChunkFaction.CURRENT;
        }
        Faction faction = Board.getInstance().getFactionAt(fromChunkInfo(ci));
        Faction playerFac = FPlayers.getInstance().getByPlayer(player).getFaction();
        if (faction.isNone()) {
            return ChunkFaction.NONE;
        } else if (faction.isSafeZone()) {
            return ChunkFaction.SAFEZONE;
        } else if (faction.isWarZone()) {
            if (isPvpAllowedWg(ci)) {
                return ChunkFaction.WARZONE;
            } else {
                return ChunkFaction.SAFEZONE;
            }
        } else if (faction.equals(playerFac)) {
            return ChunkFaction.SELF;
        } else {
            Relation relation = faction.getRelationTo(playerFac);
            if (relation.isAlly()) {
                return ChunkFaction.ALLY;
            } else if (relation.isEnemy()) {
                return ChunkFaction.ENEMY;
            } else if (relation.isNeutral()) {
                return ChunkFaction.NEUTRAL;
            } else {
                return ChunkFaction.NONE;
            }
        }
    }

    private FLocation fromChunkInfo(ChunkInfo ci) {
        return new FLocation(ci.getWorld().getName(), ci.getX(), ci.getZ());
    }

    private boolean isPvpAllowedWg(ChunkInfo ci) {
        try {
            RegionManager regionManager = worldGuard.getRegionManager(ci.getWorld());
            ApplicableRegionSet set = regionManager.getApplicableRegions(chunkToVector(ci));
            // non-deprecated method is unreliable
            return set.allows(DefaultFlag.PVP);
        } catch (Throwable t) {
            return true;
        }
    }

    private Vector chunkToVector(ChunkInfo ci) {
        return new Vector(ci.getX() * 16 + 8, 72, ci.getZ() * 16 + 8);
    }
}
