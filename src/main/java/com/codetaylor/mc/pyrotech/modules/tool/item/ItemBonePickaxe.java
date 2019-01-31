package com.codetaylor.mc.pyrotech.modules.tool.item;

import com.codetaylor.mc.athenaeum.reference.EnumMaterial;
import com.codetaylor.mc.pyrotech.modules.tool.ModuleToolConfig;
import net.minecraft.item.ItemPickaxe;

public class ItemBonePickaxe
    extends ItemPickaxe {

  public static final String NAME = "bone_pickaxe";

  public ItemBonePickaxe() {

    super(EnumMaterial.BONE.getToolMaterial());

    Integer maxDamage = ModuleToolConfig.DURABILITY.get("bone");

    if (maxDamage != null) {
      this.setMaxDamage(maxDamage);
    }
  }

}