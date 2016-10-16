package rafradek.TF2weapons.projectiles;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;

public class RenderProjectile extends Render<EntityProjectileBase> {
	private ModelBase model;
	private final ResourceLocation texturered;
	private final ResourceLocation textureblu;

	public RenderProjectile(ModelBase model, ResourceLocation redTexture, ResourceLocation bluTexture, RenderManager manager) {
	super(manager);
	// we could have initialized it above, but here is fine as well:
		this.model = model;
		this.texturered=redTexture;
		this.textureblu=bluTexture;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityProjectileBase entity) {
		
		return TF2weapons.getTeamForDisplay(entity.shootingEntity)==0?texturered:textureblu;
	}

	@Override
	public void doRender(EntityProjectileBase entity, double x, double y, double z, float yaw, float partialTick) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x,(float) y+entity.height/2,(float) z);
		GL11.glColor4f(0.7F, 0.7F, 0.7F, 1F);
		GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTick - 90.0F, 0.0F, 1.0F, 0.0F);
	    GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTick, 0.0F, 0.0F, 1.0F);
		
		bindEntityTexture(entity);
		if(entity.getCritical()==2){
	        GlStateManager.disableLighting();
	        model.render(entity, 0F, 0F, 0.0F, 0.0F, 0.0F, 0.0625F);
	        GlStateManager.enableLighting();
		}
		else{
			model.render(entity, 0F, 0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		}
		GL11.glPopMatrix();
	}
}
