package rafradek.TF2weapons.weapons;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2EventBusListener;
import rafradek.TF2weapons.TF2weapons;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityBulletTracer extends Particle {
	
	private int duration;
	private boolean nextDead;

	public EntityBulletTracer(World par1World, double startX, double startY, double startZ, double x, double y, double z, int duration,int crits,EntityLivingBase shooter) {
		super(par1World, startX, startY, startZ);
		this.particleScale=0.2f;
		this.duration=duration;
		this.motionX=(x-startX)/duration;
		this.motionY=(y-startY)/duration;
		this.motionZ=(z-startZ)/duration;
		this.particleMaxAge=200;
		this.setSize(0.025f, 0.025f);
		//this.setParticleIcon(Item.itemsList[2498+256].getIconFromDamage(0));
		this.setParticleTexture(TF2EventBusListener.pelletIcon);
		//this.setParticleTextureIndex(81);
		this.multipleParticleScaleBy(2);
		
		// TODO Auto-generated constructor stub
		if(crits!=2){
			this.setRBGColorF(0.97f, 0.76f,0.51f);
		}
		else{
			if(TF2weapons.getTeamForDisplay(shooter)==0){
				this.setRBGColorF(1f, 0.2f,0.2f);
			}
			else{
				this.setRBGColorF(0.2f, 0.2f,1f);
			}
		}
		//S/ystem.out.println("Crits: "+crits);
	}
	
	public void onUpdate(){
		if(nextDead){
			this.setExpired();
		}
		if(this.worldObj.rayTraceBlocks(new Vec3d(posX, posY, posZ), new Vec3d(posX+motionX, posY+motionY, posZ+motionZ)) != null){
			nextDead=true;
			//this.setVelocity(0, 0, 0);
		}
		super.onUpdate();
		this.motionX *= 1.025D;
        this.motionY *= 1.025D;
        this.motionZ *= 1.025D;
		if(duration > 0){
			duration--;
			if(duration==0)
				this.setExpired();
		}
	}
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
		
		
		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        Vec3d rightVec=new Vec3d(this.motionX,this.motionY,this.motionZ).crossProduct(Minecraft.getMinecraft().getRenderViewEntity().getLook(1)).normalize();
        //System.out.println(rightVec);
        float f4 = 0.1F * this.particleScale;
        
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;
        
        float xNext = (float) (x+this.motionX*2);
        float yNext = (float) (y+this.motionY*2);
        float zNext = (float) (z+this.motionZ*2);
        
        float xMin = this.particleTexture.getMinU();
        float xMax = this.particleTexture.getMaxU();
        float yMin = this.particleTexture.getMinV();
        float yMax = this.particleTexture.getMaxV();
        
        worldRendererIn.pos((double)(x - rightVec.xCoord * f4), (double)(y-rightVec.yCoord*f4), (double)(z-rightVec.zCoord*f4)).tex((double)xMax, (double)yMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
        worldRendererIn.pos((double)(x + rightVec.xCoord * f4), (double)(y+rightVec.yCoord*f4), (double)(z+rightVec.zCoord*f4)).tex((double)xMax, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
        worldRendererIn.pos((double)(xNext + rightVec.xCoord * f4), (double)(yNext+rightVec.yCoord*f4), (double)(zNext+rightVec.zCoord*f4)).tex((double)xMin, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
        worldRendererIn.pos((double)(xNext - rightVec.xCoord * f4 ), (double)(yNext-rightVec.yCoord*f4), (double)(zNext-rightVec.zCoord*f4)).tex((double)xMin, (double)yMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
		
        /*worldRendererIn.pos((double)(x + rightVec.xCoord * f4), (double)(y+rightVec.yCoord*f4), (double)(z+rightVec.zCoord*f4)).tex((double)xMax, (double)yMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
        worldRendererIn.pos((double)(x - rightVec.xCoord * f4), (double)(y-rightVec.yCoord*f4), (double)(z-rightVec.zCoord*f4)).tex((double)xMax, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
        worldRendererIn.pos((double)(xNext - rightVec.xCoord * f4), (double)(yNext-rightVec.yCoord*f4), (double)(zNext-rightVec.zCoord*f4)).tex((double)xMin, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
        worldRendererIn.pos((double)(xNext + rightVec.xCoord * f4 ), (double)(yNext+rightVec.yCoord*f4), (double)(zNext+rightVec.zCoord*f4)).tex((double)xMin, (double)yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;*/
        //System.out.println("Rotation X: "+rotationX+" Rotation Z: "+rotationZ+" Rotation YZ: "+rotationYZ+" Rotation XY: "+rotationXY+" rotation XZ: "+rotationXZ);
		//super.renderParticle(worldRendererIn, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }
	public void moveEntity(double x, double y, double z)
    {
        this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
        this.resetPositionToBB();
    }
	
	public int getFXLayer() {
	      return 1;
	   }
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float p_70070_1_)
    {
		return 15728880;
    }

    public float getBrightness(float p_70013_1_)
    {
        return 1.0F;
    }
}
