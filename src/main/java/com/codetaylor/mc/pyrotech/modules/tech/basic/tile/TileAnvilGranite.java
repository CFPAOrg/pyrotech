package com.codetaylor.mc.pyrotech.modules.tech.basic.tile;

import com.codetaylor.mc.pyrotech.library.Stages;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasicConfig;
import com.codetaylor.mc.pyrotech.modules.tech.basic.block.spi.BlockAnvilBase;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.basic.tile.spi.TileAnvilBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileAnvilGranite
    extends TileAnvilBase {

  @Override
  protected boolean allowAutomation() {

    return ModuleTechBasicConfig.GRANITE_ANVIL.ALLOW_AUTOMATION;
  }

  @Override
  public int getBloomAnvilDamagePerHit() {

    return ModuleTechBasicConfig.GRANITE_ANVIL.BLOOM_DAMAGE_PER_HIT;
  }

  @Override
  protected int getHitsPerDamage() {

    return ModuleTechBasicConfig.GRANITE_ANVIL.HITS_PER_DAMAGE;
  }

  @Override
  protected double getExhaustionCostPerCraftComplete() {

    return ModuleTechBasicConfig.GRANITE_ANVIL.EXHAUSTION_COST_PER_CRAFT_COMPLETE;
  }

  @Override
  protected double getExhaustionCostPerHit() {

    return ModuleTechBasicConfig.GRANITE_ANVIL.EXHAUSTION_COST_PER_HIT;
  }

  @Override
  protected int getHammerHitReduction(ResourceLocation resourceLocation) {

    return ModuleTechBasicConfig.ANVIL_COMMON.getHammerHitReduction(resourceLocation);
  }

  @Override
  protected String[] getPickaxeWhitelist() {

    return ModuleTechBasicConfig.ANVIL_COMMON.PICKAXE_WHITELIST;
  }

  @Override
  protected String[] getPickaxeBlacklist() {

    return ModuleTechBasicConfig.ANVIL_COMMON.PICKAXE_BLACKLIST;
  }

  @Override
  protected int getMinimumHungerToUse() {

    return ModuleTechBasicConfig.GRANITE_ANVIL.MINIMUM_HUNGER_TO_USE;
  }

  @Nonnull
  @Override
  protected BlockAnvilBase getBlock() {

    return ModuleTechBasic.Blocks.ANVIL_GRANITE;
  }

  @Override
  public AnvilRecipe.EnumTier getRecipeTier() {

    return AnvilRecipe.EnumTier.GRANITE;
  }

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleTechBasicConfig.STAGES_ANVIL_GRANITE;
  }
}
