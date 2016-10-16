package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2ActionHandler;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemUsable;

public class EntityAIUseMedigun extends EntityAIUseRangedWeapon {


    public EntityAIUseMedigun(EntityTF2Character par1IRangedAttackMob, float par2, float par5)
    {
    	super(par1IRangedAttackMob,par2,par5);
    	
    }
    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */

    /**
     * Resets the task
     */
    
    public void resetTask()
    {
    	TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(0, entityHost), entityHost.dimension);
    	if(this.jump){
			this.entityHost.jump=false;
    	}
    	this.entityHost.getNavigator().clearPathEntity();
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.rangedAttackTime = -1;
    }

    /**
     * Updates the task
     */
    public double lookingAtMax(){
    	if(this.attackTarget==null) return 0;
    	double d0 = this.attackTarget.posX - this.entityHost.posX;
        double d1 = (this.attackTarget.posY + this.attackTarget.getEyeHeight()) - (this.entityHost.posY + (double)this.entityHost.getEyeHeight());
        double d2 = this.attackTarget.posZ - this.entityHost.posZ;
        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
        float compareyaw=Math.abs( 180 - Math.abs(Math.abs(f - MathHelper.wrapDegrees(this.entityHost.rotationYawHead)) - 180)); 
        float comparepitch=Math.abs( 180 - Math.abs(Math.abs(f1 - this.entityHost.rotationPitch) - 180)); 
        //System.out.println("Angled: "+compareyaw+" "+comparepitch);
        return Math.max(compareyaw, comparepitch);
    }
    public void updateTask()
    {
    	if((this.attackTarget != null && this.attackTarget.deathTime>0) || this.entityHost.deathTime>0){
    		this.resetTask();
    		return;
    	}
    	if(this.attackTarget == null){
    		return;
    	}
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
        
        double lookX=this.attackTarget.posX;
        double lookY=this.attackTarget.posY+this.attackTarget.getEyeHeight();
        double lookZ=this.attackTarget.posZ;
        boolean comeCloser=this.entityHost.getEntitySenses().canSee(this.attackTarget);
        this.entityHost.setJumping(true);
        if (comeCloser){
            ++this.field_75318_f;
            if(d0 <= (double)this.attackRangeSquared/4){
            	this.field_75318_f=20;
            }
        }
        else
            this.field_75318_f = 0;

        if (d0 <= (double)this.attackRangeSquared && this.field_75318_f >= 20){
        	if(!this.dodging){
        		this.entityHost.getNavigator().clearPathEntity();
        		this.dodging=true;
        	}
        }
        else {
        	this.dodging=false;
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }
        
        this.entityHost.getLookHelper().setLookPosition(lookX,lookY,lookZ, this.entityHost.rotation, 90.0F);
        //this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 1.0F, 90.0F);
        if(d0 <= 64){
        	
        	if(!pressed){
        		pressed=true;
        		this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).healTarget=this.attackTarget.getEntityId();
        		this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state=1;
        		TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(1, entityHost), entityHost.dimension);
        		TF2weapons.network.sendToDimension(new TF2Message.CapabilityMessage(entityHost),entityHost.dimension);
        		//System.out.println("co�do");
        	}
        	
    	}
        else{
        	if(pressed){
        		if(this.jump){
        			this.entityHost.jump=false;
            	}
        		this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).healTarget=-1;
        		this.entityHost.getCapability(TF2weapons.WEAPONS_CAP, null).state=0;
        		TF2weapons.network.sendToDimension(new TF2Message.ActionMessage(0, entityHost), entityHost.dimension);
        		TF2weapons.network.sendToDimension(new TF2Message.CapabilityMessage(entityHost),entityHost.dimension);
		    	//System.out.println("co�z");
        	}
        	pressed=false;
        }
        //if(){
        	if(this.jump&&d0<this.jumprange){
        		this.entityHost.jump=true;
        	}
        	else if(this.jump){
        		this.entityHost.jump=false;
        	}
        	if(this.dodge&&this.entityHost.getNavigator().noPath()){
	        	Vec3d Vec3d = RandomPositionGenerator.findRandomTarget((EntityCreature) this.entityHost, 3, 2);
	
	            if (Vec3d != null)
	            {
	               this.entityHost.getNavigator().tryMoveToXYZ(Vec3d.xCoord, Vec3d.yCoord, Vec3d.zCoord, this.entityMoveSpeed*this.dodgeSpeed);
	            }
        	}
       // }
    }

}
