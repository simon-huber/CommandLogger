/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ibhh.CommandLogger;

import java.util.HashMap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 *
 * @author Simon
 */
public class EntityManager {

    public static Chunk[] getChunks(World world, Location loc1, Location loc2) {
        Chunk[] chunks = new Chunk[1000];
        int laenge = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());
        int breite = Math.abs(loc1.getBlockX() - loc2.getBlockX());
        for (int i = 0; i < laenge; i = i + 16) {
            for (int i2 = 0; i2 < breite; i2 = i2 + 16) {
                Location aktloc;
                if (loc1.getZ() < loc2.getBlockZ()) {
                    if (loc1.getBlockX() < loc2.getBlockX()) {
                        aktloc = new Location(world, loc1.getBlockX() + i2, loc1.getBlockY(), loc1.getBlockZ() + i);
                    } else {
                        aktloc = new Location(world, loc1.getBlockX() - i2, loc1.getBlockY(), loc1.getBlockZ() + i);
                    }
                } else {
                    if (loc1.getBlockX() < loc2.getBlockX()) {
                        aktloc = new Location(world, loc1.getBlockX() + i2, loc1.getBlockY(), loc1.getBlockZ() - i);
                    } else {
                        aktloc = new Location(world, loc1.getBlockX() - i2, loc1.getBlockY(), loc1.getBlockZ() - i);
                    }
                }
                for (int chunii = 0; chunii < chunks.length; chunii++) {
                    if (chunks[chunii] == null) {
                        chunks[chunii] = aktloc.getChunk();
                        break;
                    } else if (chunks[chunii] == aktloc.getChunk()) {
                        break;
                    }
                }
            }
        }
        int anzahl = 0;
        for (Chunk ch : chunks) {
            if (ch != null) {
                anzahl++;
            }
        }
        Chunk[] chunksneu = new Chunk[anzahl];
        for (int anz = 0; anz < chunks.length; anz++) {
            if (chunks[anz] != null) {
                for (int chunii = 0; chunii < chunks.length; chunii++) {
                    if (chunksneu[chunii] == null) {
                        chunksneu[chunii] = chunks[anz];
                        break;
                    }
                }
            }
        }
        return chunksneu;
    }

    public static int maxMobs(HashMap<EntityType, Integer> list, Chunk[] chunks) {
        int gekilltemobs = 0;
        HashMap<EntityType, Integer> verbleibend = list;
        for (Chunk ch : chunks) {
            for (Entity ent : ch.getEntities()) {
                if (ent.getType() != EntityType.PLAYER) {
                    if (list.containsKey(ent.getType())) {
                        Entity entity = (Entity) ent;
                        if (verbleibend.get(ent.getType()) <= 0) {
                            entity.remove();
                        } else {
                            verbleibend.put(ent.getType(), verbleibend.get(ent.getType()) - 1);
                        }
                    }
                }
            }
        }
        return gekilltemobs;
    }
}
