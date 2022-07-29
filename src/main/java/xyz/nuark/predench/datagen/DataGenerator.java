package xyz.nuark.predench.datagen;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import xyz.nuark.predench.Predench;

@Mod.EventBusSubscriber(modid = Predench.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        if (event.includeServer()) {
            Predench.LOGGER.info("Generating recipes data");
            event.getGenerator().addProvider(new RecipeGenerator(event.getGenerator()));
        }
    }
}
