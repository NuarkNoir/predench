package xyz.nuark.predench.mixin;

import com.google.gson.Gson;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.nuark.predench.EnchantmentData;
import xyz.nuark.predench.Predench;
import xyz.nuark.predench.PredenchEnchantmentDataExtractor;
import xyz.nuark.predench.PredenchSeedUpdater;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

@Mixin(EnchantmentMenu.class)
public abstract class MixinEnchantmentMenu implements PredenchSeedUpdater, PredenchEnchantmentDataExtractor {
    @Shadow protected abstract List<EnchantmentInstance> getEnchantmentList(ItemStack p_39472_, int p_39473_, int p_39474_);

    public OptionalInt updatePlayerEnchantmentSeed(Player player) {
        try {
            player.onEnchantmentPerformed(null, 0);
            int seed = player.getEnchantmentSeed();
            enchantmentSeed.set(seed);
            return OptionalInt.of(seed);
        } catch (Exception e) {
            Predench.LOGGER.error("Failed to update enchantment seed for player " + player.getName().getString(), e);
        }
        return OptionalInt.empty();
    }

    @Override
    public Optional<EnchantmentData> getEnchantmentData(Level level, BlockPos tablePos, ItemStack itemstack) {
        try {
            int[] costs = new int[3];
            float j = 0;

            for(BlockPos blockpos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
                if (EnchantmentTableBlock.isValidBookShelf(level, tablePos, blockpos)) {
                    j += level.getBlockState(tablePos.offset(blockpos)).getEnchantPowerBonus(level, tablePos.offset(blockpos));
                }
            }

            Random random = new Random();
            random.setSeed(this.enchantmentSeed.get());

            for(int k = 0; k < 3; ++k) {
                costs[k] = EnchantmentHelper.getEnchantmentCost(random, k, (int)j, itemstack);
                if (costs[k] < k + 1) {
                    costs[k] = 0;
                }
                costs[k] = net.minecraftforge.event.ForgeEventFactory.onEnchantmentLevelSet(level, tablePos, k, (int)j, itemstack, costs[k]);
            }

            EnchantmentData enchantmentData = new EnchantmentData();

            for(int l = 0; l < 3; ++l) {
                if (costs[l] > 0) {
                    List<EnchantmentInstance> list = this.getEnchantmentList(itemstack, l, costs[l]);
                    if (list != null && !list.isEmpty()) {
                        for (EnchantmentInstance enchantmentInstance : list) {
                            enchantmentData.addToTier(l, new EnchantmentData.Enchantment(
                                    enchantmentInstance.enchantment.getFullname(enchantmentInstance.level).getString(),
                                    enchantmentInstance.level,
                                    costs[l]
                            ));
                        }
                    }
                }
            }

            Predench.LOGGER.info("Enchantment data: " + new Gson().toJson(enchantmentData));

            return Optional.of(enchantmentData);
        } catch (Exception e) {
            Predench.LOGGER.error("Failed to get enchantment data for itemstack " + itemstack.toString(), e);
        }

        return Optional.empty();
    }

    @Final
    @Shadow
    private final DataSlot enchantmentSeed = DataSlot.standalone();
}

