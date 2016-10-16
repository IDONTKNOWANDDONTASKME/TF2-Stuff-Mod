package rafradek.TF2weapons.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2EventBusListener.DestroyBlockEntry;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2ActionHandler;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.BulletMessage;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemBulletWeapon extends ItemWeapon {
	public static double lastStartX=90;
	public static double lastStartY=90;
	public static double lastStartZ=90;
	public static double lastEndX=990;
	public static double lastEndY=900;
	public static double lastEndZ=900;
	public static HashMap<Entity, float[]> lastShot= new HashMap<Entity, float[]>();
	public static ArrayList<RayTraceResult> lastShotClient= new ArrayList<RayTraceResult>();
	public static boolean processShotServer;
	public static EntityLivingBase dummyEnt=new EntityCreeper(null);
			
	public void handleShoot(EntityLivingBase living, ItemStack stack,World world, HashMap<Entity, float[]> map,int critical){
		DamageSource var22=TF2weapons.causeDirectDamage(stack, living, critical);
		
		if(!(this instanceof ItemMeleeWeapon))
			var22.setProjectile();
		
    	Iterator<Entity> iterator=map.keySet().iterator();
        while(iterator.hasNext()){
        	Entity entity=(Entity) iterator.next();
        	if(!((ItemWeapon)stack.getItem()).onHit(stack, living, entity)) continue;
        	float distance = (float) new Vec3d(living.posX, living.posY, living.posZ).distanceTo(new Vec3d(entity.posX, entity.posY, entity.posZ));
        	if(map.get(entity) != null && map.get(entity)[1] != 0&&TF2weapons.dealDamage(entity, world, living, stack, critical, map.get(entity)[1],var22)){
        		//System.out.println("Damage: "+map.get(entity)[1]);
            	distance=((ItemBulletWeapon) stack.getItem()).getMaxRange()/distance;
            	double distX=(living.posX-entity.posX)*distance;
            	double distY=(living.posY-entity.posY)*distance;
            	double distZ=(living.posZ-entity.posZ)*distance;
            	if(entity != null && !(entity instanceof EntityLivingBase &&((EntityLivingBase)entity).getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()>=1)&&stack != null && map.get(entity) != null){
            		double knockbackAmount=(double)((ItemBulletWeapon) stack.getItem()).getWeaponKnockback(stack,living)*map.get(entity)[1]* 0.01625D / ((ItemBulletWeapon) stack.getItem()).getMaxRange();
	            	if (knockbackAmount > 0)
	                {
	                    if (distance > 0.0F)
	                    {
	                        entity.addVelocity(-(distX * knockbackAmount),-(distY * knockbackAmount), -(distZ * knockbackAmount));
	                        entity.isAirBorne=-(distY * knockbackAmount) > 0.01D;
	                    }
	                }
            	}
        	}
        }
        map.clear();

	}
	
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand, PredictionMessage message)
    {
		if(world.isRemote&&living==ClientProxy.getLocalPlayer()){
			lastShotClient.clear();
		}
		super.use(stack, living, world, hand, message);
		if(world.isRemote&&living==ClientProxy.getLocalPlayer()){
			ClientProxy.getLocalPlayer().getCapability(TF2weapons.WEAPONS_CAP, null).recoil+=getData(stack).getFloat(PropertyType.RECOIL);
			message.target=lastShotClient;
			return true;
		}
		else if(!world.isRemote){
			if(living instanceof EntityPlayer){
				//System.out.println("Shoot: "+message.readData);
				if(message.readData==null){
					return false;
				}
				int totalCrit=TF2weapons.calculateCritPre(stack, living);
				HashMap<Entity, float[]> shotInfo= new HashMap<Entity, float[]>();
				for(Object[] obj:message.readData){
					Entity target=world.getEntityByID((Integer) obj[0]);
					if(target==null) continue;
					
					if(!shotInfo.containsKey(target)||shotInfo.get(target)==null){
			    		shotInfo.put(target, new float[3]);
			    	}
					int critical=totalCrit;
			    	//System.out.println(var4.hitInfo);
			    	if((Boolean)obj[1]){
			    		critical=2;
			    	}
			    	critical=this.setCritical(stack, living, target, critical);
			    	if(critical>totalCrit){
			    		totalCrit=critical;
			    	}
			    	//ItemRangedWeapon.critical=critical;
			    	float[] values=shotInfo.get(target);
			    	//System.out.println(obj[2]+" "+critical);
			    	values[0]++;
			    	values[1]+=TF2weapons.calculateDamage(target,world, living, stack, critical, (Float) obj[2]);
				}
				//living.getCapability(TF2weapons.WEAPONS_CAP, null).predictionList.add(message);
				handleShoot(living, stack, world, shotInfo,totalCrit);
			}
			else {
				handleShoot(living, stack, world, lastShot,critical);
				lastShot.clear();
			}
		}
		return true;
		//if(world.isRemote) return false;
		/*if(((!world.isRemote && (processShotServer||!(living instanceof EntityPlayer)))||(world.isRemote&&living instanceof EntityPlayer)) && super.use(stack, living, world, hand)){
			//System.out.println(world.isRemote+" "+stack.getTagCompound().getShort("reload")+" "+TF2ActionHandler.playerAction.get(world.isRemote).get(living));

            if(!world.isRemote && living != null&& !processShotServer)
            {
            	handleShoot(living, stack, world, lastShot,critical);
            }
            else if(world.isRemote&&living==Minecraft.getMinecraft().thePlayer){
            	
            	//TF2weapons.network.sendToServer(new BulletMessage(Minecraft.getMinecraft().thePlayer.inventory.currentItem,lastShotClient, hand));
            	//lastShotClient.clear();
            }
            
            return ;
		}*/
    }
	public boolean showTracer(ItemStack stack){
		return true;
	}
	@Override
	public void shoot(ItemStack stack, EntityLivingBase living, World world, int critical, EnumHand hand) {
		
		boolean removeBlocks=TF2Attribute.getModifier("Destroy Block", stack, 0, living)>0;
		if(!world.isRemote && living instanceof EntityPlayer && !removeBlocks) return;
		double startX=0;
		double startY=0;
		double startZ=0;
		
		double endX=0;
		double endY=0;
		double endZ=0;
		
		double[] rand=TF2weapons.radiusRandom3D(this.getWeaponSpread(stack,living), world.rand);
		
		//if(target==null){
			startX=living.posX;// - (double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
			startY=living.posY + living.getEyeHeight();
			startZ=living.posZ;// - (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
			
			//double[] rand=TF2weapons.radiusRandom2D(this.getWeaponSpread(stack), world.rand);
			
			//float spreadPitch = (float) (living.rotationPitch / 180 + rand[1]);
			//float spreadYaw = (float) (living.rotationYaw / 180 + rand[0]*(90/Math.max(90-Math.abs(spreadPitch*180),0.0001f)));
			//System.out.println("Rot: "+living.rotationYawHead+" "+living.rotationPitch);
			float spreadPitch = living.rotationPitch / 180;
			float spreadYaw = living.rotationYawHead / 180;
		
			endX=(double)(-MathHelper.sin(spreadYaw * (float)Math.PI) * MathHelper.cos(spreadPitch * (float)Math.PI));
			endY=(double)(-MathHelper.sin(spreadPitch * (float)Math.PI));
			endZ=(double)(MathHelper.cos(spreadYaw * (float)Math.PI) * MathHelper.cos(spreadPitch * (float)Math.PI));
			
			float var9 = MathHelper.sqrt_double(endX * endX + endY * endY + endZ * endZ);
			//float[] ratioX= this.calculateRatioX(living.rotationYaw, living.rotationPitch);
			//float[] ratioY= this.calculateRatioY(living.rotationYaw, living.rotationPitch);
			//float wrapAngledYaw=MathHelper.wrapAngleTo180_float(living.rotationYaw);
			//float fixedYaw=Math.max(Math.abs(wrapAngledYaw),90)-Math.min(Math.abs(wrapAngledYaw),90);
			
			endX = (endX / (double)var9 + rand[0]) * getMaxRange() /*+ (rand[0]*ratioX[0])((fixedYaw/90)+(1-fixedYaw/90)*(-living.rotationPitch/90))*this.positive(wrapAngledYaw)*40*/;
			endY = (endY / (double)var9 + rand[1]) * getMaxRange() /*+ (rand[1]*ratioY[1])(0.5-Math.abs(spreadPitch))*80*40*/;
			endZ = (endZ / (double)var9 + rand[2]) * getMaxRange() /*+ ((ratioX[2]>ratioY[2]?rand[0]:rand[1])*(ratioX[2]+ratioY[2]))(rand[0]*ratioX[2] + rand[1]*ratioY[2])((1-fixedYaw/90)+(fixedYaw/90)*(-living.rotationPitch/90))*this.positive(wrapAngledYaw)*40*/;
			double distanceMax=getMaxRange()/Math.sqrt(endX*endX+endY*endY+endZ*endZ);
			//System.out.println(ratioX[0]+" "+ratioX[1]+" "+ratioX[2]+" "+ratioY[0]+" "+ratioY[1]+" "+ratioY[2]);
			
			endX *= distanceMax;
			endY *= distanceMax;
			endZ *= distanceMax;
			endX += startX;
			endY += startY;
			endZ += startZ;
			
			
			
		/*} else {
			startY = living.posY + (double)living.getEyeHeight() - 0.10000000149011612D;
			endX = target.posX - living.posX;
	        double var8 = target.posY + (double)target.getEyeHeight() - 0.699999988079071D - startY;
	        endZ = target.posZ - living.posZ;
	        double var12 = (double)MathHelper.sqrt_double(endX * endX + endZ * endZ);

	        if (var12 >= 1.0E-7D)
	        {
	            float var14 = (float)(Math.atan2(endZ, endX) * 180.0D / Math.PI) - 90.0F;
	            float var15 = (float)(-(Math.atan2(var8, var12) * 180.0D / Math.PI));
	            double var16 = endX / var12;
	            double var18 = endZ / var12;
	            startX=living.posX + var16;
	            startZ=living.posZ + var18;
	            float var20 = (float)var12 * 0.2F;
	            
	            endY=var8 + (double)var20;
	            
	            float var9 = MathHelper.sqrt_double(endX * endX + endY * endY + endZ * endZ);
	            endX = (endX / (double)var9 + rand[0]) * getMaxRange();
				endY = (endY / (double)var9 + rand[1]) * getMaxRange();
				endZ = (endZ / (double)var9 + rand[2]) * getMaxRange();
				
				double distance=getMaxRange()/Math.sqrt(Math.pow(endX, 2)+Math.pow(endY, 2)+Math.pow(endZ,2));
				
				endX *= distance;
				endY *= distance;
				endZ *= distance;
				endX += startX;
				endY += startY;
				endZ += startZ;
	        }
		}*/
		if(world.isRemote){
			if(this.showTracer(stack)){
			float mult=hand==EnumHand.MAIN_HAND?1:-1;
			ClientProxy.spawnBulletParticle(world,living,startX-(double)(MathHelper.cos(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F * mult), startY-0.1, 
					startZ- (double)(MathHelper.sin(living.rotationYaw / 180.0F * (float)Math.PI) * 0.16F * mult), endX, endY, endZ, 20,critical);
			}
			if(living !=Minecraft.getMinecraft().thePlayer) return;
		}
		//System.out.println(startX+" "+startY+" "+startZ+" "+endX+" "+endY+" "+endZ);
        List<RayTraceResult> list=TF2weapons.pierce(world, living, startX, startY, startZ, endX, endY, endZ,this.canHeadshot(living,stack), this.getBulletSize(stack,living),this.canPenetrate(stack,living));
        for(RayTraceResult var4:list){	
            if (var4.entityHit != null)
            {
            	float distance = 0;
                if (living != null)
                {
                    distance = (float) living.getPositionVector().distanceTo(var4.entityHit.getPositionVector());
                    distance-=living.width/2 + var4.entityHit.width/2;
                    
                    if(distance<0)
                    	distance=0;
                }
                if(!world.isRemote && !(living instanceof EntityPlayer)){
	            	if(!lastShot.containsKey(var4.entityHit)||lastShot.get(var4.entityHit)==null){
	            		lastShot.put(var4.entityHit, new float[3]);
	            	}
	            	//System.out.println(var4.hitInfo);
	            	if(var4.hitInfo!=null){
	            		critical=2;
	            		ItemWeapon.critical=2;
	            	}
	            	critical=this.setCritical(stack, living, var4.entityHit, critical);
	            	ItemWeapon.critical=critical;
	            	float[] values=lastShot.get(var4.entityHit);
	            	values[0]++;
	            	values[1]+=TF2weapons.calculateDamage(var4.entityHit,world, living, stack, critical, distance);
	            	//values[2]=distance;
                }
                else if(world.isRemote){
                	//System.out.println(var4.hitInfo);
    	            var4.hitInfo=new float[]{var4.hitInfo!=null?1:0,distance};
    	            lastShotClient.add(var4);
                }
            }
            else if(var4.getBlockPos()!=null){
            	if(world.isRemote && getData(stack).hasProperty(PropertyType.HIT_SOUND)){
            		SoundEvent event=getData(stack).hasProperty(PropertyType.HIT_WORLD_SOUND)?getSound(stack,PropertyType.HIT_WORLD_SOUND):getSound(stack,PropertyType.HIT_SOUND);
            		world.playSound(var4.hitVec.xCoord, var4.hitVec.yCoord, var4.hitVec.zCoord, event, SoundCategory.PLAYERS, getData(stack).getName().equals("fryingpan")?2f:0.7f, 1f, false);
            	}
            	else if(!world.isRemote && removeBlocks){
            		float damage=TF2weapons.calculateDamage(TF2weapons.dummyEnt,world, living, stack, critical, (float) living.getPositionVector().distanceTo(var4.hitVec));
            		if(stack.getItem() instanceof ItemSniperRifle){
            			damage*=2.52f;
            		}
            		damage*=TF2Attribute.getModifier("Destroy Block", stack, 0, living);
            		TF2weapons.damageBlock(var4.getBlockPos(), living, world, stack, critical, damage,new Vec3d(endX,endY,endZ), null);
            	}
            }
            
        }
	}
	public boolean canPenetrate(ItemStack stack,EntityLivingBase shooter) {
		// TODO Auto-generated method stub
		return TF2Attribute.getModifier("Penetration", stack, 0, shooter)>0;
	}

	/*public boolean checkHeadshot(World world, Entity living,
			ItemStack stack, Vec3d hitVec) {
		double ymax=living.getEntityBoundingBox().maxY;
		AxisAlignedBB head=AxisAlignedBB.getBoundingBox(living.posX-0.21, ymax-0.21, living.posZ-0.21,living.posX+0.21, ymax+0.21, living.posZ+0.21);
		System.out.println("Trafienie: "+Math.abs(ymax-hitVec.yCoord));
		
		return Math.abs(ymax-hitVec.yCoord)<0.205;
	}*/
	public boolean canHeadshot(EntityLivingBase living,ItemStack stack) {
		// TODO Auto-generated method stub
		return false;
	}
	public int positive(float value){
		if(value>0)
			return 1;
		return -1;
	}
	public float[] calculateRatioX(float yaw,float pitch){
		float[] result=new float[3];
		float angledYaw=Math.abs(MathHelper.wrapDegrees(yaw));
		float distanceYaw=Math.max(angledYaw,90)-Math.min(angledYaw,90);
		result[0]=(distanceYaw/90)+(1-distanceYaw/90)*(-pitch/90);
		result[2]=(1-distanceYaw/90);//+(1-distanceYaw/90)*(-pitch/90);
		result[1]=0;
		return result;
	}
	public float[] calculateRatioY(float yaw,float pitch){
		float[] result=new float[3];
		float angledYaw=Math.abs(MathHelper.wrapDegrees(yaw));
		float distanceYaw=Math.max(angledYaw,90)-Math.min(angledYaw,90);
		result[0]=0;
		result[2]=(distanceYaw/90)*(-pitch/90);
		result[1]=1-Math.abs(pitch)/90;
		return result;
	}
	
	public float getMaxRange(){
		return 256;
	}
	public float getBulletSize(ItemStack stack,EntityLivingBase living){
		return 0.04f;
	}
	public int setCritical(ItemStack stack,EntityLivingBase shooter, Entity target, int old){
		return TF2weapons.calculateCritsPost(target, shooter, old, stack);
	}
}
