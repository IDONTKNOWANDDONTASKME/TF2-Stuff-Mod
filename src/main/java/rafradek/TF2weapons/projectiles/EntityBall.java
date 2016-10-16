package rafradek.TF2weapons.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;

public class EntityBall extends EntityProjectileSimple {

	public Vec3d throwPos;
	public boolean canBePickedUp;
	public EntityBall(World world) {
		super(world);
		this.throwPos=new Vec3d(0,0,0);
		// TODO Auto-generated constructor stub
	}

	public EntityBall(World world, EntityLivingBase living, EnumHand hand) {
		super(world, living, hand);
		this.throwPos=this.getPositionVector();
	}
	/*@Override
	public void onHitGround(int x, int y, int z, RayTraceResult mop) {
		if(!this.canBePickedUp){
			super.onHitGround(x, y, z, mop);
		}
	}*/
	public boolean useCollisionBox(){
		return true;
	}
	@Override
	public void onHitBlockX(){
		this.canBePickedUp=true;
    	this.motionX=-this.motionX*0.12;
    	this.motionY=this.motionY*0.3;
    	this.motionZ=this.motionZ*0.3;
	}
	public void onHitBlockY(Block block){
		this.canBePickedUp=true;
		this.motionX=this.motionX*0.3;
    	this.motionY=-this.motionY*0.12;
    	this.motionZ=this.motionZ*0.3;
	}
	@Override
	public void onHitBlockZ(){
		this.canBePickedUp=true;
		this.motionX=this.motionX*0.3;
    	this.motionY=this.motionY*0.3;
    	this.motionZ=-this.motionZ*0.12;
	}
	public void onCollideWithPlayer(EntityPlayer entityIn)
    {
		if(!this.worldObj.isRemote && this.canBePickedUp && entityIn == this.shootingEntity){
			if(entityIn.inventory.addItemStackToInventory(new ItemStack(TF2weapons.itemAmmo,1,14))){
				this.setDead();
			}
		}
    }
	/*@Override
	public void onHitMob(Entity entityHit, RayTraceResult mop) {
		super.onHitMob(entityHit, mop);
		if(!this.worldObj.isRemote){
			
		}
	}*/
	public void setDead(){
		if(this.impact){
			this.impact=false;
			this.canBePickedUp=true;
		}
		else{
			super.setDead();
		}
	}
}
