package xyz.nuark.predench.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.nuark.predench.EnchantmentData;
import xyz.nuark.predench.Predench;
import xyz.nuark.predench.PredenchEnchantmentDataExtractor;
import xyz.nuark.predench.PredenchSeedUpdater;

import java.util.Optional;
import java.util.OptionalInt;

@Mixin(EnchantmentTableBlock.class)
public class MixinEnchantmentTableBlock {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        Predench.LOGGER.info("EnchantmentTableBlock use finished, injecting our code...");
        if (!level.isClientSide) {
            cir.cancel();
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            Predench.LOGGER.info("Main hand: " + main);
            Predench.LOGGER.info("Offhand: " + off);

            OptionalInt optionalInt = player.openMenu(blockState.getMenuProvider(level, blockPos));
            if (optionalInt.isPresent() && player.containerMenu.getType() == MenuType.ENCHANTMENT) {
                if (main.is(Items.SPONGE) && off.is(Items.WATER_BUCKET)) {
                    if (main.getCount() > 1) {
                        player.displayClientMessage(new TextComponent("You should have exactly one sponge in your hand."), true);
                    } else {
                        updatePlayerEnchantmentSeed(player);
                    }
                } else if (main.is(Predench.STRANGE_WRITING_BOOK_ITEM.get()) && off.getItem().isEnchantable(off)) {
                    writeBookWithEnchantmentsData(level, blockPos, player, off);
                }
            }

            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    private void updatePlayerEnchantmentSeed(Player player) {
        if (player.experienceLevel < 3) {
            player.displayClientMessage(new TextComponent("You need at least 3 levels to use this."), true);
        } else {
            OptionalInt newSeed = ((PredenchSeedUpdater) player.containerMenu).updatePlayerEnchantmentSeed(player);
            if (newSeed.isPresent()) {
                player.displayClientMessage(new TextComponent("New enchantment seed: " + newSeed.getAsInt()), true);
                player.giveExperienceLevels(-3);
                player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.WET_SPONGE));
                player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.BUCKET));
            } else {
                player.displayClientMessage(new TextComponent("Failed to update enchantment seed."), true);
            }
        }
        player.closeContainer();
    }

    private void writeBookWithEnchantmentsData(Level level, BlockPos blockPos, Player player, ItemStack off) {
        if (player.experienceLevel < 3) {
            player.displayClientMessage(new TextComponent("You need at least 3 levels to use this."), true);
        } else {
            ItemStack enchantmentInfoBook = new ItemStack(Items.WRITTEN_BOOK);
            Optional<EnchantmentData> data = ((PredenchEnchantmentDataExtractor) player.containerMenu).getEnchantmentData(level, blockPos, off);
            if (data.isPresent()) {
                CompoundTag nbt = enchantmentInfoBook.getOrCreateTag();
                nbt.putString("title", "Predench report");
                nbt.putString("author", "nuark");
                nbt.put("pages", data.get().convertToBookPages());
                player.setItemSlot(EquipmentSlot.MAINHAND, enchantmentInfoBook);
                player.giveExperienceLevels(-10);
            } else {
                player.displayClientMessage(new TextComponent("Couldn't retrieve enchantment data."), true);
            }
        }
        player.closeContainer();
    }
}
