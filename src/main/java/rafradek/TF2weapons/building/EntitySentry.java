package rafradek.TF2weapons.building;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.ai.EntityAINearestChecked;
import rafradek.TF2weapons.characters.ai.EntityAISentryAttack;
import rafradek.TF2weapons.characters.ai.EntityAISentryIdle;
import rafradek.TF2weapons.characters.ai.EntityAISentryOwnerHurt;
import rafradek.TF2weapons.projectiles.EntityProjectileBase;

public class EntitySentry extends EntityBuilding {

	public ItemStack sentryBullet=ItemFromData.getNewStack("sentrybullet");
	public ItemStack sentryBullet2=ItemFromData.getNewStack("sentrybullet2");
	public ItemStack sentryRocket=ItemFromData.getNewStack("sentryrocket");
	public float rotationDefault=0;
	public int attackDelay;
	public int attackDelayRocket;
	public boolean shootRocket;
	public boolean shootBullet;
	private static final DataParameter<Integer> AMMO = EntityDataManager.createKey(EntitySentry.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ROCKET = EntityDataManager.createKey(EntitySentry.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> KILLS = EntityDataManager.createKey(EntitySentry.class, DataSerializers.VARINT);
	private static final DataParameter<Boolean> CONTROLLED = EntityDataManager.createKey(EntitySentry.class, DataSerializers.BOOLEAN);
	
	public EntitySentry(World worldIn) {
		super(worldIn);
		this.setSize(0.8f, 0.8f);
	}
	public EntitySentry(World worldIn, EntityLivingBase owner) {
		super(worldIn,owner);
		this.setSize(0.8f, 0.8f);
	}
	public void adjustSize(){
		if(this.getLevel()==1){
			this.setSize(0.8f, 0.8f);
		}
		else if(this.getLevel()==2){
			this.setSize(1f, 1f);
		}
		else if(this.getLevel()==3){
			this.setSize(1.2f, 1.2f);
		}
	}
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(this.isControlled()){
        	amount*=0.5f;
        }
        return super.attackEntityFrom(source, amount);
    }
	public SoundEvent getSoundNameForState(int state){
		switch(state){
		case 0:return this.getLevel()==1?TF2Sounds.MOB_SENTRY_SCAN_1:(this.getLevel()==2?TF2Sounds.MOB_SENTRY_SCAN_2:TF2Sounds.MOB_SENTRY_SCAN_3);
		//case 2:return TF2weapons.MOD_ID+":mob.sentry.shoot."+this.getLevel();
		case 3:return TF2Sounds.MOB_SENTRY_EMPTY;
		default:return super.getSoundNameForState(state);
		}
	}
	public void setAttackTarget(EntityLivingBase target){
		if(TF2weapons.isOnSameTeam(this.getOwner(), target)){
			return;
		}
		if(target!=this.getAttackTarget()){
			this.playSound(TF2Sounds.MOB_SENTRY_SPOT, 1.5f, 1f);
		}
		super.setAttackTarget(target);
	}
	public void applyTasks(){
		
		this.targetTasks.addTask(1, new EntityAISentryOwnerHurt(this,true));
		this.targetTasks.addTask(2, new EntityAINearestChecked(this, EntityLivingBase.class, true,false,new Predicate<EntityLivingBase>(){
			public boolean apply(EntityLivingBase target)
	        {
				return ((((TF2weapons.sentryAttacksPlayers && getOwnerId()!=null)&&target instanceof EntityPlayer) || target.getTeam()!=null || (TF2weapons.sentryAttacksMobs&&target instanceof IMob&& getOwnerId()!=null) ||(target instanceof EntityLiving && ((EntityLiving)target).getAttackTarget()==getOwner()))&&!TF2weapons.isOnSameTeam(EntitySentry.this, target))&&(!(target instanceof EntityTF2Character&&TF2weapons.naturalCheck.equals("Never")) ||!((EntityTF2Character)target).natural);
				
	        }
		}, false));
		this.tasks.addTask(1, new EntityAISentryAttack(this));
		this.tasks.addTask(2, new EntityAISentryIdle(this));
	}
	public void onLivingUpdate()
	{
		
		if(this.rotationDefault==0){
			this.rotationDefault=this.rotationYawHead;
		}
		if(this.attackDelay>0){
			this.attackDelay--;
		}
		if(this.attackDelayRocket>0){
			this.attackDelayRocket--;
		}
		this.ignoreFrustumCheck=this.isControlled();
		if(this.isControlled()){
			Vec3d lookVec=this.getOwner().getLookVec().scale(200);
			List<RayTraceResult> trace=TF2weapons.pierce(worldObj, this.getOwner(), this.getOwner().posX, this.getOwner().posY+this.getOwner().getEyeHeight(), this.getOwner().posZ, this.getOwner().posX+lookVec.xCoord, this.getOwner().posY+this.getOwner().getEyeHeight()+lookVec.yCoord, this.getOwner().posZ+lookVec.zCoord, false, 0.02f, false);
			this.getLookHelper().setLookPosition(trace.get(0).hitVec.xCoord,trace.get(0).hitVec.yCoord,trace.get(0).hitVec.zCoord,30, 75);
		}
		if(this.getAttackTarget()!=null&&!this.getAttackTarget().isEntityAlive()){
			this.setAttackTarget(null);
		}
        super.onLivingUpdate();
	}
	public ItemStack getHeldItem(EnumHand hand){
		return sentryRocket;
	}
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        //this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValu(1.6D);
    }
	protected void entityInit()
	{
		super.entityInit();
		this.dataManager.register(AMMO, 150);
		this.dataManager.register(ROCKET, 20);
		this.dataManager.register(KILLS, 0);
		this.dataManager.register(CONTROLLED, Boolean.valueOf(false));
	}

	public void shootRocket(EntityLivingBase owner){
		while(this.getLevel()==3&&this.getRocketAmmo()>0&&this.attackDelayRocket<=0){
    		this.attackDelayRocket+=60;
    		if(this.isControlled()){
    			this.attackDelayRocket*=0.75f;
    		}
    		try {
    			//System.out.println(owner);
    			this.playSound(TF2Sounds.MOB_SENTRY_ROCKET, 1.5f, 1f);
				EntityProjectileBase proj=MapList.projectileClasses.get(ItemFromData.getData(this.sentryRocket).getString(PropertyType.PROJECTILE)).getConstructor(World.class,EntityLivingBase.class,EnumHand.class).newInstance(this.worldObj,this,EnumHand.MAIN_HAND);
				proj.shootingEntity=owner;
				proj.usedWeapon=sentryRocket;
				proj.sentry=this;
				this.worldObj.spawnEntityInWorld(proj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		this.setRocketAmmo(this.getRocketAmmo()-1);
    		/*RayTraceResult bullet=TF2weapons.pierce(this.host.worldObj, this.host, this.host.posX, this.host.posY+this.host.height/2, this.host.posZ, this.target.posX, this.target.posY+this.target.height/2, this.target.posZ,false, 0.08f);
    		if(bullet.entityHit!=null){
    			DamageSource src=TF2weapons.causeBulletDamage("Sentry Gun", this.host.getOwner(), 0);
    			TF2weapons.dealDamage(bullet.entityHit, this.host.worldObj, this.host.owner, null, 0, 1.6f, src);
    			Vec3d dist=new Vec3d(this.host.posX-bullet.entityHit.posX,this.host.posY-bullet.entityHit.posY,this.host.posZ-bullet.entityHit.posZ).normalize();
    			bullet.entityHit.addVelocity(dist.xCoord,dist.yCoord, dist.zCoord);
    		}*/
    		
    	}
	}
	public void shootBullet(EntityLivingBase owner){
		this.setSoundState(this.getAmmo()>0?2:3);
    	Vec3d attackPos=this.isControlled()?new Vec3d(this.getLookHelper().getLookPosX(),this.getLookHelper().getLookPosY(),this.getLookHelper().getLookPosZ()):this.getAttackTarget().getPositionVector().addVector(0,this.getAttackTarget().getEyeHeight(),0);
    	while(this.attackDelay<=0&&this.getAmmo()>0){
    		this.attackDelay+=this.getLevel()>1?2.5f:5f;
    		if(this.isControlled()){
    			this.attackDelay/=2;
    		}
    		this.playSound(this.getLevel()==1?TF2Sounds.MOB_SENTRY_SHOOT_1:TF2Sounds.MOB_SENTRY_SHOOT_2, 1.5f, 1f);
    		List<RayTraceResult> list=TF2weapons.pierce(this.worldObj, this, this.posX, this.posY+this.getEyeHeight(), this.posZ, attackPos.xCoord, attackPos.yCoord, attackPos.zCoord,false, 0.08f,false);
    		for(RayTraceResult bullet:list){
    			if(bullet.entityHit!=null){

        			DamageSource src=TF2weapons.causeDirectDamage(sentryBullet, owner, 0).setProjectile();
        			
        			if(TF2weapons.dealDamage(bullet.entityHit, this.worldObj, owner, this.sentryBullet, TF2weapons.calculateCritsPost(bullet.entityHit, null, 0, null), 1.6f, src)){
	        			Vec3d dist=new Vec3d(bullet.entityHit.posX-this.posX,bullet.entityHit.posY-this.posY,bullet.entityHit.posZ-this.posZ).normalize();
	        			bullet.entityHit.addVelocity(dist.xCoord*0.35,dist.yCoord*0.35, dist.zCoord*0.35);
	        			if(bullet.entityHit instanceof EntityLivingBase){
	        				((EntityLivingBase)bullet.entityHit).setLastAttacker(this);
	        				((EntityLivingBase)bullet.entityHit).setRevengeTarget(this);
	        				if(!bullet.entityHit.isEntityAlive())
	        					this.setKills(this.getKills()+1);
	        			}
	        			
    			}
    			}
    			
    		}
    		this.setAmmo(this.getAmmo()-1);
    	}
	}
	public boolean canEntityBeSeen(Entity entityIn)
    {
		return this.worldObj.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ),false,true,false) == null;
    }
	public int getMaxAmmo(){
		return this.getLevel()==1?150:200;
	}
	
	public int getAmmo(){
		return this.dataManager.get(AMMO);
	}
	
	public int getKills(){
		return this.dataManager.get(KILLS);
	}
	
	public int getRocketAmmo(){
		return this.dataManager.get(ROCKET);
	}
	
	public void setAmmo(int ammo){
		this.dataManager.set(AMMO,ammo);
	}
	
	public void setRocketAmmo(int ammo){
		this.dataManager.set(ROCKET,ammo);
	}
	public void setKills(int kills){
		this.dataManager.set(KILLS,kills);
	}
	public void setControlled(boolean control){
		this.dataManager.set(CONTROLLED,control);
	}
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeEntityToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setShort("Ammo", (short) this.getAmmo());
        par1NBTTagCompound.setShort("RocketAmmo", (short) this.getRocketAmmo());
        par1NBTTagCompound.setShort("Kills", (short) this.getKills());
    }
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readEntityFromNBT(par1NBTTagCompound);
        
        this.setAmmo(par1NBTTagCompound.getShort("Ammo"));
        this.setRocketAmmo(par1NBTTagCompound.getShort("RocketAmmo"));
        this.setKills(par1NBTTagCompound.getShort("Kills"));
    }
	public float getCollHeight(){
		return 1.2f;
	}
	public float getCollWidth(){
		return 1.12f;
	}
	public float getEyeHeight(){
		return this.height/2+0.2f;
	}
	protected SoundEvent getHurtSound()
    {
        return this.isSapped()?null:TF2Sounds.MOB_SENTRY_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return TF2Sounds.MOB_SENTRY_DEATH;
    }
    
    
    public boolean canUseWrench(){
		return super.canUseWrench()||this.getAmmo()<this.getMaxAmmo()||this.getRocketAmmo()<20;
	}
    public void upgrade(){
    	super.upgrade();
    	this.setAmmo(200);
    }
	public boolean isControlled() {
		// TODO Auto-generated method stub
		return this.isEntityAlive() && this.dataManager.get(CONTROLLED);//this.getOwner() != null && this.getOwner() instanceof EntityPlayer && this.getOwner().getCapability(TF2weapons.WEAPONS_CAP, null).controlledSentry==this;
	}
}
