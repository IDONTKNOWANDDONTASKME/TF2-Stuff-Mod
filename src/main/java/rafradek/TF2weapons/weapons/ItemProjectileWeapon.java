package rafradek.TF2weapons.weapons;

import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2ProjectileHandler;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;
import rafradek.TF2weapons.projectiles.EntityBall;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;
import rafradek.TF2weapons.projectiles.EntityRocket;

public class ItemProjectileWeapon extends ItemWeapon {

	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world,
			int thisCritical, EnumHand hand) {
		if(!world.isRemote){
			//System.out.println("Tick: "+living.ticksExisted);
			EntityProjectileBase proj;
			/*double oldX=living.posX;
			double oldY=living.posY;
			double oldZ=living.posZ;
			float oldPitch=living.rotationPitch;
			float oldYaw=living.rotationYawHead;
			if(this.usePrediction()&&living instanceof EntityPlayer){
				PredictionMessage message=TF2ProjectileHandler.nextShotPos.get(living);
				living.posX=message.x;
				living.posY=message.y;
				living.posZ=message.z;
				living.rotationYawHead=message.yaw;
				living.rotationPitch=message.pitch;
			}*/
			try {
				proj = MapList.projectileClasses.get(ItemFromData.getData(stack).getString(PropertyType.PROJECTILE)).getConstructor(World.class,EntityLivingBase.class,EnumHand.class).newInstance(world,living,hand);
				//proj.setIsCritical(thisCritical);
				world.spawnEntityInWorld(proj);
				proj.setCritical(thisCritical);
			} catch (Exception exception){
				exception.printStackTrace();
			}
			}
			//living.posX=oldX;
			//living.posY=oldY;
			//living.posZ=oldZ;
			//living.rotationPitch=oldPitch;
			//living.rotationYawHead=oldYaw;
	}
	public void onDealDamage(ItemStack stack, EntityLivingBase attacker, Entity target, DamageSource source){
		super.onDealDamage(stack, attacker, target, source);
		if(target instanceof EntityLivingBase && !(target instanceof EntityBuilding) && getData(stack).getName().equals("sandmanball")){
			EntityBall ball=(EntityBall) source.getSourceOfDamage();
			if(!ball.canBePickedUp && ball.throwPos.squareDistanceTo(target.getPositionVector())>600){
				TF2weapons.stun((EntityLivingBase) target, 160, true);
				target.playSound(TF2Sounds.WEAPON_STUN_MAX, 4f, 1f);
			}
			else if(!ball.canBePickedUp && ball.throwPos.squareDistanceTo(target.getPositionVector())>8){
				TF2weapons.stun((EntityLivingBase) target, (int) (ball.throwPos.distanceTo(target.getPositionVector())*8), false);
				target.playSound(TF2Sounds.WEAPON_STUN, 1.6f, 1f);
			}
		}
	}
	public float getProjectileSpeed(ItemStack stack,EntityLivingBase living){
		return TF2Attribute.getModifier("Proj Speed", stack, (float) ItemFromData.getData(stack).getFloat(PropertyType.PROJECTILE_SPEED),living);
	}
	@Override
    public boolean canFire(World world, EntityLivingBase living, ItemStack stack)
    {
    	return /*(((!(living instanceof EntityPlayer) || ) || TF2ProjectileHandler.nextShotPos.containsKey(living))||world.isRemote*/super.canFire(world, living, stack);
    }
}
