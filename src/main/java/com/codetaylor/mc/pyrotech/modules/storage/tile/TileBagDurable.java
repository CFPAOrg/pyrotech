package com.codetaylor.mc.pyrotech.modules.storage.tile;

import com.codetaylor.mc.pyrotech.library.Stages;
import com.codetaylor.mc.pyrotech.modules.storage.ModuleStorageConfig;
import com.codetaylor.mc.pyrotech.modules.storage.tile.spi.TileBagBase;

import javax.annotation.Nullable;

public class TileBagDurable
    extends TileBagBase {

  @Override
  public int getItemCapacity() {

    return ModuleStorageConfig.DURABLE_ROCK_BAG.MAX_ITEM_CAPACITY;
  }

  @Override
  public boolean allowAutomation() {

    return ModuleStorageConfig.DURABLE_ROCK_BAG.ALLOW_AUTOMATION;
  }

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleStorageConfig.STAGES_DURABLE_ROCK_BAG;
  }
}
