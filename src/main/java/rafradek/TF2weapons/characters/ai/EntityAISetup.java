package rafradek.TF2weapons.characters.ai;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.characters.EntityEngineer;
import rafradek.TF2weapons.message.TF2ActionHandler;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.weapons.ItemWeapon;
import rafradek.TF2weapons.weapons.ItemWrench;

public class EntityAISetup extends EntityAIBase {

	public EntityEngineer engineer;
	public int buildType;
	public boolean found;
	public Vec3d target;
	public EntityAISetup(EntityEngineer engineer){
		this.engineer=engineer;
		this.setMutexBits(3);
	}
	@Override
	public boolean shouldExecute() {

		if(this.engineer.isInWater()||this.engineer.getHeldItem(EnumHand.MAIN_HAND)==null||this.engineer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemWrench)
			return false;
		
		buildType=(this.engineer.metal>=130&&(this.engineer.sentry==null||this.engineer.sentry.isDead))?1:(this.engineer.metal>=100&&(this.engineer.dispenser==null||this.engineer.dispenser.isDead))?2:0;
		//System.out.println("Promote: "+buildType);
		if(buildType>0){
			this.engineer.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(TF2weapons.itemBuildingBox,1,16+buildType*2+this.engineer.getEntTeam()));
			this.engineer.getHeldItem(EnumHand.MAIN_HAND).setTagCompound(new NBTTagCompound());
		}
		return buildType>0;
	}
	public void resetTask()
    {
    	this.engineer.getNavigator().clearPathEntity();
    	this.engineer.switchSlot(0);
    }
	public void updateTask()
    {
		if(this.buildType==1){
			if(this.target!=null&&this.engineer.getDistanceSq(this.target.xCoord,this.target.yCoord, this.target.zCoord)<2){
				this.engineer.sentry=(EntitySentry) this.spawn();
				return;
			}
			if(this.engineer.getNavigator().noPath()){
				
				Vec3d Vec3d = RandomPositionGenerator.findRandomTarget((EntityCreature) this.engineer, 3, 2);
				if(Vec3d!=null){
					AxisAlignedBB box=new AxisAlignedBB(Vec3d.xCoord-0.5, Vec3d.yCoord, Vec3d.zCoord-0.5, Vec3d.xCoord+0.5, Vec3d.yCoord+1, Vec3d.zCoord+0.5);
					List<AxisAlignedBB> list=this.engineer.worldObj.getCollisionBoxes(this.engineer, box);
					
					if (list.isEmpty()&&!this.engineer.worldObj.isMaterialInBB(box,Material.WATER))
		            {
		                this.engineer.getNavigator().tryMoveToXYZ(Vec3d.xCoord, Vec3d.yCoord, Vec3d.zCoord, 1);
		                this.target=Vec3d;
		            }
				}
				/*for(AxisAlignedBB entry:list){
					System.out.println(entry);
				}*/
				
	            
				
			}
		}
		else if(this.buildType==2&&this.engineer.getNavigator().noPath()){
			if(this.target!=null&&this.engineer.getDistanceSq(this.target.xCoord,this.target.yCoord, this.target.zCoord)<2){
				this.engineer.dispenser= (EntityDispenser) this.spawn();
				return;
			}
			if(this.engineer.getNavigator().noPath()){
				
				Vec3d Vec3d = RandomPositionGenerator.findRandomTarget((this.engineer.sentry!=null&&!this.engineer.sentry.isDead)?this.engineer.sentry:(EntityCreature) this.engineer, 2, 1);
				if(Vec3d!=null){
					AxisAlignedBB box=new AxisAlignedBB(Vec3d.xCoord-0.5, Vec3d.yCoord, Vec3d.zCoord-0.5, Vec3d.xCoord+0.5, Vec3d.yCoord+1, Vec3d.zCoord+0.5);
					List<AxisAlignedBB> list=this.engineer.worldObj.getCollisionBoxes(this.engineer, box);
					/*for(AxisAlignedBB entry:list){
						System.out.println(entry);
					}*/
					if (list.isEmpty()&&!this.engineer.worldObj.isMaterialInBB(box,Material.WATER))
		            {
		                this.engineer.getNavigator().tryMoveToXYZ(Vec3d.xCoord, Vec3d.yCoord, Vec3d.zCoord, 1);
		                this.target=Vec3d;
		            }
				}
			}
		}
    }
	public EntityBuilding spawn(){
		EntityBuilding building;
		if(buildType==1){
			building=new EntitySentry(this.engineer.worldObj,this.engineer);
		}
		else {
			building=new EntityDispenser(this.engineer.worldObj,this.engineer);
		}
		IBlockState blockTarget=this.engineer.worldObj.getBlockState(new BlockPos(target));
		if(!blockTarget.getBlock().isPassable(this.engineer.worldObj, new BlockPos(target))){
			building.setPosition(target.xCoord,target.yCoord+1.3,target.zCoord);
		}
		else{
			building.setPosition(target.xCoord,target.yCoord+0.3,target.zCoord);
		}
		building.setEntTeam(this.engineer.getEntTeam());
		this.engineer.worldObj.spawnEntityInWorld(building);
		this.target=null;
		this.buildType=0;
		this.resetTask();
		return building;
	}
}
