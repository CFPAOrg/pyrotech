package com.codetaylor.mc.pyrotech.library.spi.plugin.jei;

import com.codetaylor.mc.athenaeum.util.StringHelper;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.IPyrotechRecipeWrapper;
import com.codetaylor.mc.pyrotech.library.spi.recipe.IRecipeTimed;
import net.minecraft.client.Minecraft;

import java.awt.*;

public abstract class JEIRecipeWrapperTimed
    implements IPyrotechRecipeWrapper {

  protected final String timeString;

  public JEIRecipeWrapperTimed(IRecipeTimed recipe) {

    this.timeString = StringHelper.ticksToHMS(recipe.getTimeTicks());
  }

  @Override
  public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    int stringWidth = minecraft.fontRenderer.getStringWidth(this.timeString);
    minecraft.fontRenderer.drawString(this.timeString, this.getTimeDisplayX(stringWidth), this.getTimeDisplayY(), Color.DARK_GRAY.getRGB());
  }

  protected int getTimeDisplayX(int stringWidth) {

    return 36 - stringWidth / 2;
  }

  protected int getTimeDisplayY() {

    return 30;
  }
}
