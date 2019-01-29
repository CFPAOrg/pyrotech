package com.codetaylor.mc.pyrotech.modules.bloomery.plugin.jei;

import com.codetaylor.mc.pyrotech.modules.bloomery.ModuleBloomery;
import com.codetaylor.mc.pyrotech.modules.bloomery.recipe.BloomeryRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PluginJEI
    implements IModPlugin {

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {

    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    registry.addRecipeCategories(
        new JEIRecipeCategoryBloomery(guiHelper)
    );
  }

  @Override
  public void register(IModRegistry registry) {

    // --- Blacklist Ingredients

    IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
    blacklist.addIngredientToBlacklist(new ItemStack(ModuleBloomery.Items.TONGS_STONE_FULL));
    blacklist.addIngredientToBlacklist(new ItemStack(ModuleBloomery.Items.TONGS_FLINT_FULL));
    blacklist.addIngredientToBlacklist(new ItemStack(ModuleBloomery.Items.TONGS_BONE_FULL));
    blacklist.addIngredientToBlacklist(new ItemStack(ModuleBloomery.Items.TONGS_IRON_FULL));
    blacklist.addIngredientToBlacklist(new ItemStack(ModuleBloomery.Items.TONGS_DIAMOND_FULL));

    // --- Bloomery
    {
      registry.addRecipeCatalyst(new ItemStack(ModuleBloomery.Blocks.BLOOMERY), JEIRecipeCategoryBloomery.UID);
      registry.handleRecipes(BloomeryRecipe.class, JEIRecipeWrapperBloomery::new, JEIRecipeCategoryBloomery.UID);
      List<BloomeryRecipe> recipeList = new ArrayList<>(ModuleBloomery.Registries.BLOOMERY_RECIPE.getValuesCollection());
      registry.addRecipes(recipeList, JEIRecipeCategoryBloomery.UID);
    }
  }

}