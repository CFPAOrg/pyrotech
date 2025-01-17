package com.codetaylor.mc.pyrotech.modules.storage.block.item;

import com.codetaylor.mc.athenaeum.util.StackHelper;
import com.codetaylor.mc.pyrotech.modules.storage.block.spi.BlockBagBase;
import com.codetaylor.mc.pyrotech.modules.storage.tile.spi.TileBagBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

public class ItemBlockBag
    extends ItemBlock {

  private static final int COLOR_BAG_FULL = Color.decode("0xFF0000").getRGB();
  private static final int COLOR_BAG = Color.decode("0x70341e").getRGB();

  private final BlockBagBase blockBag;

  public ItemBlockBag(BlockBagBase blockBag) {

    super(blockBag);
    this.blockBag = blockBag;
    this.setMaxStackSize(1);
  }

  public boolean isOpen(ItemStack itemStack) {

    return (itemStack.getMetadata() == 1);
  }

  public boolean isItemValidForInsertion(ItemStack itemStack) {

    return this.blockBag.isItemValidForInsertion(itemStack);
  }

  public boolean allowAutoPickupMainhand() {

    return this.blockBag.allowAutoPickupMainhand();
  }

  public boolean allowAutoPickupOffhand() {

    return this.blockBag.allowAutoPickupOffhand();
  }

  public boolean allowAutoPickupHotbar() {

    return this.blockBag.allowAutoPickupHotbar();
  }

  public boolean allowAutoPickupInventory() {

    return this.blockBag.allowAutoPickupInventory();
  }

  private int getItemCapacity() {

    return this.blockBag.getItemCapacity();
  }

  @Override
  public double getDurabilityForDisplay(ItemStack stack) {

    return 1 - this.getCount(stack) / (double) this.getItemCapacity();
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {

    return this.getCount(stack) > 0;
  }

  @Override
  public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack) {

    if (this.getCount(stack) == this.getItemCapacity()) {
      return COLOR_BAG_FULL;
    }

    return COLOR_BAG;
  }

  public int getCount(ItemStack itemStack) {

    NBTTagCompound tagCompound = itemStack.getTagCompound();

    if (tagCompound == null) {
      return -1;
    }

    return tagCompound.getInteger("count");
  }

  public void updateCount(ItemStack itemStack) {

    IItemHandler handler = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

    if (handler instanceof TileBagBase.StackHandler) {
      NBTTagCompound tag = StackHelper.getTagSafe(itemStack);
      int count = ((TileBagBase.StackHandler) handler).getTotalItemCount();
      tag.setInteger("count", count);
    }
  }

  @ParametersAreNonnullByDefault
  @Override
  public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {

    super.addInformation(stack, world, tooltip, flag);
    int count = this.getCount(stack);

    if (count == -1) {
      tooltip.add(I18n.translateToLocalFormatted("gui.pyrotech.tooltip.item.capacity.empty", this.getItemCapacity()));

    } else {
      tooltip.add(I18n.translateToLocalFormatted("gui.pyrotech.tooltip.item.capacity", count, this.getItemCapacity()));
    }
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {

    return new CapabilityProvider(this.getItemCapacity(), this.blockBag::isItemValidForInsertion);
  }

  @Nonnull
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ) {

    ItemStack heldItem = player.getHeldItem(hand);

    if (player.isSneaking()) {

      // try inventory
      if (this.tryTransferItems(world, pos, facing, heldItem)) {

        if (!world.isRemote) {
          this.updateCount(heldItem);
        }
        return EnumActionResult.SUCCESS;
      }

      // spill contents
      if (this.isOpen(heldItem)
          && this.trySpillContents(world, pos, facing, heldItem)) {

        if (!world.isRemote) {
          this.updateCount(heldItem);
        }
        return EnumActionResult.SUCCESS;
      }
    }

    ItemStack copy = heldItem.copy();
    EnumActionResult result = super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

    if (result == EnumActionResult.SUCCESS) {
      TileEntity tileEntity = world.getTileEntity(pos.offset(facing));

      if (tileEntity instanceof TileBagBase) {
        IItemHandler itemHandler = copy.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        TileBagBase.StackHandler tileHandler = ((TileBagBase) tileEntity).getStackHandler();

        if (itemHandler != null
            && tileHandler != null) {
          for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack stackInSlot = itemHandler.getStackInSlot(i);
            tileHandler.setStackInSlot(i, stackInSlot);
          }
        }
      }
    }

    return result;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {

    RayTraceResult rayTraceResult = this.rayTrace(world, player, false);
    ItemStack heldItem = player.getHeldItem(hand);

    if (player.isSneaking()) {

      //noinspection ConstantConditions
      if (rayTraceResult == null
          || rayTraceResult.typeOfHit == RayTraceResult.Type.MISS) {
        // cycle open closed
        heldItem.setItemDamage(heldItem.getItemDamage() == 0 ? 1 : 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, heldItem);
      }
    }

    return super.onItemRightClick(world, player, hand);
  }

  private boolean trySpillContents(World world, BlockPos pos, EnumFacing facing, ItemStack itemStack) {

    if (world.isAirBlock(pos.offset(facing))) {
      IItemHandler handler = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

      if (handler instanceof TileBagBase.StackHandler) {

        if (!world.isRemote) {

          for (int i = 0; i < handler.getSlots(); i++) {
            StackHelper.spawnStackHandlerContentsOnTop(world, (TileBagBase.StackHandler) handler, pos.offset(facing), 0);
          }
        }
        return true;
      }
    }

    return false;
  }

  private boolean tryTransferItems(World world, BlockPos pos, EnumFacing facing, ItemStack itemStack) {

    IItemHandler itemHandler = itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

    if (!(itemHandler instanceof TileBagBase.StackHandler)) {
      return false;
    }

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity == null) {
      return false;
    }

    IItemHandler otherHandler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing);

    if (otherHandler == null) {
      return false;
    }

    if (world.isRemote) {
      return true;
    }

    // Go through all the items in the bag
    for (int i = 0; i < itemHandler.getSlots(); i++) {
      ItemStack stackInSlot = itemHandler.getStackInSlot(i);

      if (stackInSlot.isEmpty()) {
        continue;
      }

      ItemStack remainingItems = stackInSlot.copy();

      // and try to put each stack into the targeted handler
      for (int j = 0; j < otherHandler.getSlots(); j++) {
        remainingItems = otherHandler.insertItem(j, remainingItems, false);

        if (remainingItems.isEmpty()) {
          break;
        }
      }

      if (stackInSlot.getCount() != remainingItems.getCount()) {
        ((TileBagBase.StackHandler) itemHandler).setStackInSlot(i, remainingItems);
      }
    }

    return true;
  }

  public static class CapabilityProvider
      implements ICapabilityProvider,
      INBTSerializable<NBTTagCompound> {

    private TileBagBase.StackHandler stackHandler;

    /* package */ CapabilityProvider(int itemCapacity, Predicate<ItemStack> filter) {

      this.stackHandler = new TileBagBase.StackHandler(itemCapacity, filter);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {

      return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
        //noinspection unchecked
        return (T) this.stackHandler;
      }

      return null;
    }

    @Override
    public NBTTagCompound serializeNBT() {

      return this.stackHandler.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

      this.stackHandler.deserializeNBT(nbt);
    }
  }
}
