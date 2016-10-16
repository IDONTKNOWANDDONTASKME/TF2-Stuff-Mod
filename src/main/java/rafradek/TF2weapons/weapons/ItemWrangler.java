package rafradek.TF2weapons.weapons;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemWrangler extends ItemUsable {

	public ItemWrangler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		return true;
	}

	@Override
	public boolean fireTick(ItemStack stack, EntityLivingBase living, World world) {
		// TODO Auto-generated method stub
		EntitySentry sentry=living.getCapability(TF2weapons.WEAPONS_CAP, null).controlledSentry;
		if(sentry != null && sentry.isEntityAlive()){
			sentry.shootBullet(living);
		}
		return false;
	}

	@Override
	public boolean altFireTick(ItemStack stack, EntityLivingBase living, World world) {
		EntitySentry sentry=living.getCapability(TF2weapons.WEAPONS_CAP, null).controlledSentry;
		if(sentry != null && sentry.isEntityAlive()){
			sentry.shootRocket(living);
		}
		return false;
	}
	public void draw(WeaponsCapability weaponsCapability,ItemStack stack, final EntityLivingBase living, World world) {
		super.draw(weaponsCapability, stack, living, world);
		if(!world.isRemote){
			weaponsCapability.controlledSentry=null;
			List<EntitySentry> list=world.getEntitiesWithinAABB(EntitySentry.class, living.getEntityBoundingBox().expand(128, 128, 128), new Predicate<EntitySentry>(){
	
				@Override
				public boolean apply(EntitySentry input) {
					// TODO Auto-generated method stub
					return input.getOwner()==living;
				}
				
			});
			Collections.sort(list, new EntityAINearestAttackableTarget.Sorter(living));
			if(!list.isEmpty()){
				list.get(0).setControlled(true);
				weaponsCapability.controlledSentry=list.get(0);
			}
		}
	}
	public void holster(WeaponsCapability weaponsCapability,ItemStack stack, final EntityLivingBase living, World world) {
		if(weaponsCapability.controlledSentry!=null){
			weaponsCapability.controlledSentry.setControlled(false);
		}
		weaponsCapability.controlledSentry=null;
	}
}
