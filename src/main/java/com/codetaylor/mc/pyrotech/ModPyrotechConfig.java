package com.codetaylor.mc.pyrotech;

import com.codetaylor.mc.pyrotech.modules.bloomery.ModuleBloomery;
import com.codetaylor.mc.pyrotech.modules.bucket.ModuleBucket;
import com.codetaylor.mc.pyrotech.modules.plugin.dropt.ModulePluginDropt;
import com.codetaylor.mc.pyrotech.modules.storage.ModuleStorage;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.worldgen.ModuleWorldGen;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BooleanSupplier;

@Config(modid = ModPyrotech.MOD_ID, name = ModPyrotech.MOD_ID + "/.modules")
public class ModPyrotechConfig {

  public static Map<String, Boolean> MODULES = new TreeMap<>();

  static {
    MODULES.put(ModuleBloomery.MODULE_ID, true);
    MODULES.put(ModuleBucket.MODULE_ID, true);
    MODULES.put(ModuleStorage.MODULE_ID, true);
    MODULES.put(ModuleTechMachine.MODULE_ID, true);
    MODULES.put(ModuleWorldGen.MODULE_ID, true);

    MODULES.put(ModulePluginDropt.MODULE_ID, true);
  }

  @SuppressWarnings("unused")
  public static class ConditionConfig
      implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {

      JsonArray modules = JsonUtils.getJsonArray(json, "modules");

      for (JsonElement element : modules) {
        String module = element.getAsString();

        if (!MODULES.containsKey(module)) {
          throw new JsonSyntaxException("Unknown module id: [" + module + "]");
        }

        if (!MODULES.get(module)) {
          return () -> false;
        }
      }

      return () -> true;
    }
  }
}