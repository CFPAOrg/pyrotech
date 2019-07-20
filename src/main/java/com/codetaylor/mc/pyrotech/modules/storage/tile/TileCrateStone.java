package com.codetaylor.mc.pyrotech.modules.storage.tile;

import com.codetaylor.mc.pyrotech.library.Stages;
import com.codetaylor.mc.pyrotech.modules.storage.ModuleStorageConfig;

import javax.annotation.Nullable;

public class TileCrateStone
    extends TileCrate {

  @Override
  protected int getMaxStacks() {

    return ModuleStorageConfig.DURABLE_CRATE.MAX_STACKS;
  }

  @Override
  protected boolean allowAutomation() {

    return ModuleStorageConfig.DURABLE_CRATE.ALLOW_AUTOMATION;
  }

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleStorageConfig.STAGES_DURABLE_CRATE;
  }
}
