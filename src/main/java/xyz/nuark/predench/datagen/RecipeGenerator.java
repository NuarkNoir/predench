package xyz.nuark.predench.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import xyz.nuark.predench.Predench;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider {
    public RecipeGenerator(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Predench.VITRIOLIC_FEATHER_ITEM.get())
                .define('C', Items.COPPER_BLOCK)
                .define('F', Tags.Items.FEATHERS)
                .pattern(" C ")
                .pattern("CFC")
                .pattern(" C ")
                .unlockedBy("has_copper", has(Tags.Items.INGOTS_COPPER))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(Predench.STRANGE_WRITING_BOOK_ITEM.get())
                .requires(Predench.VITRIOLIC_FEATHER_ITEM.get())
                .requires(Items.BOOK)
                .requires(Items.GLOW_INK_SAC)
                .unlockedBy("has_vitriolic_feather", has(Predench.VITRIOLIC_FEATHER_ITEM.get()))
                .save(consumer);
    }
}
