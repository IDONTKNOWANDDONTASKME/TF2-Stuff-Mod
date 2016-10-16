package rafradek.TF2weapons.building;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;

public class EntityTeleporter extends EntityBuilding {
	//public static ArrayList<BlockPosDimension> teleportersData=new ArrayList<BlockPosDimension>();
	public static final int TP_PER_PLAYER=64;
	
	public static int tpCount=0;
	public static HashMap<UUID, TeleporterData[]> teleporters=new HashMap<UUID, TeleporterData[]>();
	
	public int tpID=-1;
	public int ticksToTeleport;
	
	public EntityTeleporter linkedTp;
	
	public float spin;
	public float spinRender;
	
	private static final DataParameter<Integer> TELEPORTS = EntityDataManager.createKey(EntityTeleporter.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TPPROGRESS = EntityDataManager.createKey(EntityTeleporter.class, DataSerializers.VARINT);
	private static final DataParameter<Byte> CHANNEL = EntityDataManager.createKey(EntityTeleporter.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> EXIT = EntityDataManager.createKey(EntityTeleporter.class, DataSerializers.BOOLEAN);
	public EntityTeleporter(World worldIn) {
		super(worldIn);
		this.setSize(1f, 0.2f);
	}
	public EntityTeleporter(World worldIn,EntityLivingBase living) {
		super(worldIn,living);
		this.setSize(1f, 0.2f);
	}
	public AxisAlignedBB getCollisionBox(Entity entityIn)
    {
		if(!this.worldObj.isRemote&&!this.isExit()&&this.getTPprogress()<=0&& entityIn!=null&&entityIn instanceof EntityLivingBase&&!(entityIn instanceof EntityBuilding)
				&&((TF2weapons.dispenserHeal && getTeam()==null && ((EntityLivingBase) entityIn).getTeam() == null)||TF2weapons.isOnSameTeam(EntityTeleporter.this,entityIn))
				&&entityIn.getEntityBoundingBox().intersectsWith(this.getEntityBoundingBox().expand(0, 0.5, 0).offset(0, 0.5D, 0))){
			
			if(ticksToTeleport<=0){
				if(ticksToTeleport<0){
					ticksToTeleport=10;
				}
				else{
					TeleporterData exit=this.getTeleportExit();
					if(exit !=null){
						if(exit.dimension!=this.dimension)
							entityIn.changeDimension(exit.dimension);
						entityIn.setPositionAndUpdate(exit.getX()+0.5, exit.getY()+0.23, exit.getZ()+0.5);
						this.setTeleports(this.getTeleports()+1);
						this.setTPprogress(this.getLevel()==1?200:(this.getLevel()==2?100:60));
						this.playSound(TF2Sounds.MOB_TELEPORTER_SEND, 1.5f, 1f);
						entityIn.playSound(TF2Sounds.MOB_TELEPORTER_RECEIVE, 0.75f, 1f);
					}
				}
			}
		}
		return super.getCollisionBox(entityIn);
    }
	public void onLivingUpdate(){
		super.onLivingUpdate();
		if(!this.worldObj.isRemote){
			if(this.tpID==-1){
				tpCount++;
				this.tpID=tpCount;
			}
			if(!this.isExit()){
				ticksToTeleport--;
				if(this.getTPprogress()>0){
					this.setTPprogress(this.getTPprogress()-1);
				}
				if(this.getSoundState()==1&&(this.getTPprogress()>0||this.getTeleportExit()==null)){
					this.setSoundState(0);
					if(this.linkedTp!=null){
						this.linkedTp.setSoundState(0);
					}
					
				}
				if(this.getSoundState()==0&&this.getTPprogress()<=0&&this.getTeleportExit()!=null){
					this.setSoundState(1);
					if(this.linkedTp!=null){
						this.linkedTp.setSoundState(1);
					}
				}
				/*if(ticksToTeleport<=0){
					List<EntityLivingBase> targetList=this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().expand(0, 0.5, 0).offset(0, 0.5D, 0), new Predicate<EntityLivingBase>(){
		
						@Override
						public boolean apply(EntityLivingBase input) {
							
							return !(input instanceof EntityBuilding)&&EntityTeleporter.this!=input&&((TF2weapons.dispenserHeal&&input instanceof EntityPlayer && getTeam()==null && input.getTeam() == null)||TF2weapons.isOnSameTeam(EntityTeleporter.this,input));
						}
						
					});
					
					if(!targetList.isEmpty()){
						if(ticksToTeleport<0){
							ticksToTeleport=10;
						}
						else{
							BlockPosDimension exit=this.getTeleportExit();
							if(exit !=null){
								if(exit.dimension!=this.dimension)
									targetList.get(0).travelToDimension(exit.dimension);
								targetList.get(0).setPositionAndUpdate(exit.getX()+0.5, exit.getY()+0.23, exit.getZ()+0.5);
							}
						}
					}
				}*/
			}
			else if(teleporters.get(this.getOwnerId())[this.getID()]==null||teleporters.get(this.getOwnerId())[this.getID()].id!=this.tpID){
				this.setExit(false);
				this.updateTeleportersData(true);
			}
			else if(teleporters.get(this.getOwnerId())[this.getID()].id==this.tpID&&!this.getPosition().equals(teleporters.get(this.getOwnerId())[this.getID()])){
				this.updateTeleportersData(false);
			}
		}
		else{
			if(this.getSoundState()==1){
				this.spin+=(float)Math.PI*(this.getLevel()==1?0.25f:(this.getLevel()==2?0.325f:0.4f));
			}
			else{
				this.spin=0;
			}
		}
	}
	public TeleporterData getTeleportExit(){
		UUID uuid=this.getOwnerId();
		if(this.getOwnerId()!=null&&teleporters.get(uuid)!=null){
			final TeleporterData data=teleporters.get(uuid)[this.getID()];
			List<EntityTeleporter> list=worldObj.getEntities(EntityTeleporter.class, new Predicate<EntityTeleporter>(){

				@Override
				public boolean apply(EntityTeleporter input) {
					// TODO Auto-generated method stub
					return data != null && data.id==input.tpID;
				}
				
			});
			if(!list.isEmpty()){
				this.linkedTp=list.get(0);
				//System.out.println("linkedtpset");
			}
			return data;
		}
		return null;
	}
	public SoundEvent getSoundNameForState(int state){
		switch(state){
		case 0:return null;
		case 1:return this.getLevel()==1?TF2Sounds.MOB_TELEPORTER_SPIN_1:(this.getLevel()==2?TF2Sounds.MOB_TELEPORTER_SPIN_2:TF2Sounds.MOB_TELEPORTER_SPIN_3);
		default:return super.getSoundNameForState(state);
		}
	}
	public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
    {
		if(this.worldObj.isRemote &&player==this.getOwner()&&hand==EnumHand.MAIN_HAND){
			ClientProxy.showGuiTeleporter(this);
		}
		return false;
	}
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(TELEPORTS, 0);
		this.dataManager.register(TPPROGRESS, 0);
		this.dataManager.register(EXIT, false);
		this.dataManager.register(CHANNEL, (byte) 0);
	}
	public boolean isExit(){
		return this.dataManager.get(EXIT);
	}
	public void setExit(boolean exit){
		//if(this.getOwner()!=null&&exit&&teleporters.get(UUID.fromString(this.getOwnerId()))[this.getID()]!=null) return;
		this.dataManager.set(EXIT, exit);
		if(this.getOwnerId()!=null){
			this.updateTeleportersData(false);
		}
	}
	public int getID(){
		return (int) this.dataManager.get(CHANNEL);
	}
	public void setID(int id){
		if((id>=TP_PER_PLAYER||id<0)/*||teleporters.get(UUID.fromString(this.getOwnerId()))[id]!=null*/) return;
		if(this.getOwnerId()!=null){
			this.updateTeleportersData(true);
		}
		this.dataManager.set(CHANNEL, (byte) id);;
		if(this.getOwnerId()!=null){
			this.updateTeleportersData(false);
		}
	}
	public int getTPprogress(){
		return this.isDisabled()?20:this.dataManager.get(TPPROGRESS);
	}
	public void setTPprogress(int progress){
		this.dataManager.set(TPPROGRESS, progress);
	}
	public void setTeleports(int amount) {
		// TODO Auto-generated method stub
		this.dataManager.set(TELEPORTS, amount);
	}
	public int getTeleports() {
		// TODO Auto-generated method stub
		return this.dataManager.get(TELEPORTS);
	}
	/*public void setOwner(EntityLivingBase owner) {
		super.setOwner(owner);
		if(owner instanceof EntityPlayer){
			this.dataManager.set(key, value);14, owner.getUniqueID().toString());
		}
	}*/
	public void upgrade(){
		super.upgrade();
	}
	protected SoundEvent getHurtSound()
    {
        return null;
    }
	public void setDead(){
		//Chunk chunk=this.worldObj.getChunkFromBlockCoords(this.getPosition());
		//if(chunk.isLoaded()){
		if(!this.worldObj.isRemote)
			this.updateTeleportersData(true);
		//}
		//System.out.println("teleporter removed: "+chunk.isLoaded()+" "+chunk.isEmpty()+" "+chunk.isPopulated());
		super.setDead();
	}
	
	public void notifyDataManagerChange(DataParameter<?> key){
		super.notifyDataManagerChange(key);
		if(!this.worldObj.isRemote){
			if(key==OWNER_UUID){
				UUID id=this.getOwnerId();
				if(!teleporters.containsKey(id)){
					teleporters.put(id, new TeleporterData[TP_PER_PLAYER]);
				}
			}
		}
	}
	public void updateTeleportersData(boolean forceremove){
		if(this.worldObj.isRemote) return;
		
		UUID id=this.getOwnerId();
		if(!forceremove&&this.isExit()/*&&teleporters.get(id)[this.getID()]==null*/){
			teleporters.get(id)[this.getID()]=new TeleporterData(this.getPosition(),this.tpID,this.dimension);
		}else if(this.isExit()||(!this.isExit()&&this.getPosition().equals(teleporters.get(id)[this.getID()]))){
			new Exception().printStackTrace();
			teleporters.get(id)[this.getID()]=null;
		}
	}
    protected SoundEvent getDeathSound()
    {
        return TF2Sounds.MOB_TELEPORTER_DEATH;
    }
    public float getCollHeight(){
		return 0.2f;
	}
	public float getCollWidth(){
		return 1f;
	}
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setByte("TeleExitID", (byte) this.getID());
        par1NBTTagCompound.setBoolean("TeleExit", this.isExit());
        par1NBTTagCompound.setInteger("TeleID", this.tpID);
        par1NBTTagCompound.setShort("Teleports", (short) this.getTeleports());
    }
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.tpID=par1NBTTagCompound.getInteger("TeleID");
        this.setTeleports(par1NBTTagCompound.getShort("Teleports"));
        this.setID(par1NBTTagCompound.getByte("TeleExitID"));
        this.setExit(par1NBTTagCompound.getBoolean("TeleExit"));
    }
	
	public static class TeleporterData extends BlockPos{

		public final int dimension;
		public final int id;
		public TeleporterData(BlockPos blockPos,int id,int dimension) {
			super(blockPos);
			this.id=id;
			this.dimension=dimension;
		}
	}
	
}
