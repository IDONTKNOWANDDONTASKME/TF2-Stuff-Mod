package rafradek.TF2weapons.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

public class ItemChargingTarge extends ItemFromData {

	public ItemChargingTarge() {
		super();
	}

	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world,EntityPlayer living,EnumHand hand) {
		if(!living.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.containsKey("Charging")){
			if(!world.isRemote)
				living.addPotionEffect(new PotionEffect(TF2weapons.charging,40));
			living.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.put("Charging", 280);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}
	public static ItemStack getChargingShield(EntityLivingBase living){
		if(living.getHeldItemMainhand() !=null && living.getHeldItemMainhand().getItem() instanceof ItemChargingTarge){
			return living.getHeldItemMainhand();
		}
		else if(living.getHeldItemOffhand() !=null && living.getHeldItemOffhand().getItem() instanceof ItemChargingTarge){
			return living.getHeldItemOffhand();
		}
		else{
			return null;
		}
	}
	@SideOnly(Side.CLIENT)
    public boolean showDurabilityBar(ItemStack stack)
    {
		if(Minecraft.getMinecraft().thePlayer.getActivePotionEffect(TF2weapons.charging)!=null){
			return true;
		}
    	Integer value=Minecraft.getMinecraft().thePlayer.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.get("Charging");
    	return value!=null&&value>0;
    }
    @SideOnly(Side.CLIENT)
    public double getDurabilityForDisplay(ItemStack stack)
    {
    	if(Minecraft.getMinecraft().thePlayer.getActivePotionEffect(TF2weapons.charging)!=null){
			return 1-((double)Minecraft.getMinecraft().thePlayer.getActivePotionEffect(TF2weapons.charging).getDuration()/(double)40);
		}
    	Integer value=Minecraft.getMinecraft().thePlayer.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.get("Charging");
        return (double)(value!=null?value:0) / (double)280;
    }
}
