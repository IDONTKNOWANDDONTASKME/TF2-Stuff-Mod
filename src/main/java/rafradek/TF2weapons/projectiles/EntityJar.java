package rafradek.TF2weapons.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class EntityJar extends EntityProjectileBase {

	public EntityJar(World p_i1756_1_) {
		super(p_i1756_1_);
	}
	
	public EntityJar(World p_i1756_1_, EntityLivingBase p_i1756_2_, EnumHand hand) {
		super(p_i1756_1_, p_i1756_2_, hand);
	}
	
	public void explode(double x, double y, double z,Entity direct, float power){
		if(!this.worldObj.isRemote){
			for(EntityLivingBase living:this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(5, 5, 5))){
				if(living.canBeHitWithPotion() && living.getDistanceSqToEntity(this)<25&&living!=this.shootingEntity && !TF2weapons.isOnSameTeam(this.shootingEntity, living)){
					living.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation(ItemFromData.getData(this.usedWeapon).getString(PropertyType.EFFECT_TYPE)),300));
				}
				else{
					living.extinguish();
				}
			}
			this.playSound(TF2Sounds.JAR_EXPLODE, 1.5f, 1f);
			this.worldObj.playEvent(2002, new BlockPos(this), Potion.getPotionFromResourceLocation(ItemFromData.getData(this.usedWeapon).getString(PropertyType.EFFECT_TYPE))==TF2weapons.jarate?PotionType.getID(PotionTypes.FIRE_RESISTANCE):PotionType.getID(PotionTypes.INVISIBILITY));
			this.setDead();
		}
	}
	
	@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		this.explode(mop.hitVec.xCoord+mop.sideHit.getFrontOffsetX()*0.05, mop.hitVec.yCoord+mop.sideHit.getFrontOffsetY()*0.05, mop.hitVec.zCoord+mop.sideHit.getFrontOffsetZ()*0.05,null,1f);
	}

	@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		this.explode(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord,mop.entityHit,1f);
	}
	public void onUpdate()
    {
		super.onUpdate();
		
    }
	public void spawnParticles(double x, double y, double z){
		if (this.isInWater())
        {
        	this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, this.motionX, this.motionY, this.motionZ);
        }
        /*else{
        	this.worldObj.spawnParticle(EnumParticleTypes., x, y, z, 0, 0, 0);
        }*/
	}
	protected float getSpeed()
    {
        return 1.04f;
    }

}
