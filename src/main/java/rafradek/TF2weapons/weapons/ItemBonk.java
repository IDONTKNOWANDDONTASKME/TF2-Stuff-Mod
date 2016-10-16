package rafradek.TF2weapons.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemBonk extends ItemFromData {
	public ItemBonk()
    {
        super();
        this.setMaxStackSize(64);
    }
	public int getMaxItemUseDuration(ItemStack stack)
    {
        return 40;
    }
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }
    @SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack)
    {
    	Integer value=Minecraft.getMinecraft().thePlayer.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.get(getData(stack).getName());
    	return value!=null&&value>0;
    }
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack)
    {
    	Integer value=Minecraft.getMinecraft().thePlayer.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.get(getData(stack).getName());
        return (double)(value!=null?value:0) / (double)ItemFromData.getData(stack).getInt(PropertyType.COOLDOWN);
    }
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
    	Integer value=playerIn.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.get(getData(itemStackIn).getName());
    	if(value==null||value<=0){
    		playerIn.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    	}
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
    }
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
    	if(!(entityLiving instanceof EntityPlayer&&((EntityPlayer)entityLiving).capabilities.isCreativeMode))
    		stack.stackSize--;
    	entityLiving.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.put(getData(stack).getName(), ItemFromData.getData(stack).getInt(PropertyType.COOLDOWN));
    	entityLiving.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(getData(stack).getString(PropertyType.EFFECT_TYPE)), ItemFromData.getData(stack).getInt(PropertyType.DURATION)));
    	return stack;
    }
}
