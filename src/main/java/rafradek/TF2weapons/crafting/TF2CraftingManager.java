package rafradek.TF2weapons.crafting;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;

public class TF2CraftingManager {
	public static final ShapelessOreRecipe[] AMMO_RECIPES=new ShapelessOreRecipe[14];
	public static final TF2CraftingManager INSTANCE= new TF2CraftingManager();
    private final List<IRecipe> recipes = Lists.<IRecipe>newArrayList();

	public TF2CraftingManager(){
		ItemStack bonk=ItemFromData.getNewStack("bonk");
		bonk.stackSize=2;
		ItemStack cola=ItemFromData.getNewStack("critcola");
		cola.stackSize=2;
		addRecipe(TF2CraftingManager.AMMO_RECIPES[1]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,1),new Object[]{"ingotCopper","ingotLead","gunpowder"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[2]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,20,2),new Object[]{"ingotCopper","ingotLead","gunpowder"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[3]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,20,3),new Object[]{"ingotCopper","ingotLead","gunpowder"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[4]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,4),new Object[]{"ingotCopper","ingotLead","gunpowder"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[5]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,32,5),new Object[]{"ingotCopper","ingotLead","gunpowder"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[6]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,3,6),new Object[]{"ingotCopper","ingotLead","gunpowder"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[7]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,7),new Object[]{"ingotIron","ingotIron",Blocks.TNT}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[8]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,8),new Object[]{"ingotIron","ingotIron",Blocks.TNT}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[11]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,11),new Object[]{"ingotIron","ingotIron",Blocks.TNT}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemAmmo,8,13),new Object[]{" R ","RIR"," R ",'I',"ingotIron",'R',"dustRedstone"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemAmmo,4,14),new Object[]{" P ","P P"," P ",'P',"paper"}));
		addShapelessRecipe(new ItemStack(TF2weapons.itemAmmoMedigun,1),new Object[]{Items.SPECKLED_MELON,Items.GHAST_TEAR,new ItemStack(Items.DYE,1,15)});
		addRecipe(new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmoFire,1),new Object[]{"ingotIron",Items.MAGMA_CREAM,"ingotIron"}));
		addRecipe(TF2CraftingManager.AMMO_RECIPES[9]=new ShapelessOreRecipe(new ItemStack(TF2weapons.itemAmmo,25,9),new Object[]{"ingotIron","paper"}));
		addRecipe(new AustraliumRecipe());
		addRecipe(new ShapedOreRecipe(ItemFromData.getNewStack("cloak"),new Object[]{"AAA","LGL","AAA",'A',"ingotAustralium",'I',"ingotIron",'G', "blockGlass",'L',"leather"}));
		addRecipe(new ShapedOreRecipe(TF2weapons.itemDisguiseKit,new Object[]{"I I","PAG","I I",'A',"ingotAustralium",'I',"ingotIron",'G', "blockGlass",'P',"paper"}));
		addRecipe(new ShapedOreRecipe(ItemFromData.getNewStack("sapper"),new Object[]{" R ","IRI"," R ",'I',"ingotIron",'R', "dustRedstone"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemBuildingBox,1,18),new Object[]{"RDR","GIG","III",'D',new ItemStack(Blocks.DISPENSER),'I',"ingotIron",'G', "gunpowder",'R',"dustRedstone"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemHorn),new Object[]{"CLC","C C"," C ",'C',"ingotCopper",'L',"leather"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemBuildingBox,1,20),new Object[]{"MDR","SIm","rIG",'D',new ItemStack(Blocks.DISPENSER),'I',"ingotIron",'M',new ItemStack(TF2weapons.itemAmmo,1,12),'G', new ItemStack(TF2weapons.itemAmmo,1,8),'R',"dustRedstone",'r', new ItemStack(TF2weapons.itemAmmo,1,7),'S', new ItemStack(TF2weapons.itemAmmo,1,1),'m', new ItemStack(TF2weapons.itemAmmo,1,2)}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemBuildingBox,1,22),new Object[]{"IAI","RAR","IAI",'I',"ingotIron",'A',new ItemStack(TF2weapons.itemTF2,1,6),'R',"dustRedstone"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.blockAmmoFurnace),new Object[]{"RIG","SFr","sIM",'F',new ItemStack(Blocks.FURNACE),'I',"ingotIron",'M',new ItemStack(TF2weapons.itemAmmo,1,2),'G', new ItemStack(TF2weapons.itemAmmo,1,8),'R', new ItemStack(TF2weapons.itemAmmo,1,7),'r', new ItemStack(TF2weapons.itemAmmo,1,6),'s', new ItemStack(TF2weapons.itemAmmo,1,1),'S', new ItemStack(TF2weapons.itemAmmo,1,11)}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemAmmoBelt),new Object[]{" IL","IL ","L  ",'I',"ingotIron",'L',"leather"}));
		addRecipe(new ShapedOreRecipe(bonk,new Object[]{"SDS","IWI","SAS",'I',"ingotIron",'A',new ItemStack(TF2weapons.itemTF2,1,6),'W',new ItemStack(Items.WATER_BUCKET),'S',new ItemStack(Items.SUGAR),'D',"dyeYellow"}));
		addRecipe(new ShapedOreRecipe(cola,new Object[]{"SDS","IWI","SAS",'I',"ingotIron",'A',new ItemStack(TF2weapons.itemTF2,1,6),'W',new ItemStack(Items.WATER_BUCKET),'S',new ItemStack(Items.SUGAR),'D',"dyeMagenta"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemSandvich),new Object[]{" B ","LHL"," B ",'B',new ItemStack(Items.BREAD),'L',new ItemStack(Blocks.TALLGRASS,1,1),'H',new ItemStack(Items.PORKCHOP)}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemChocolate,2),new Object[]{"CCC","CCC","MII",'C',new ItemStack(Items.DYE,1,3),'M',new ItemStack(Items.MILK_BUCKET),'I',"paper"}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemScoutBoots),new Object[]{"FFF","FBF","FFF",'F',new ItemStack(Items.FEATHER),'B',new ItemStack(Items.LEATHER_BOOTS)}));
		addRecipe(new ShapedOreRecipe(new ItemStack(TF2weapons.itemMantreads),new Object[]{" B ","III",'I',"ingotIron",'B',new ItemStack(Items.IRON_BOOTS)}));
		
		ItemStack jarate=ItemFromData.getNewStack("jarate");
		jarate.getTagCompound().setBoolean("IsEmpty", true);
		addRecipe(new ShapedOreRecipe(jarate,new Object[]{" G ","G G","GGG",'G',"paneGlass"}));
		ItemStack madmilk=ItemFromData.getNewStack("madmilk");
		madmilk.getTagCompound().setBoolean("IsEmpty", true);
		addRecipe(new ShapedOreRecipe(madmilk,new Object[]{" G ","G G","GGG",'G',"paneGlass"}));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,4),new ItemStack(TF2weapons.itemTF2,1,3),new ItemStack(TF2weapons.itemTF2,1,3),new ItemStack(TF2weapons.itemTF2,1,3));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,5),new ItemStack(TF2weapons.itemTF2,1,4),new ItemStack(TF2weapons.itemTF2,1,4),new ItemStack(TF2weapons.itemTF2,1,4));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,3,4),new ItemStack(TF2weapons.itemTF2,1,5));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,3,3),new ItemStack(TF2weapons.itemTF2,1,4));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,9),new ItemStack(TF2weapons.itemTF2,1,3),new ItemStack(TF2weapons.itemTF2,1,3));
		addShapelessRecipe(new ItemStack(TF2weapons.itemTF2,1,10),new ItemStack(TF2weapons.itemTF2,1,5),new ItemStack(TF2weapons.itemTF2,1,5),new ItemStack(TF2weapons.itemTF2,1,5));
		addRecipe(new OpenCrateRecipe());
		addRecipe(new RecipeToScrap());
	}
	
	public ShapedRecipes addRecipe(ItemStack stack, Object... recipeComponents)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (recipeComponents[i] instanceof String[])
        {
            String[] astring = (String[])((String[])recipeComponents[i++]);

            for (String s2 : astring)
            {
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }
        else
        {
            while (recipeComponents[i] instanceof String)
            {
                String s1 = (String)recipeComponents[i++];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        }

        Map<Character, ItemStack> map;

        for (map = Maps.<Character, ItemStack>newHashMap(); i < recipeComponents.length; i += 2)
        {
            Character character = (Character)recipeComponents[i];
            ItemStack itemstack = null;

            if (recipeComponents[i + 1] instanceof Item)
            {
                itemstack = new ItemStack((Item)recipeComponents[i + 1]);
            }
            else if (recipeComponents[i + 1] instanceof Block)
            {
                itemstack = new ItemStack((Block)recipeComponents[i + 1], 1, 32767);
            }
            else if (recipeComponents[i + 1] instanceof ItemStack)
            {
                itemstack = (ItemStack)recipeComponents[i + 1];
            }

            map.put(character, itemstack);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int l = 0; l < j * k; ++l)
        {
            char c0 = s.charAt(l);

            if (map.containsKey(Character.valueOf(c0)))
            {
                aitemstack[l] = ((ItemStack)map.get(Character.valueOf(c0))).copy();
            }
            else
            {
                aitemstack[l] = null;
            }
        }

        ShapedRecipes shapedrecipes = new ShapedRecipes(j, k, aitemstack, stack);
        this.recipes.add(shapedrecipes);
        return shapedrecipes;
    }

    /**
     * Adds a shapeless crafting recipe to the the game.
     */
    public void addShapelessRecipe(ItemStack stack, Object... recipeComponents)
    {
        List<ItemStack> list = Lists.<ItemStack>newArrayList();

        for (Object object : recipeComponents)
        {
            if (object instanceof ItemStack)
            {
                list.add(((ItemStack)object).copy());
            }
            else if (object instanceof Item)
            {
                list.add(new ItemStack((Item)object));
            }
            else
            {
                if (!(object instanceof Block))
                {
                    throw new IllegalArgumentException("Invalid shapeless recipe: unknown type " + object.getClass().getName() + "!");
                }

                list.add(new ItemStack((Block)object));
            }
        }

        this.recipes.add(new ShapelessRecipes(stack, list));
    }

    /**
     * Adds an IRecipe to the list of crafting recipes.
     */
    public void addRecipe(IRecipe recipe)
    {
        this.recipes.add(recipe);
    }

    /**
     * Retrieves an ItemStack that has multiple recipes for it.
     */
    @Nullable
    public ItemStack findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getCraftingResult(craftMatrix);
            }
        }

        return null;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : this.recipes)
        {
            if (irecipe.matches(craftMatrix, worldIn))
            {
                return irecipe.getRemainingItems(craftMatrix);
            }
        }

        ItemStack[] aitemstack = new ItemStack[craftMatrix.getSizeInventory()];

        for (int i = 0; i < aitemstack.length; ++i)
        {
            aitemstack[i] = craftMatrix.getStackInSlot(i);
            
        }

        return aitemstack;
    }

    public List<IRecipe> getRecipeList()
    {
        return this.recipes;
    }
}
