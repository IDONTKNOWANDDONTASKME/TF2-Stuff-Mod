package rafradek.TF2weapons.projectiles;

import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import atomicstryker.dynamiclights.client.DynamicLights;
import atomicstryker.dynamiclights.client.IDynamicLightSource;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.weapons.ItemProjectileWeapon;
import rafradek.TF2weapons.weapons.TF2Explosion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Optional.Interface(iface="atomicstryker.dynamiclights.client.IDynamicLightSource.class", modid="DynamicLights", striprefs=true)
public abstract class EntityProjectileBase extends Entity implements IProjectile, IThrowableEntity, IDynamicLightSource 
{
	public ArrayList<Entity> hitEntities=new ArrayList<Entity>();
    //private Block field_145790_g;
    /** Seems to be some sort of timer for animating an arrow. */
    /** The owner of this arrow. */
    public EntityLivingBase shootingEntity;
    public ItemStack usedWeapon;
    public double gravity=0.05d;
	public float distanceTravelled;
	public BlockPos stickedBlock;
	public EntitySentry sentry;
   
	private static final DataParameter<Byte> CRITICAL = EntityDataManager.createKey(EntityProjectileBase.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> STICK = EntityDataManager.createKey(EntityProjectileBase.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Rotations> STICK_POS = EntityDataManager.createKey(EntityProjectileBase.class, DataSerializers.ROTATIONS);
	
    public EntityProjectileBase(World p_i1753_1_)
    {
        super(p_i1753_1_);
        this.setSize(0.5F, 0.5F);
    }
    
    public EntityProjectileBase(World world, EntityLivingBase shooter, EnumHand hand)
    {
        this(world);
        this.shootingEntity = shooter;
        this.usedWeapon=shooter.getHeldItem(hand).copy();
        this.setLocationAndAngles(shooter.posX, shooter.posY + (double)shooter.getEyeHeight(), shooter.posZ, shooter.rotationYawHead, shooter.rotationPitch+this.getPitchAddition());
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F*(hand==EnumHand.MAIN_HAND?1:-1));
        this.posY -= shooter instanceof EntitySentry?-0.1D:0.1D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F*(hand==EnumHand.MAIN_HAND?1:-1));
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (double)(-MathHelper.sin(this.rotationYaw/ 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, ((ItemProjectileWeapon)this.usedWeapon.getItem()).getProjectileSpeed(usedWeapon, shooter), ((ItemProjectileWeapon)this.usedWeapon.getItem()).getWeaponSpread(usedWeapon, shooter));
    }
    protected void entityInit()
    {
    	this.dataManager.register(CRITICAL, (byte)0);
    	this.dataManager.register(STICK, false);
    	this.dataManager.register(STICK_POS, new Rotations(0f, 0f, 0f));
    }
    public float getPitchAddition(){
    	return 0;
    }
    public boolean isImmuneToExplosions(){
		return true;
	}
    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void setThrowableHeading(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_)
    {
        float f2 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
        //System.out.println("motion: "+p_70186_1_+" "+p_70186_3_+" "+p_70186_5_+" "+f2);
        p_70186_1_ /= (double)f2;
        p_70186_3_ /= (double)f2;
        p_70186_5_ /= (double)f2;
        p_70186_1_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
        p_70186_3_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
        p_70186_5_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
        p_70186_1_ *= (double)p_70186_7_;
        p_70186_3_ *= (double)p_70186_7_;
        p_70186_5_ *= (double)p_70186_7_;
        this.motionX = p_70186_1_;
        this.motionY = p_70186_3_;
        this.motionZ = p_70186_5_;
        float f3 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70186_1_, p_70186_5_) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70186_3_, (double)f3) * 180.0D / Math.PI);
    }

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    /*@SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_)
    {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }*/

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    //@SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_)
    {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70016_3_, (double)f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        }
    }
    
    public void explode(double x, double y, double z,Entity direct,float damageMult){
		if(worldObj.isRemote||this.shootingEntity==null) return;
		this.setDead();
		float distance= (float) new Vec3d(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ).distanceTo(new Vec3d(x, y, z));
		float blockDmg=TF2Attribute.getModifier("Destroy Block", this.usedWeapon, 0, (EntityLivingBase) this.shootingEntity)*TF2weapons.calculateDamage(TF2weapons.dummyEnt,worldObj, (EntityLivingBase) this.shootingEntity, usedWeapon, this.getCritical(), distance);
		TF2Explosion explosion = new TF2Explosion(this.worldObj, this, x,y,z, this.getExplosionSize()*TF2Attribute.getModifier("Explosion Radius", this.usedWeapon, 1, (EntityLivingBase) this.shootingEntity),direct,blockDmg);
		//System.out.println("ticks: "+this.ticksExisted);
        explosion.isFlaming = false;
        explosion.isSmoking = true;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        Iterator<Entity> affectedIterator=explosion.affectedEntities.keySet().iterator();
        while(affectedIterator.hasNext()){
        	Entity ent=affectedIterator.next();
        	int critical=TF2weapons.calculateCritsPost(ent, shootingEntity, this.getCritical(), this.usedWeapon);
        	float dmg=TF2weapons.calculateDamage(ent,worldObj, (EntityLivingBase) this.shootingEntity, usedWeapon, critical, distance)*damageMult;
        	TF2weapons.dealDamage(ent, this.worldObj, (EntityLivingBase) this.shootingEntity, this.usedWeapon, critical, explosion.affectedEntities.get(ent)*dmg, TF2weapons.causeBulletDamage(this.usedWeapon,(EntityLivingBase) this.shootingEntity,critical, this).setExplosion());
        	if(this.sentry !=null&&ent instanceof EntityLivingBase){
        		((EntityLivingBase)ent).setLastAttacker(this.sentry);
        		((EntityLivingBase)ent).setRevengeTarget(this.sentry);
        		if(!ent.isEntityAlive())
        			this.sentry.setKills(this.sentry.getKills()+1);
        	}
        }
        Iterator<EntityPlayer> iterator = this.worldObj.playerEntities.iterator();

        while (iterator.hasNext())
        {
            EntityPlayer entityplayer = (EntityPlayer)iterator.next();

            if (entityplayer.getDistanceSq(x, y, z) < 4096.0D)
            {
                ((EntityPlayerMP)entityplayer).connection.sendPacket(new SPacketExplosion(x, y, z,this.getExplosionSize(), explosion.affectedBlockPositions, (Vec3d)explosion.func_77277_b().get(entityplayer)));
            }
        }
	}
    public void travelToDimension(int dimensionId)
    {
    	
    }
    public float getExplosionSize(){
    	return 2.74f;
    }
    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {
    	if (this.ticksExisted>this.getMaxTime()){
        	this.setDead();
        	return;
        }
    	
        super.onUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }

        /*BlockPos blockpos = new BlockPos(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(this.worldObj, blockpos);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBox(this.worldObj, blockpos, iblockstate);

            if (axisalignedbb != null && axisalignedbb.isVecInside(new Vec3d(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }*/
        
        
        Vec3d Vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d Vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult RayTraceResult = this.worldObj.rayTraceBlocks(Vec3d1, Vec3d, false, true, false);
        Vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

        if (RayTraceResult != null)
        {
            Vec3d = new Vec3d(RayTraceResult.hitVec.xCoord, RayTraceResult.hitVec.yCoord, RayTraceResult.hitVec.zCoord);
        }

        Entity entity = null;
        Vec3d result=null;
        List<?> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
        double d0 = 0.0D;
        int i;
        float f1;

        for (i = 0; i < list.size(); ++i)
        {
            Entity entity1 = (Entity)list.get(i);

            if (entity1.canBeCollidedWith() && entity1.isEntityAlive() && entity1 != this.sentry&&/*TF2weapons.canHit(shootingEntity, entity1)*/ entity1 != this.shootingEntity)
            {
                f1 = this.getCollisionSize();
                AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
                RayTraceResult RayTraceResult1 = axisalignedbb1.calculateIntercept(Vec3d1, Vec3d);

                if (RayTraceResult1 != null)
                {
                    double d1 = Vec3d1.distanceTo(RayTraceResult1.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        entity = entity1;
                        d0 = d1;
                        result=RayTraceResult1.hitVec;
                    }
                }
            }
        }

        if (entity != null)
        {
            RayTraceResult = new RayTraceResult(entity,result);
        }

        if (RayTraceResult != null && RayTraceResult.entityHit != null && RayTraceResult.entityHit instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)RayTraceResult.entityHit;

            if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
            {
                RayTraceResult = null;
            }
        }

        float f2;
        if (RayTraceResult != null)
        {
            if (RayTraceResult.entityHit != null)
            {
            	this.onHitMob(RayTraceResult.entityHit, RayTraceResult);
         
            }
            else if(!this.useCollisionBox())
            {
            	int attr=this.worldObj.isRemote?0:(int) TF2Attribute.getModifier("Coll Remove", this.usedWeapon, 0, (EntityLivingBase) this.shootingEntity);
            	if(attr==0){
	            	BlockPos blpos=RayTraceResult.getBlockPos();
	            	this.onHitGround(blpos.getX(), blpos.getY(), blpos.getZ(), RayTraceResult);
            	}
            	else if(attr==2){
            		this.explode(RayTraceResult.hitVec.xCoord, RayTraceResult.hitVec.yCoord, RayTraceResult.hitVec.zCoord, null, 1f);
            	}
            	else{
            		this.setDead();
            	}
            }
        }

        /*if (this.getIsCritical())
        {
            for (i = 0; i < 4; ++i)
            {
                this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
            }
        }*/
        /*if(!this.worldObj.isRemote&&this.isSticked()&&(this.stickedBlock==null||this.worldObj.isAirBlock(this.stickedBlock))){
        	this.setSticked(false);
        	this.stickedBlock=null;
        }*/
        if(this.isSticked()){
        	this.setPosition(this.dataManager.get(STICK_POS).getX(),this.dataManager.get(STICK_POS).getY(),this.dataManager.get(STICK_POS).getZ());
        	if(!this.worldObj.isRemote && this.ticksExisted%5==0 && this.worldObj.getCollisionBoxes(this, this.getEntityBoundingBox().expand(0.1f, 0.1f, 0.1f)).isEmpty()){
        		this.setSticked(false);
        	}
        }
        if(this.moveable()){
	        if(!this.useCollisionBox()){
	        	this.posX += this.motionX;
	            this.posY += this.motionY;
	            this.posZ += this.motionZ;
	        }
	        else {
	        	//this.setPosition(this.posX, this.posY, this.posZ);
	        	this.moveEntity(this.motionX, this.motionY, this.motionZ);
	        }
	        float f3 = (float) (1-this.getGravity()/5);
        	this.motionX *= (double)f3;
            this.motionY *= (double)f3;
            this.motionZ *= (double)f3;
            this.motionY -= this.getGravity();
	        f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
	        if(f2>0.1||Math.abs(this.motionY)>this.getGravity()*3){
	            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
	
	            for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
	            {
	                ;
	            }
	
	            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
	            {
	                this.prevRotationPitch += 360.0F;
	            }
	
	            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
	            {
	                this.prevRotationYaw -= 360.0F;
	            }
	
	            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
	            {
	                this.prevRotationYaw += 360.0F;
	            }
	
	            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
	            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
	        }
        }
        if(this.worldObj.isRemote){
			for (int j = 0; j < 4; ++j)
            {
				double pX=this.posX - this.motionX * (double)j / 4.0D - this.motionX;
				double pY=this.posY + this.height/2 - this.motionY * (double)j / 4.0D- this.motionY;
				double pZ=this.posZ - this.motionZ * (double)j / 4.0D- this.motionZ;
				if(this.getCritical()==2){
					ClientProxy.spawnCritParticle(this.worldObj, pX, pY, pZ,TF2weapons.getTeamForDisplay((EntityLivingBase) this.shootingEntity));
				}
				this.spawnParticles(pX, pY, pZ);
				//EntityFX ent=Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), pX, pY, pZ, 0, 0, 0);
                
            }
			//ClientProxy.spawnParticle(this.worldObj,new EntityRocketEffect(worldObj, this.posX, this.posY, this.posZ));
		}
        
        if (this.isWet())
        {
            this.extinguish();
        }

        
        
        if(!this.useCollisionBox()){
	        this.setPosition(this.posX, this.posY, this.posZ);
	        this.doBlockCollisions();
        }
        /*if(!this.worldObj.isRemote&&MinecraftServer.getServer().isSinglePlayer()&&Minecraft.getMinecraft().thePlayer!=null){
        	Entity fentity=Minecraft.getMinecraft().theWorld.getEntityByID(this.getEntityId());
        	if(fentity!=null){
            	fentity.setVelocity(this.motionX,this.motionY,this.motionZ);
            	fentity.setPosition(this.posX, this.posY, this.posZ);
            	fentity.prevPosX=this.prevPosX;
            	fentity.prevPosY=this.prevPosY;
            	fentity.prevPosZ=this.prevPosZ;
        	}
        }*/
    }
    
    //@SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_)
    {
    	if(this.moveable()){
    		super.setPositionAndRotationDirect(p_180426_1_, p_180426_3_, p_180426_5_, p_180426_7_, p_180426_8_, p_180426_9_, p_180426_10_);
    	}
    }
    
    
    
   /* public void setPosition(double x, double y, double z)
    {
	    this.posX = x;
	    this.posY = y;
	    this.posZ = z;
	    float f = this.width / 2f;
	    float f1 = this.height /2f;
	    this.setEntityBoundingBox(new AxisAlignedBB(x - (double)f, y-(double)f1, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }*/
    
    public void moveEntity(double x, double y, double z)
    {
        if (this.noClip)
        {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        }
        else
        {
            this.worldObj.theProfiler.startSection("move");
            //double d3 = this.posX;
           // double d4 = this.posY;
            //double d5 = this.posZ;

            if (this.isInWeb)
            {
                this.isInWeb = false;
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
            }

            double d3 = x;
            double d4 = y;
            double d5 = z;
            
            List<AxisAlignedBB> list1 = this.worldObj.getCollisionBoxes(this, this.getEntityBoundingBox().addCoord(x, y, z));
            AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            int i = 0;

            
            for (int j = list1.size(); i < j; ++i)
            {
                y = ((AxisAlignedBB)list1.get(i)).calculateYOffset(this.getEntityBoundingBox(), y);
            }

            double limit=y/d4;
            if(!this.isSticky()){
            	this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            }
            boolean i_ = this.onGround || d4 != y && d4 < 0.0D;
            int j4 = 0;

            for (int k = list1.size(); j4 < k; ++j4)
            {
                x = ((AxisAlignedBB)list1.get(j4)).calculateXOffset(this.getEntityBoundingBox(), x);
            }

            if(this.isSticky()){
	            if(x/d3<limit){
	            	limit=x/d3;
	            }
            }
            else{
            	this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
            }
            j4 = 0;

            for (int k4 = list1.size(); j4 < k4; ++j4)
            {
                z = ((AxisAlignedBB)list1.get(j4)).calculateZOffset(this.getEntityBoundingBox(), z);
            }

            if(this.isSticky()){
        		if(z/d5<limit){
	            	limit=z/d5;
	            }
            	this.setEntityBoundingBox(this.getEntityBoundingBox().offset(d3*limit,d4*limit,d5*limit));
            }
            else{
            	this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));
            }

            if (this.stepHeight > 0.0F && i_ && (d3 != x || d5 != z))
            {
                double d11 = x;
                double d7 = y;
                double d8 = z;
                AxisAlignedBB axisalignedbb1 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                y = (double)this.stepHeight;
                List<AxisAlignedBB> list = this.worldObj.getCollisionBoxes(this, this.getEntityBoundingBox().addCoord(d3, y, d5));
                AxisAlignedBB axisalignedbb2 = this.getEntityBoundingBox();
                AxisAlignedBB axisalignedbb3 = axisalignedbb2.addCoord(d3, 0.0D, d5);
                double d9 = y;
                int l = 0;

                for (int i1 = list.size(); l < i1; ++l)
                {
                    d9 = ((AxisAlignedBB)list.get(l)).calculateYOffset(axisalignedbb3, d9);
                }

                axisalignedbb2 = axisalignedbb2.offset(0.0D, d9, 0.0D);
                double d15 = d3;
                int j1 = 0;

                for (int k1 = list.size(); j1 < k1; ++j1)
                {
                    d15 = ((AxisAlignedBB)list.get(j1)).calculateXOffset(axisalignedbb2, d15);
                }

                axisalignedbb2 = axisalignedbb2.offset(d15, 0.0D, 0.0D);
                double d16 = d5;
                int l1 = 0;

                for (int i2 = list.size(); l1 < i2; ++l1)
                {
                    d16 = ((AxisAlignedBB)list.get(l1)).calculateZOffset(axisalignedbb2, d16);
                }

                axisalignedbb2 = axisalignedbb2.offset(0.0D, 0.0D, d16);
                AxisAlignedBB axisalignedbb4 = this.getEntityBoundingBox();
                double d17 = y;
                int j2 = 0;

                for (int k2 = list.size(); j2 < k2; ++j2)
                {
                    d17 = ((AxisAlignedBB)list.get(j2)).calculateYOffset(axisalignedbb4, d17);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, d17, 0.0D);
                double d18 = d3;
                int l2 = 0;

                for (int i3 = list.size(); l2 < i3; ++l2)
                {
                    d18 = ((AxisAlignedBB)list.get(l2)).calculateXOffset(axisalignedbb4, d18);
                }

                axisalignedbb4 = axisalignedbb4.offset(d18, 0.0D, 0.0D);
                double d19 = d5;
                int j3 = 0;

                for (int k3 = list.size(); j3 < k3; ++j3)
                {
                    d19 = ((AxisAlignedBB)list.get(j3)).calculateZOffset(axisalignedbb4, d19);
                }

                axisalignedbb4 = axisalignedbb4.offset(0.0D, 0.0D, d19);
                double d20 = d15 * d15 + d16 * d16;
                double d10 = d18 * d18 + d19 * d19;

                if (d20 > d10)
                {
                    x = d15;
                    z = d16;
                    y = -d9;
                    this.setEntityBoundingBox(axisalignedbb2);
                }
                else
                {
                    x = d18;
                    z = d19;
                    y = -d17;
                    this.setEntityBoundingBox(axisalignedbb4);
                }

                int l3 = 0;

                for (int i4 = list.size(); l3 < i4; ++l3)
                {
                    y = ((AxisAlignedBB)list.get(l3)).calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

                if (d11 * d11 + d8 * d8 >= x * x + z * z)
                {
                    x = d11;
                    y = d7;
                    z = d8;
                    this.setEntityBoundingBox(axisalignedbb1);
                }
            }

            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.resetPositionToBB();
            this.isCollidedHorizontally = d3 != x || d5 != z;
            this.isCollidedVertically = d4 != y;
            this.onGround = this.isCollidedVertically && d4 < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
            if(this.isSticky() && !this.worldObj.isRemote && this.isCollided){
            	this.setSticked(true);
            }
            j4 = MathHelper.floor_double(this.posX);
            int l4 = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            int i5 = MathHelper.floor_double(this.posZ);
            BlockPos blockpos = new BlockPos(j4, l4, i5);
            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

            if (iblockstate.getMaterial() == Material.AIR)
            {
                BlockPos blockpos1 = blockpos.down();
                IBlockState iblockstate1 = this.worldObj.getBlockState(blockpos1);
                Block block1 = iblockstate1.getBlock();

                if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate)
                {
                    iblockstate = iblockstate1;
                    blockpos = blockpos1;
                }
            }
            this.stickedBlock=blockpos;
            this.updateFallState(y, this.onGround, iblockstate, blockpos);
            /*double limit=y/d7;
            if(!this.isSticky()){
            	this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            }
            if(this.isSticky()){
	            if(x/d6<limit){
	            	limit=x/d6;
	            }
            }
            if(this.isSticky()){
        		if(z/d8<limit){
	            	limit=z/d8;
	            }
            	this.setEntityBoundingBox(this.getEntityBoundingBox().offset(d6*limit,d7*limit,d8*limit));
            }*/

            if (d3 != x)
            {
            	this.onHitBlockX();
            }

            if (d5 != z)
            {
            	this.onHitBlockZ();
            }
            Block block = iblockstate.getBlock();
            if (d4 != y)
            {
            	this.onHitBlockY(block);
            }

            if (this.canTriggerWalking() && this.getRidingEntity() == null)
            {
                /*double d15 = this.posX - d3;
                double d16 = this.posY - d4;
                double d17 = this.posZ - d5;

                if (block1 != Blocks.ladder)
                {
                    d16 = 0.0D;
                }
				*/
                if (block != null && this.onGround)
                {
                    block.onEntityCollidedWithBlock(this.worldObj, blockpos, iblockstate, this);
                }
            }

            try
            {
                this.doBlockCollisions();
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }

            this.worldObj.theProfiler.endSection();
        }
    }
    /*public void resetPositionToBB()
    {
        AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
        this.posX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.posY = (axisalignedbb.minY + axisalignedbb.maxY) / 2.0D;
        this.posZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
    }*/
    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_)
    {

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_)
    {
    	
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    public abstract void onHitGround(int x, int y, int z, RayTraceResult mop);
    
    public abstract void onHitMob(Entity entityHit, RayTraceResult mop);
    
    protected boolean canTriggerWalking()
    {
        return false;
    }

    //@SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    public boolean moveable(){
    	return !this.isSticked();
    }
    /**
     * Sets the amount of knockback the arrow applies when it hits a mob.
     */
    public void setKnockbackStrength(int p_70240_1_)
    {
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem()
    {
        return false;
    }

    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public void setCritical(int critical)
    {
        this.dataManager.set(CRITICAL,(byte)critical);
    }
    
    public void setSticked(boolean stick)
    {
        this.dataManager.set(STICK,stick);
        this.dataManager.set(STICK_POS, new Rotations((float)this.posX, (float)this.posY, (float)this.posZ));
        /*if(!this.worldObj.isRemote){
        	EntityTracker tracker=((WorldServer)this.worldObj).getEntityTracker();
			tracker.sendToAllTrackingEntity(this, new S12PacketEntityVelocity(this));
			tracker.sendToAllTrackingEntity(this, new S18PacketEntityTeleport(this));
        }*/
    }
    /**
     * Whether the arrow has a stream of critical hit particles flying behind it.
     */
    public int getCritical()
    {
    	return (int) this.dataManager.get(CRITICAL);
    }
    
    public boolean isSticked()
    {
        return this.dataManager.get(STICK);
    }
    
    protected float getSpeed()
    {
        return 3;
    }
    
    protected double getGravity()
    {
    	return 0.0381f;
    }

	@Override
	public Entity getThrower() {
		return this.shootingEntity;
	}

	@Override
	public void setThrower(Entity entity) {
		this.shootingEntity=(EntityLivingBase) entity;
		
	}
	public boolean isSticky(){
		return false;
	}
	public void onHitBlockX(){
		this.motionX=0;
	}
	public void onHitBlockY(Block block){
		block.onLanded(this.worldObj, this);
	}
	public void onHitBlockZ(){
		this.motionZ=0;
	}
	public abstract void spawnParticles(double x, double y, double z);
	
	public int getMaxTime(){
		return 1000;
	}
	
	public boolean writeToNBTOptional(NBTTagCompound tagCompund){
		return false;
	}
	
	public boolean useCollisionBox(){
		return false;
	}
	
	public float getCollisionSize(){
    	return 0.3f;
	}
	@SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength();

        if (Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 512.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }
	@Optional.Method(modid="DynamicLights")
    public void makeLit(){
		if(TF2weapons.dynamicLightsProj)
			DynamicLights.addLightSource(this);
    }
	@Optional.Method(modid="DynamicLights")
	@Override
	public Entity getAttachmentEntity() {
		// TODO Auto-generated method stub
		return this;
	}
	@Optional.Method(modid="DynamicLights")
	@Override
	public int getLightLevel() {
		// TODO Auto-generated method stub
		return 9;
	}
}