package rafradek.TF2weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionTF2 extends Potion {

	public PotionTF2(boolean isBadEffectIn, int liquidColorIn,int x, int y) {
		super(isBadEffectIn, liquidColorIn);
		this.setIconIndex(x, y);
	}
	
}
