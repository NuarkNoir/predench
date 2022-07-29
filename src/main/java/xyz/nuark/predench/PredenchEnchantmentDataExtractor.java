package xyz.nuark.predench;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface PredenchEnchantmentDataExtractor {
    Optional<EnchantmentData> getEnchantmentData(Level level, BlockPos tablePos, ItemStack itemstack);
}
