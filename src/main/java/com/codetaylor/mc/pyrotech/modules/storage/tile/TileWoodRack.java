package com.codetaylor.mc.pyrotech.modules.storage.tile;

import com.codetaylor.mc.athenaeum.inventory.ObservableStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.data.TileDataItemStackHandler;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileData;
import com.codetaylor.mc.athenaeum.network.tile.spi.ITileDataItemStackHandler;
import com.codetaylor.mc.athenaeum.util.FacingHelper;
import com.codetaylor.mc.athenaeum.util.OreDictHelper;
import com.codetaylor.mc.athenaeum.util.Properties;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import com.codetaylor.mc.pyrotech.interaction.api.Transform;
import com.codetaylor.mc.pyrotech.interaction.spi.IInteraction;
import com.codetaylor.mc.pyrotech.interaction.spi.ITileInteractable;
import com.codetaylor.mc.pyrotech.interaction.spi.InteractionItemStack;
import com.codetaylor.mc.pyrotech.library.Stages;
import com.codetaylor.mc.pyrotech.library.spi.tile.TileNetBase;
import com.codetaylor.mc.pyrotech.library.util.Util;
import com.codetaylor.mc.pyrotech.modules.storage.ModuleStorage;
import com.codetaylor.mc.pyrotech.modules.storage.ModuleStorageConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileWoodRack
    extends TileNetBase
    implements ITileInteractable {

  private StackHandler stackHandler;
  private boolean settlingStacks;

  private Interaction[] interactions;

  public TileWoodRack() {

    // --- Init ---

    super(ModuleStorage.TILE_DATA_SERVICE);

    this.stackHandler = new StackHandler(9);
    this.stackHandler.addObserver((handler, slot) -> {
      this.settleStacks();
      this.markDirty();
    });

    // --- Network ---

    this.registerTileDataForNetwork(new ITileData[]{
        new TileDataItemStackHandler<>(this.stackHandler)
    });

    // --- Interactions ---

    this.interactions = new Interaction[]{
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 0),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 1),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 2),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 3),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 4),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 5),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 6),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 7),
        new Interaction(new ItemStackHandler[]{this.stackHandler}, 8)
    };

    for (int i = 0; i < 6; i++) {
      this.interactions[i].setAbove(this.interactions[i + 3]);
    }

    for (int i = 3; i < 9; i++) {
      this.interactions[i].setBelow(this.interactions[i - 3]);
    }
  }

  public void dropContents() {

    StackHelper.spawnStackHandlerContentsOnTop(this.world, this.stackHandler, this.pos);
  }

  // ---------------------------------------------------------------------------
  // - Accessors
  // ---------------------------------------------------------------------------

  public StackHandler getStackHandler() {

    return this.stackHandler;
  }

  // ---------------------------------------------------------------------------
  // - Capabilities
  // ---------------------------------------------------------------------------

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {

    return this.allowAutomation()
        && (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
  }

  @Nullable
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {

    if (this.allowAutomation()
        && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {

      //noinspection unchecked
      return (T) this.stackHandler;
    }

    return null;
  }

  protected boolean allowAutomation() {

    return ModuleStorageConfig.WOOD_RACK.ALLOW_AUTOMATION;
  }

  // ---------------------------------------------------------------------------
  // - Rendering
  // ---------------------------------------------------------------------------

  @Override
  public boolean shouldRenderInPass(int pass) {

    return (pass == 0) || (pass == 1);
  }

  // ---------------------------------------------------------------------------
  // - Custom Logic
  // ---------------------------------------------------------------------------

  private void settleStacks() {

    if (this.settlingStacks
        || this.world.isRemote) {
      return;
    }

    this.settlingStacks = true;

    for (int i = 0; i < 6; i++) {
      int slotLimitAbove = this.stackHandler.getSlotLimit(i + 3);
      ItemStack stackAbove = this.stackHandler.extractItem(i + 3, slotLimitAbove, false);
      stackAbove = this.stackHandler.insertItem(i, stackAbove, false);
      this.stackHandler.insertItem(i + 3, stackAbove, false);
    }

    this.settlingStacks = false;
  }

  // ---------------------------------------------------------------------------
  // - Serialization
  // ---------------------------------------------------------------------------

  @Override
  public void readFromNBT(NBTTagCompound compound) {

    super.readFromNBT(compound);

    this.stackHandler.deserializeNBT(compound.getCompoundTag("stackHandler"));
  }

  @Nonnull
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {

    super.writeToNBT(compound);

    compound.setTag("stackHandler", this.stackHandler.serializeNBT());

    return compound;
  }

  // ---------------------------------------------------------------------------
  // - Interactions
  // ---------------------------------------------------------------------------

  @Nullable
  @Override
  public Stages getStages() {

    return ModuleStorageConfig.STAGES_WOOD_RACK;
  }

  @Override
  public IInteraction[] getInteractions() {

    return this.interactions;
  }

  @Override
  public EnumFacing getTileFacing(World world, BlockPos pos, IBlockState blockState) {

    if (blockState.getBlock() == ModuleStorage.Blocks.WOOD_RACK) {
      return blockState.getValue(Properties.FACING_HORIZONTAL);
    }

    return ITileInteractable.super.getTileFacing(world, pos, blockState);
  }

  private static class Interaction
      extends InteractionItemStack {

    private static final Vec3d TEXT_OFFSET_NORTH = new Vec3d(0, 0, -0.5);
    private static final Vec3d TEXT_OFFSET_SOUTH = new Vec3d(0, 0, 0.5);

    private InteractionItemStack below;
    private InteractionItemStack above;

    public Interaction(ItemStackHandler[] stackHandlers, int slot) {

      super(
          stackHandlers,
          slot,
          EnumFacing.VALUES,
          Interaction.createInteractionBounds(slot, 3),
          Interaction.createTransform(slot, 3)
      );
    }

    public void setBelow(InteractionItemStack below) {

      this.below = below;
    }

    public void setAbove(InteractionItemStack above) {

      this.above = above;
    }

    private static AxisAlignedBB createInteractionBounds(int slot, int slotCount) {

      int x = slot % slotCount;
      int y = slot / slotCount;

      double opening = 10.0 / 16.0;
      double size = opening / (double) slotCount;

      return new AxisAlignedBB(
          x * size + 3f / 16f, y * size + 4f / 16f, 1.0 / 16.0,
          x * size + size + 3f / 16f, y * size + size + 4f / 16f, 15.0 / 16.0
      );
    }

    private static Transform createTransform(int slot, int slotCount) {

      int x = slot % slotCount;
      int y = slot / slotCount;

      double opening = 10.0 / 16.0;
      double size = opening / (double) slotCount;

      return new Transform(
          Transform.translate(
              x * size + size / 2.0 + 3.0 / 16.0,
              y * size + size / 2.0 + 4.0 / 16.0,
              0.5
          ),
          Transform.rotate(1, 0, 0, 90),
          Transform.scale(size, 14.0 / 16.0, size)
      );
    }

    @Override
    public boolean isEnabled() {

      if (this.isEmpty()) {

        if (this.below == null) {
          // We're empty and there isn't one below us, we're active!
          return true;
        }

        ItemStack stackInSlot = this.below.getStackInSlot();

        if (stackInSlot.getCount() == stackInSlot.getMaxStackSize()) {
          // We're empty and the one below is full, we're active!
          return true;
        }

        // We're empty, but the one below is not full, delegate to item validation!
        return true;
      }

      // We're not empty, we're active!
      return true;
    }

    @Override
    public Vec3d getTextOffset(EnumFacing tileFacing, EnumFacing playerHorizontalFacing, EnumFacing sideHit) {

      EnumFacing actualSideHit = FacingHelper.translateFacing(tileFacing, sideHit);

      if (actualSideHit == EnumFacing.NORTH) {
        return TEXT_OFFSET_NORTH;

      } else if (actualSideHit == EnumFacing.SOUTH) {
        return TEXT_OFFSET_SOUTH;
      }

      return null;
    }

    @Override
    protected boolean doItemStackValidation(ItemStack itemStack) {

      if (!OreDictHelper.contains("logWood", itemStack)) {
        return false;
      }

      if (this.isEmpty()
          && this.below != null) {

        if (this.below.isEmpty()) {
          return false;
        }

        ItemStack remaining = this.below.insert(itemStack, true);

        if (itemStack.getCount() > remaining.getCount()) {
          // Some items can be stacked below, reject!
          return false;
        }
      }

      return true;
    }

    @Override
    protected void onInsert(EnumType type, ItemStack itemStack, World world, EntityPlayer player, BlockPos pos) {

      super.onInsert(type, itemStack, world, player, pos);

      if (!world.isRemote
          && type == EnumType.MouseClick) {
        world.playSound(
            null,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            SoundEvents.BLOCK_WOOD_PLACE,
            SoundCategory.BLOCKS,
            0.75f,
            (float) (1 + Util.RANDOM.nextGaussian() * 0.4f)
        );
      }
    }
  }

  private class StackHandler
      extends ObservableStackHandler
      implements ITileDataItemStackHandler {

    /* package */ StackHandler(int size) {

      super(size);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {

      if (!OreDictHelper.contains("logWood", stack)) {
        return stack;
      }

      return super.insertItem(slot, stack, simulate);
    }
  }
}
