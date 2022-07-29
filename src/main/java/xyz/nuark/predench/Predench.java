package xyz.nuark.predench;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(Predench.MOD_ID)
public class Predench {
    public static final String MOD_ID = "predench";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Really necessary to create item group for two items :)
    public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab("predench") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(VITRIOLIC_FEATHER_ITEM.get());
        }
    };

    // Only two items, no need to create holder class
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Predench.MOD_ID);
    public static final RegistryObject<Item> VITRIOLIC_FEATHER_ITEM = ITEMS.register("vitriolic_feather", () -> new Item(new Item.Properties().tab(Predench.ITEM_GROUP).stacksTo(16).fireResistant().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> STRANGE_WRITING_BOOK_ITEM = ITEMS.register("strange_writing_book", () -> new Item(new Item.Properties().tab(Predench.ITEM_GROUP).stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public Predench() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
