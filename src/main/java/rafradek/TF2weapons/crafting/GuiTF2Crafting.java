package rafradek.TF2weapons.crafting;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;

public class GuiTF2Crafting extends GuiContainer
{
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation(TF2weapons.MOD_ID,"textures/gui/container/cabinet.png");

    public GuiButtonToggleItem[] buttonsItem;
    public ItemStack[] itemsToRender;
    public int firstIndex;
    public float scroll;
    public int tabid;
    public ItemStack craftingTabStack=new ItemStack(TF2weapons.itemAmmo,1,1);
    public ItemStack chestTabStack=new ItemStack(Blocks.CHEST);

    //public TileEntityCabinet cabinet;
	private boolean isScrolling;

	private boolean wasClicking;
    
    public GuiTF2Crafting(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition)
    {
        super(new ContainerTF2Workbench(Minecraft.getMinecraft().thePlayer,playerInv, worldIn, blockPosition));
        //this.cabinet=cabinet;
        this.xSize=176;
        this.ySize=180;
        this.itemsToRender=new ItemStack[9];
        this.buttonsItem=new GuiButtonToggleItem[12];
    }
    public void initGui()
    {
        super.initGui();
        for(int x=0;x<3;x++){
        	for(int y=0;y<4;y++){
        		this.buttonList.add(buttonsItem[x+y*3]=new GuiButtonToggleItem(x+y*3, this.guiLeft+7+x*18, this.guiTop+14+y*18, 18, 18));
        	}
        }
        setButtons();
    }
    public void setButtons(){
    	for(int i=0;i<12;i++){
    		//System.out.println("Buttons: "+buttonsItem[i]+" "+firstIndex);
    		if(i+firstIndex<TF2CraftingManager.INSTANCE.getRecipeList().size()){
    			buttonsItem[i].stackToDraw=TF2CraftingManager.INSTANCE.getRecipeList().get(i+firstIndex).getRecipeOutput();
    			buttonsItem[i].selected=i+firstIndex==((ContainerTF2Workbench)this.inventorySlots).currentRecipe;
    		}
    		else{
    			buttonsItem[i].stackToDraw=null;
    			buttonsItem[i].selected=false;
    		}
    	}
    }
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
    	boolean flag = Mouse.isButtonDown(0);
        int i = this.guiLeft;
        int j = this.guiTop;
        int k = i + 61;
        int l = j + 14;
        int i1 = k + 14;
        int j1 = l + 72;

        if (!this.wasClicking && flag && mouseX >= k && mouseY >= l && mouseX < i1 && mouseY < j1)
        {
            this.isScrolling = true;
        }

        if (!flag)
        {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling)
        {
        	int size=TF2CraftingManager.INSTANCE.getRecipeList().size();
            this.scroll = ((float)(mouseY - l) - 7.5F) / ((float)(j1 - l) - 15.0F);
            this.scroll = MathHelper.clamp_float(this.scroll, 0.0F, 1.0F);
            this.firstIndex=Math.round(this.scroll*(size-12)/3)*3;
            this.setButtons();
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    protected void drawTab(int id)
    {
    	boolean flag=id==tabid;
        boolean flag1 = true;
        int i = id;
        int j = 200+i * 28;
        int k = 16;
        int l = this.guiLeft + 28 * i;
        int i1 = this.guiTop;
        int j1 = 32;

        if (flag)
        {
            k += 32;
        }

        if (i == 5)
        {
            l = this.guiLeft + this.xSize - 28;
        }
        else if (i > 0)
        {
            l += i;
        }

        if (flag1)
        {
            i1 = i1 - 28;
        }
        else
        {
            k += 64;
            i1 = i1 + (this.ySize - 4);
        }

        GlStateManager.disableLighting();
        GlStateManager.color(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        GlStateManager.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.drawTexturedModalRect(l, i1, j, k, 28, 32);
        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        l = l + 6;
        i1 = i1 + 8 + (flag1 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        ItemStack itemstack = id==0?craftingTabStack:chestTabStack;
        this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
        this.itemRender.renderItemOverlays(this.fontRendererObj, itemstack, l, i1);
        GlStateManager.disableLighting();
        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }
    public void drawHoveringText(List<String> textLines, int x, int y)
    {
        drawHoveringText(textLines, x, y, fontRendererObj);   
    }
    @SuppressWarnings("unchecked")
	protected void actionPerformed(GuiButton button) throws IOException
    {
    	if(button.id<12){
    		int currentRecipe=button.id+this.firstIndex;
    		((ContainerTF2Workbench)this.inventorySlots).currentRecipe=currentRecipe;
    		TF2weapons.network.sendToServer(new TF2Message.GuiConfigMessage(this.inventorySlots.windowId, (byte) 0, currentRecipe));
    		setButtons();
    		this.inventorySlots.onCraftMatrixChanged(null);
    		itemsToRender=new ItemStack[9];
            if(currentRecipe>=0&&currentRecipe<TF2CraftingManager.INSTANCE.getRecipeList().size()){
            	IRecipe recipe=TF2CraftingManager.INSTANCE.getRecipeList().get(currentRecipe);
            	
            	if(recipe instanceof ShapelessOreRecipe){
            		List<Object> input=((ShapelessOreRecipe)recipe).getInput();
            		for(int i=0;i<input.size();i++){
            			if(input.get(i) instanceof ItemStack){
            				itemsToRender[i]=(ItemStack) input.get(i);
            			}
            			else if(input.get(i) != null){
            				itemsToRender[i]=((List<ItemStack>) input.get(i)).get(0);
            			}
            		}
            	}
            	else if(recipe instanceof ShapelessRecipes){
            		List<ItemStack> input=((ShapelessRecipes)recipe).recipeItems;
            		for(int i=0;i<input.size();i++){
            			itemsToRender[i]=(ItemStack) input.get(i);
            		}
            	}
            	else if(recipe instanceof ShapedOreRecipe){
            		Object[] input=((ShapedOreRecipe)recipe).getInput();
            		for(int i=0;i<input.length;i++){
            			if(input[i] instanceof ItemStack){
            				itemsToRender[i]=(ItemStack) input[i];
            			}
            			else if(input[i] != null){
            				itemsToRender[i]=((List<ItemStack>) input[i]).get(0);
            			}
            		}
            	}
            	else if(recipe instanceof ShapedRecipes){
            		ItemStack[] input=((ShapedRecipes)recipe).recipeItems;
            		for(int i=0;i<input.length;i++){
            			itemsToRender[i]=(ItemStack) input[i];
            		}
            	}
            	else if(recipe instanceof AustraliumRecipe){
            		itemsToRender[0]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[1]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[2]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[3]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[4]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[6]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[7]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[8]=new ItemStack(TF2weapons.itemTF2,1,2);
            		itemsToRender[5]=new ItemStack(TF2weapons.itemTF2,1,9);
            	}
            	else if(recipe instanceof RecipeToScrap){
            		itemsToRender[0]=new ItemStack(TF2weapons.itemTF2,1,9);
            		itemsToRender[1]=new ItemStack(TF2weapons.itemTF2,1,9);
            	}
            	else if(recipe instanceof OpenCrateRecipe){
            		itemsToRender[0]=new ItemStack(TF2weapons.itemTF2,1,7);
            		itemsToRender[1]=ItemFromData.getNewStack("crate1");
            	}
            }
    	}
    }
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 8, 5, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 3, 4210752);
        for(int i=0;i<12;i++){
        	if(this.buttonsItem[i].stackToDraw !=null && this.buttonsItem[i].isMouseOver()){
        		((GuiTF2Crafting)mc.currentScreen).drawHoveringText(this.buttonsItem[i].stackToDraw.getTooltip(mc.thePlayer, false), mouseX-this.guiLeft, mouseY-this.guiTop);
        	}
        }
        /*for(int i=0;i<4;i++){
        	this.fontRendererObj.drawString(I18n.format(TF2CraftingManager.INSTANCE.getRecipeList().get(i).getRecipeOutput().getDisplayName(), new Object[0]), 10, 17+18*i, 16777215);
        }*/
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
       // this.drawTab(0);
        //this.drawTab(1);
        
        x = this.guiLeft + 62;
        y = this.guiTop + 15;
        int k = y + 72;
        
        this.drawTexturedModalRect(x, y + (int)((float)(k - y - 17) * this.scroll), 232, 0, 12, 15);
		GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.zLevel=0;
        for(x=0;x<3;x++){
        	for(y=0;y<3;y++){
	        	ItemStack stack=this.itemsToRender[x+y*3];
	        	if(stack!=null){
	        		this.itemRender.renderItemIntoGUI(stack, this.guiLeft+86+18*x, this.guiTop+23+18*y);
	        	}
        	}
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
        this.zLevel=120;
        this.drawTexturedModalRect(85+this.guiLeft, 22+this.guiTop, 85, 22, 54,54);
        this.zLevel=0;
        /*int currentRecipe=((ContainerTF2Workbench)this.inventorySlots).currentRecipe;
        if(currentRecipe>=0&&currentRecipe<TF2CraftingManager.INSTANCE.getRecipeList().size()){
        	IRecipe recipe=TF2CraftingManager.INSTANCE.getRecipeList().get(currentRecipe);
        	
        	if(recipe instanceof ShapelessOreRecipe){
        		List<Object> input=;
        		for(int i=0;i<((ShapelessOreRecipe)recipe).getInput().size();i++){
        			this.itemRender.renderItemIntoGUI(((ShapelessOreRecipe)recipe).getInput().get(i), , y);
        		}
        	}
        	
        }*/
    }
    /*protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel)
    {
        GlStateManager.pushMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.setupGuiTransform(x, y, bakedmodel.isGui3d());
        bakedmodel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
        GlStateManager.color(0.4F, 0.4F, 0.4F);
        this.itemRender.renderItem(stack, bakedmodel);
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
    }
    private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d)
    {
        GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + this.zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, -1.0F, 1.0F);
        GlStateManager.scale(16.0F, 16.0F, 16.0F);

        if (isGui3d)
        {
            GlStateManager.enableLighting();
        }
        else
        {
            GlStateManager.disableLighting();
        }
    }*/
}