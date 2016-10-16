package rafradek.TF2weapons.crafting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class GuiButtonToggleItem extends GuiButton {

	public boolean selected;
	public ItemStack stackToDraw;
	public GuiButtonToggleItem(int buttonId, int x, int y, int widthIn, int heightIn) {
		super(buttonId, x, y, widthIn, heightIn, "");
		// TODO Auto-generated constructor stub
	}

	protected int getHoverState(boolean mouseOver)
    {
        int i = 1;

        if (!this.enabled)
        {
            i = 0;
        }
        else if (mouseOver||selected)
        {
            i = 2;
        }

        return i;
    }
	public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible&&this.stackToDraw!=null)
        {
        	super.drawButton(mc, mouseX, mouseY);
            this.zLevel = 100.0F;
            mc.getRenderItem().zLevel = 100.0F;
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();
            mc.getRenderItem().renderItemAndEffectIntoGUI(this.stackToDraw, this.xPosition+1, this.yPosition+1);
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, this.stackToDraw, this.xPosition+1, this.yPosition+1);
            GlStateManager.disableLighting();
            RenderHelper.disableStandardItemLighting();
            mc.getRenderItem().zLevel = 0.0F;
            this.zLevel = 0.0F;
        }
    }
}
