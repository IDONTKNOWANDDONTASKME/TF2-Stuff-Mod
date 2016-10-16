package rafradek.TF2weapons.characters;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.pathfinding.PathNavigateGround;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Sounds;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.characters.ai.EntityAIAirblast;

public class EntityPyro extends EntityTF2Character{
	
	public EntityPyro(World par1World) {
		super(par1World);
		this.tasks.addTask(3, new EntityAIAirblast(this));
		if(this.attack !=null){
			this.attack.setDodge(true,true);
			this.attack.dodgeSpeed=1.2f;
			this.attack.setRange(6.2865F);
			this.attack.projSpeed=1.2570f;
		}
		this.rotation=16;
		this.ammoLeft=133;
		this.experienceValue=15;
		//((PathNavigateGround)this.getNavigator()).set(true);
		//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemUsable.getNewStack("Minigun"));
		
	}
	protected ResourceLocation getLootTable()
    {
        return TF2weapons.lootPyro;
    }
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if(source==DamageSource.onFire){
        	return false;
        }
        return super.attackEntityFrom(source, amount);
    }
	public void setFire(int time){
		super.setFire(1);
	}
	/*protected void addWeapons()
    {
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemFromData.getNewStack("flamethrower"));
    }*/
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(18.5D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
    }
	/*public void onLivingUpdate()
    {
        super.onLivingUpdate();
        if(this.ammoLeft>0&&this.getAttackTarget()!=null&&this.getDistanceSqToEntity(this.getAttackTarget())<=400&&(!TF2ActionHandler.playerAction.get(this.worldObj.isRemote).containsKey(this)||(TF2ActionHandler.playerAction.get(this.worldObj.isRemote).get(this)&3)==0)){
    		TF2ActionHandler.playerAction.get(this.worldObj.isRemote).put(this, TF2ActionHandler.playerAction.get(this.worldObj.isRemote).containsKey(this)?TF2ActionHandler.playerAction.get(this.worldObj.isRemote).get(this)+2:2);
    	}
    }*/
	protected SoundEvent getAmbientSound()
    {
        return TF2Sounds.MOB_PYRO_SAY;
    }
	

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected SoundEvent getHurtSound()
    {
        return TF2Sounds.MOB_PYRO_HURT;
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected SoundEvent getDeathSound()
    {
        return TF2Sounds.MOB_PYRO_DEATH;
    }

    /**
     * Get this Entity's EnumCreatureAttribute
     */
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
    {
    	if(this.rand.nextFloat()<0.15f+p_70628_2_*0.075f){
    		this.entityDropItem(ItemFromData.getNewStack("shotgun"), 0);
    	}
    	if(this.rand.nextFloat()<0.05f+p_70628_2_*0.025f){
    		this.entityDropItem(ItemFromData.getNewStack("flamethrower"), 0);
    	}
    }
	/*@Override
	public float getAttributeModifier(String attribute) {
		if(attribute.equals("Minigun Spinup")){
			return super.getAttributeModifier(attribute)*1.5f;
		}
		return super.getAttributeModifier(attribute);
	}*/
}
