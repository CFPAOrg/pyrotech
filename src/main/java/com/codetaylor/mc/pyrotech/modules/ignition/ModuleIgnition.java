package com.codetaylor.mc.pyrotech.modules.ignition;

import com.codetaylor.mc.athenaeum.module.ModuleBase;
import com.codetaylor.mc.athenaeum.network.IPacketService;
import com.codetaylor.mc.athenaeum.network.tile.ITileDataService;
import com.codetaylor.mc.athenaeum.registry.Registry;
import com.codetaylor.mc.pyrotech.ModPyrotech;
import com.codetaylor.mc.pyrotech.modules.ignition.block.BlockIgniter;
import com.codetaylor.mc.pyrotech.modules.ignition.block.BlockTorchFiber;
import com.codetaylor.mc.pyrotech.modules.ignition.block.BlockTorchStone;
import com.codetaylor.mc.pyrotech.modules.ignition.init.BlockInitializer;
import com.codetaylor.mc.pyrotech.modules.ignition.init.ItemInitializer;
import com.codetaylor.mc.pyrotech.modules.ignition.item.ItemBowDrill;
import com.codetaylor.mc.pyrotech.modules.ignition.item.ItemFlintAndTinder;
import com.codetaylor.mc.pyrotech.modules.ignition.item.ItemMatchstick;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModuleIgnition
    extends ModuleBase {

  public static final String MODULE_ID = "module.ignition";
  public static final String MOD_ID = ModPyrotech.MOD_ID;
  public static final CreativeTabs CREATIVE_TAB = ModPyrotech.CREATIVE_TAB;

  public static final Logger LOGGER = LogManager.getLogger(MOD_ID + "." + ModuleIgnition.class.getSimpleName());

  public static IPacketService PACKET_SERVICE;
  public static ITileDataService TILE_DATA_SERVICE;

  public ModuleIgnition() {

    super(0, MOD_ID);

    this.setRegistry(new Registry(MOD_ID, CREATIVE_TAB));
    this.enableAutoRegistry();

    PACKET_SERVICE = this.enableNetwork();
    TILE_DATA_SERVICE = this.enableNetworkTileDataService(PACKET_SERVICE);

    MinecraftForge.EVENT_BUS.register(this);
  }

  @Override
  public void onRegister(Registry registry) {

    BlockInitializer.onRegister(registry);
    ItemInitializer.onRegister(registry);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void onClientRegister(Registry registry) {

    BlockInitializer.onClientRegister(registry);
    ItemInitializer.onClientRegister(registry);
  }

  @GameRegistry.ObjectHolder(ModuleIgnition.MOD_ID)
  public static class Blocks {

    @GameRegistry.ObjectHolder(BlockIgniter.NAME)
    public static final BlockIgniter IGNITER;

    @GameRegistry.ObjectHolder(BlockTorchFiber.NAME)
    public static final BlockTorchFiber TORCH_FIBER;

    @GameRegistry.ObjectHolder(BlockTorchStone.NAME)
    public static final BlockTorchStone TORCH_STONE;

    static {
      IGNITER = null;
      TORCH_FIBER = null;
      TORCH_STONE = null;
    }
  }

  @GameRegistry.ObjectHolder(ModuleIgnition.MOD_ID)
  public static class Items {

    @GameRegistry.ObjectHolder(ItemBowDrill.NAME)
    public static final ItemBowDrill BOW_DRILL;

    @GameRegistry.ObjectHolder(ItemFlintAndTinder.NAME)
    public static final ItemFlintAndTinder FLINT_AND_TINDER;

    @GameRegistry.ObjectHolder(ItemMatchstick.NAME)
    public static final ItemMatchstick MATCHSTICK;

    static {
      BOW_DRILL = null;
      FLINT_AND_TINDER = null;
      MATCHSTICK = null;
    }
  }

}
