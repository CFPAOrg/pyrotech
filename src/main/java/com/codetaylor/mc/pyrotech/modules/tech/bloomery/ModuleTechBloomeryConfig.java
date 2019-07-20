package com.codetaylor.mc.pyrotech.modules.tech.bloomery;

import com.codetaylor.mc.athenaeum.parser.recipe.item.MalformedRecipeItemException;
import com.codetaylor.mc.athenaeum.parser.recipe.item.ParseResult;
import com.codetaylor.mc.athenaeum.parser.recipe.item.RecipeItemParser;
import com.codetaylor.mc.pyrotech.library.Stages;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Config(modid = ModuleTechBloomery.MOD_ID, name = ModuleTechBloomery.MOD_ID + "/" + "module.tech.Bloomery")
public class ModuleTechBloomeryConfig {

  @Config.Ignore
  public static final List<Tuple<Ingredient, Double>> BLOOMERY_FUEL_MODIFIERS = new ArrayList<>(1);

  @Config.Ignore
  public static final List<Tuple<Ingredient, Double>> WITHER_FORGE_FUEL_MODIFIERS = new ArrayList<>(1);

  @Config.Ignore
  public static Stages STAGES_BLOOM = null;

  @Config.Ignore
  public static Stages STAGES_BLOOMERY = null;

  @Config.Ignore
  public static Stages STAGES_WITHER_FORGE = null;

  // ---------------------------------------------------------------------------
  // - Slag
  // ---------------------------------------------------------------------------

  public static Slag SLAG = new Slag();

  public static class Slag {

    @Config.Comment({
        "Fire damage applied to entities from the molten slag.",
        "Default: " + 3
    })
    @Config.RangeDouble(min = 0)
    public double MOLTEN_WALK_DAMAGE = 3;

    @Config.Comment({
        "Chance that the player will catch fire when harvesting molten slag.",
        "Default: " + 0.125
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double HARVESTING_PLAYER_FIRE_CHANCE = 0.125;

    @Config.Comment({
        "How many seconds the fire will last when a player catches fire",
        "from harvesting molten slag.",
        "Default: " + 1
    })
    @Config.RangeInt(min = 0)
    public int HARVESTING_PLAYER_FIRE_DURATION_SECONDS = 1;
  }

  // ---------------------------------------------------------------------------
  // - Bloomery
  // ---------------------------------------------------------------------------

  public static Bloomery BLOOMERY = new Bloomery();

  public static class Bloomery {

    @Config.Comment({
        "This is the total amount of fuel burn time required to operate the device",
        "at maximum speed. More fuel will increase the speed of the device with",
        "diminishing returns. If the speed cap flag is set to true, inserted ",
        "fuel items that cause the total burn time of all inserted fuel items",
        "to exceed this value will not be inserted.",
        "Default: " + (32000 * 8)
    })
    @Config.RangeInt(min = 1)
    public int FUEL_CAPACITY_BURN_TIME = 32000 * 8;

    @Config.Comment({
        "The total number of fuel items that the device can hold.",
        "Default: " + 16
    })
    @Config.RangeInt(min = 1)
    public int FUEL_CAPACITY_ITEMS = 16;

    @Config.Comment({
        "If true, inserted fuel items that cause the total burn time of all",
        "inserted fuel items to exceed the device's capacity will not be",
        "inserted.",
        "Default: " + false
    })
    public boolean HAS_SPEED_CAP = false;

    @Config.Comment({
        "The bloomery speed is based on the quality (burn time) of the fuel",
        "inserted: y = (scalar)(burnTime/maxBurnTime)^(1/2)",
        "For example, if set to 2, the max speed of the bloomery is 200% and",
        "25% of the total fuel capacity is required for a speed of 100%. If set",
        "to 3, the max speed is 300% and roughly 11% capacity is required for",
        "100% speed."
    })
    @Config.RangeDouble(min = 0)
    public double SPEED_SCALAR = 2;

    @Config.Comment({
        "The maximum amount of ash the device can hold.",
        "Default: " + 16
    })
    @Config.RangeInt(min = 1)
    public int MAX_ASH_CAPACITY = 16;

    @Config.Comment({
        "The chance that one fuel item will convert to one ash upon recipe",
        "completion.",
        "Default: " + 0.35
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double ASH_CONVERSION_CHANCE = 0.35;

    @Config.Comment({
        "The amount of damage done to an entity walking on top of the device",
        "when it is active.",
        "Default: " + 3
    })
    @Config.RangeDouble(min = 0)
    public double ENTITY_WALK_BURN_DAMAGE = 3;

    @Config.Comment({
        "Multiplicative modifier applied to the airflow from a block like the",
        "bellows.",
        "Default: " + 1.0
    })
    public double AIRFLOW_MODIFIER = 1.0;

    @Config.Comment({
        "Percentage of retained airflow lost per tick.",
        "Default: " + 0.005
    })
    public double AIRFLOW_DRAG_MODIFIER = 0.005;

    @Config.Comment({
        "By default, the bloomery uses the burn time of the items inserted as fuel",
        "to calculate its speed. This map allows you to specify per-item modifiers",
        "for bloomery burn time.",
        "",
        "The item string syntax is (domain):(path):(meta|*) where * is used as the",
        "OreDict wildcard value. The modifier is a double in the range [0,+double)"
    })
    public Map<String, Double> SPECIAL_FUEL_BURN_TIME_MODIFIERS = new LinkedHashMap<String, Double>() {{
      this.put("pyrotech:living_tar", 3.0);
      this.put("pyrotech:coal_coke_block", 3.0);
      this.put("minecraft:coal_block", 1.5);
    }};

    public double getSpecialFuelBurnTimeModifier(ItemStack stack) {

      // Check the tuple list first

      for (Tuple<Ingredient, Double> modifier : BLOOMERY_FUEL_MODIFIERS) {
        Ingredient ingredient = modifier.getFirst();

        if (ingredient.apply(stack)) {
          return modifier.getSecond();
        }
      }

      // Next, check config values

      ResourceLocation registryName = stack.getItem().getRegistryName();

      if (registryName != null) {

        for (Map.Entry<String, Double> entry : SPECIAL_FUEL_BURN_TIME_MODIFIERS.entrySet()) {

          try {
            ParseResult parseResult = RecipeItemParser.INSTANCE.parse(entry.getKey());

            if (parseResult.matches(stack, true)) {
              return entry.getValue();
            }

          } catch (MalformedRecipeItemException e) {
            ModuleTechBloomery.LOGGER.error("Error parsing special fuel burn time modifier for item " + entry.getKey(), e);
          }
        }

      }

      return 1;
    }
  }

  // ---------------------------------------------------------------------------
  // - Wither Forge
  // ---------------------------------------------------------------------------

  public static WitherForge WITHER_FORGE = new WitherForge();

  public static class WitherForge {

    @Config.Comment({
        "If false, the device won't play the scary sounds.",
        "Default: " + true
    })
    public boolean ENABLE_SCARY_SOUNDS = true;

    @Config.Comment({
        "The interval at which to play the scary sounds.",
        "interval + [-variance,+variance]",
        "Default: " + (40 * 20)
    })
    public int SCARY_SOUND_INTERVAL_TICKS = 40 * 20;

    @Config.Comment({
        "The random variance to apply to the scary sound interval.",
        "interval + [-variance,+variance]",
        "Default: " + (20 * 20)
    })
    public int SCARY_SOUND_INTERVAL_VARIANCE_TICKS = 20 * 20;

    @Config.Comment({
        "This is the total amount of fuel burn time required to operate the device",
        "at maximum speed. More fuel will increase the speed of the device with",
        "diminishing returns. If the speed cap flag is set to true, inserted ",
        "fuel items that cause the total burn time of all inserted fuel items",
        "to exceed this value will not be inserted.",
        "Default: " + (32000 * 4)
    })
    @Config.RangeInt(min = 1)
    public int FUEL_CAPACITY_BURN_TIME = 32000 * 4;

    @Config.Comment({
        "The total number of fuel items that the device can hold.",
        "Default: " + 16
    })
    @Config.RangeInt(min = 1)
    public int FUEL_CAPACITY_ITEMS = 16;

    @Config.Comment({
        "If true, inserted fuel items that cause the total burn time of all",
        "inserted fuel items to exceed the device's capacity will not be",
        "inserted.",
        "Default: " + false
    })
    public boolean HAS_SPEED_CAP = false;

    @Config.Comment({
        "The wither forge speed is based on the quality (burn time) of the fuel",
        "inserted: y = (scalar)(burnTime/maxBurnTime)^(1/2)",
        "For example, if set to 2, the max speed of the wither forge is 200% and",
        "25% of the total fuel capacity is required for a speed of 100%. If set",
        "to 3, the max speed is 300% and roughly 11% capacity is required for",
        "100% speed."
    })
    @Config.RangeDouble(min = 0)
    public double SPEED_SCALAR = 2;

    @Config.Comment({
        "The maximum amount of ash the device can hold.",
        "Default: " + 16
    })
    @Config.RangeInt(min = 1)
    public int MAX_ASH_CAPACITY = 16;

    @Config.Comment({
        "The chance that one fuel item will convert to one ash upon recipe",
        "completion.",
        "Default: " + 0.35
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double ASH_CONVERSION_CHANCE = 0.35;

    @Config.Comment({
        "The amount of damage done to an entity walking on top of the device",
        "when it is active.",
        "Default: " + 3
    })
    @Config.RangeDouble(min = 0)
    public double ENTITY_WALK_BURN_DAMAGE = 3;

    @Config.Comment({
        "Multiplicative modifier applied to the airflow from a block like the",
        "bellows.",
        "Default: " + 1.0
    })
    public double AIRFLOW_MODIFIER = 1.0;

    @Config.Comment({
        "Percentage of retained airflow lost per tick.",
        "Default: " + 0.005
    })
    public double AIRFLOW_DRAG_MODIFIER = 0.005;

    @Config.Comment({
        "By default, the wither forge uses the burn time of the items inserted as fuel",
        "to calculate its speed. This map allows you to specify per-item modifiers",
        "for wither forge burn time.",
        "",
        "The item string syntax is (domain):(path):(meta|*) where * is used as the",
        "OreDict wildcard value. The modifier is a double in the range [0,+double)"
    })
    public Map<String, Double> SPECIAL_FUEL_BURN_TIME_MODIFIERS = new LinkedHashMap<String, Double>() {{
      this.put("pyrotech:living_tar", 6.0);
      this.put("pyrotech:coal_coke_block", 3.0);
      this.put("minecraft:coal_block", 1.5);
    }};

    public double getSpecialFuelBurnTimeModifier(ItemStack stack) {

      // Check the tuple list first

      for (Tuple<Ingredient, Double> modifier : WITHER_FORGE_FUEL_MODIFIERS) {
        Ingredient ingredient = modifier.getFirst();

        if (ingredient.apply(stack)) {
          return modifier.getSecond();
        }
      }

      // Next, check config values

      ResourceLocation registryName = stack.getItem().getRegistryName();

      if (registryName != null) {

        for (Map.Entry<String, Double> entry : SPECIAL_FUEL_BURN_TIME_MODIFIERS.entrySet()) {

          try {
            ParseResult parseResult = RecipeItemParser.INSTANCE.parse(entry.getKey());

            if (parseResult.matches(stack, true)) {
              return entry.getValue();
            }

          } catch (MalformedRecipeItemException e) {
            ModuleTechBloomery.LOGGER.error("Error parsing special fuel burn time modifier for item " + entry.getKey(), e);
          }
        }

      }

      return 1;
    }

    @Config.Comment({
        "If true, all the bloomery recipes will also be available in this device.",
        "Default: " + true
    })
    public boolean INHERIT_BLOOMERY_RECIPES = true;

  }

  // ---------------------------------------------------------------------------
  // - Bloom
  // ---------------------------------------------------------------------------

  public static Bloom BLOOM = new Bloom();

  public static class Bloom {

    @Config.Comment({
        "Minimum amount of hunger the player needs to use.",
        "Default: " + 3
    })
    @Config.RangeInt(min = 0, max = 20)
    public int MINIMUM_HUNGER_TO_USE = 3;

    @Config.Comment({
        "How much exhaustion to apply per hit.",
        "Default: " + 1
    })
    @Config.RangeDouble(min = 0, max = 40)
    public double EXHAUSTION_COST_PER_HIT = 1;

    @Config.Comment({
        "The number of hammer hits required to extract one item from the bloom's",
        "contents.",
        "This will be further modified using the hammer power modifiers provided",
        "in this section.",
        "Default: " + 16
    })
    @Config.RangeInt(min = 1)
    public int HAMMER_HITS_REQUIRED = 16;

    @Config.Comment({
        "The number of hammer hits required to extract one item from the bloom's contents",
        "when the bloom is placed on this mod's anvil.",
        "This will be further reduced using the hammer hit reduction number provided",
        "for anvil hammers and the hammer power modifiers provided in this section.",
        "Default: " + 8
    })
    @Config.RangeInt(min = 1)
    public int HAMMER_HITS_IN_ANVIL_REQUIRED = 8;

    @Config.Comment({
        "If true, using the hammer on the bloom when the bloom is placed in the",
        "world will have a chance to break the block below the bloom when the bloom",
        "releases a stored item. This chance is based on the hardness of the block.",
        "",
        "Uses this to calculate the chance: 1 - (x/60)^(1/8), where 0 <= x <= 50",
        "This means that obsidian has roughly a 2.25% chance to break",
        "and average blocks with a hardness of 2 have roughly a 30% chance to break.",
        "Default: " + true
    })
    public boolean BREAKS_BLOCKS = true;

    @Config.Comment({
        "The amount of fire damage applied to a player when the bloom is in their",
        "inventory.",
        "Set to zero to disable.",
        "Default: " + 3
    })
    @Config.RangeDouble(min = 0)
    public double FIRE_DAMAGE_PER_SECOND = 3;

    @Config.Comment({
        "The amount of fire damage applied to a player when the bloom is in their",
        "inventory. Set to zero to disable.",
        "Default: " + 3
    })
    @Config.RangeDouble(min = 0)
    public double ENTITY_WALK_DAMAGE = 3;

    @Config.Comment({
        "The chance that hitting a raw bloom will spawn a nearby fire.",
        "Default: " + 0.1
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double FIRE_SPAWN_CHANCE_ON_HIT_RAW = 0.1;

    @Config.Comment({
        "The chance that hitting a bloom in an anvil will spawn a nearby fire.",
        "Default: " + 0.05
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double FIRE_SPAWN_CHANCE_ON_HIT_IN_ANVIL = 0.05;

    @Config.Comment({
        "The chance that a bloom will randomly spawn a nearby fire when the",
        "block randomly ticks.",
        "Default: " + 0.25
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double FIRE_SPAWN_CHANCE_RANDOM = 0.25;

    @Config.Comment({
        "A multiplicative modifier for hammer power per harvest level.",
        "",
        "The index into the array is the harvest level, the value at that index",
        "is the hammer power modifier. The array can be expanded as needed.",
        "If the harvest level of the tool used exceeds the array length, the",
        "last element in the array is used.",
        "",
        "ie. {wood/crude, stone/bone/flint, iron, diamond}",
        "Default: {0.70, 1.00, 2.00, 3.00}"
    })
    public double[] HAMMER_POWER_MODIFIER_PER_HARVEST_LEVEL = {0.70, 1.00, 2.00, 3.00};

    @Config.Comment({
        "A multiplicative modifier for hammer power per efficiency level.",
        "",
        "The index into the array is the efficiency level, the value at that index",
        "is the hammer power modifier. The array can be expanded as needed.",
        "If the efficiency level of the tool used exceeds the array length, the",
        "last element in the array is used.",
        "A modifier of 1.0 will do nothing.",
        "",
        "Default: {1.25, 1.50, 1.75, 2.00, 2.25}"
    })
    public double[] HAMMER_POWER_MODIFIER_PER_EFFICIENCY_LEVEL = {1.25, 1.50, 1.75, 2.00, 2.25};

    @Config.Comment({
        "A multiplicative modifier for hammer power when the player has the",
        "strength effect active. A modifier of 1.0 will do nothing.",
        "Default: " + 1.3
    })
    @Config.RangeDouble(min = 0)
    public double HAMMER_POWER_MODIFIER_FOR_STRENGTH_EFFECT = 1.3;

    @Config.Comment({
        "A multiplicative modifier for hammer power when the player has the",
        "weakness effect active. A modifier of 1.0 will do nothing.",
        "Default: " + 0.7
    })
    @Config.RangeDouble(min = 0)
    public double HAMMER_POWER_MODIFIER_FOR_WEAKNESS_EFFECT = 0.7;

    @Config.Comment({
        "A multiplicative modifier for hammer power when the player has the",
        "mining fatigue effect active. A modifier of 1.0 will do nothing.",
        "Default: " + 0.1
    })
    @Config.RangeDouble(min = 0)
    public double HAMMER_POWER_MODIFIER_FOR_MINING_FATIGUE_EFFECT = 0.1;

    @Config.Comment({
        "A multiplicative modifier for a recipe's failure chance when using a silk touch",
        "enchanted hammer. The smaller the number, the less chance of failure."
    })
    @Config.RangeDouble(min = 0, max = 1)
    public double SILK_TOUCH_FAILURE_MODIFIER = 0;

    @Config.Comment({
        "The chance that a fortune enchanted hammer will not consume the bloom's",
        "integrity when a hammering cycle completes.",
        "",
        "The index into the array is the fortune level, the value at that index",
        "is the chance. The array can be expanded as needed.",
        "If the fortune level of the tool used exceeds the array length, the",
        "last element in the array is used.",
        "",
        "Default: {0.15, 0.30, 0.45}"
    })
    public double[] CHANCE_TO_NOT_CONSUME_BLOOM_INTEGRITY_PER_FORTUNE_LEVEL = {0.15, 0.30, 0.45};
  }

  // ---------------------------------------------------------------------------
  // - Tongs
  // ---------------------------------------------------------------------------

  public static Tongs TONGS = new Tongs();

  public static class Tongs {

    @Config.Comment({
        "The durability of the stone tongs.",
        "Default: " + 4
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int STONE_TONGS_DURABILITY = 4;

    @Config.Comment({
        "The durability of the flint tongs.",
        "Default: " + 4
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int FLINT_TONGS_DURABILITY = 4;

    @Config.Comment({
        "The durability of the bone tongs.",
        "Default: " + 4
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int BONE_TONGS_DURABILITY = 4;

    @Config.Comment({
        "The durability of the iron tongs.",
        "Default: " + 16
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int IRON_TONGS_DURABILITY = 16;

    @Config.Comment({
        "The durability of the gold tongs.",
        "Default: " + 2
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int GOLD_TONGS_DURABILITY = 2;

    @Config.Comment({
        "The durability of the obsidian tongs.",
        "Default: " + 57
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int OBSIDIAN_TONGS_DURABILITY = 57;

    @Config.Comment({
        "The durability of the diamond tongs.",
        "Default: " + 64
    })
    @Config.RangeInt(min = 1, max = Short.MAX_VALUE)
    public int DIAMOND_TONGS_DURABILITY = 64;
  }
}
