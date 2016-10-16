package rafradek.TF2weapons.characters.ai;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.message.TF2ActionHandler;
import rafradek.TF2weapons.weapons.ItemFlameThrower;

public class EntityAIAirblast extends EntityAIBase {

	public int delay;
	public EntityTF2Character host;
	public EntityAIAirblast(EntityTF2Character entity){
		host=entity;
		this.setMutexBits(0);
	}
	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		//System.out.println("executing "+TF2ActionHandler.playerAction.server.get(host));
		//System.out.println("should execute: "+(host.worldObj.getDifficulty().getDifficultyId()>=2));
		return host.worldObj.getDifficulty().getDifficultyId()>=2;
		
	}
	@SuppressWarnings("unchecked")
	public void updateTask() {
		//System.out.println("executing "+TF2ActionHandler.playerAction.server.get(host));
		boolean easier=host.worldObj.getDifficulty()==EnumDifficulty.NORMAL;
		delay--;
		if(delay>0||this.host.getRNG().nextFloat()>(easier?0.22f:0.28f)) {
			host.getCapability(TF2weapons.WEAPONS_CAP, null).state&=5;//System.out.println("reset:");
			return;
		}
		Vec3d eyeVec=new Vec3d(host.posX, host.posY + (double)host.getEyeHeight(), host.posZ);
		List<Entity> list=host.worldObj.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(eyeVec.xCoord-5, eyeVec.yCoord-5, eyeVec.zCoord-5,
				eyeVec.xCoord+5, eyeVec.yCoord+5, eyeVec.zCoord+5));
		boolean airblast=false;
		for (Entity entity : list) {
			//System.out.println(entity+" "+ItemFlameThrower.isPushable(host,entity));
			if(ItemFlameThrower.isPushable(host,entity)&&(entity instanceof EntityThrowable || entity instanceof IProjectile)){
				//System.out.println(entity);
				//System.out.println("dystans: "+(entity.getDistanceSq(host.posX, host.posY + (double)host.getEyeHeight(), host.posZ)<25));
				//System.out.println(TF2weapons.getTeam(entity)+" "+TF2weapons.getTeam(host));
				airblast= entity.getDistanceSq(host.posX, host.posY + (double)host.getEyeHeight(), host.posZ)<(easier?16:25) && TF2weapons.lookingAt(host, (easier?30:45), entity.posX, entity.posY+entity.height/2, entity.posZ);
				if(airblast){
					break;
				}
			}
		}
		
		if(airblast){
			//System.out.println("airblast:");
			((ItemFlameThrower) this.host.getHeldItemMainhand().getItem()).altUse(this.host.getHeldItemMainhand(), host, this.host.worldObj);
			this.delay=easier?30:18;
		}
		else{
			host.getCapability(TF2weapons.WEAPONS_CAP, null).state&=5;
		}
		//System.out.println("end executing "+TF2ActionHandler.playerAction.server.get(host));
	}
}
