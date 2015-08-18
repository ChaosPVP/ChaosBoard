package org.chaospvp.board.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class ChunkInfo {
    private final World world;
    private final int x;
    private final int z;

    public ChunkInfo(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.world = chunk.getWorld();
    }

    public ChunkInfo(World world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public ChunkInfo(Location location) {
        this.world = location.getWorld();
        this.x = location.getBlockX() >> 4;
        this.z = location.getBlockZ() >> 4;
    }

    @Override
    public String toString() {
        return world.getName() + ":" + x + "," + z;
    }

    public Chunk getChunk() {
        return world.getChunkAt(x, z);
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public ChunkInfo addAndCreate(int dx, int dz) {
        return new ChunkInfo(world, x + dx, z + dz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkInfo chunkInfo = (ChunkInfo) o;

        if (x != chunkInfo.x) return false;
        if (z != chunkInfo.z) return false;
        return !(world != null ? !world.equals(chunkInfo.world) : chunkInfo.world != null);

    }

    @Override
    public int hashCode() {
        int result = world != null ? world.hashCode() : 0;
        result = 31 * result + x;
        result = 31 * result + z;
        return result;
    }
}
