package xyz.nuark.predench;

import net.minecraft.world.entity.player.Player;

import java.util.OptionalInt;

public interface PredenchSeedUpdater {
    OptionalInt updatePlayerEnchantmentSeed(Player player);
}
