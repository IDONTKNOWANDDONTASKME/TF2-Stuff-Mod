package rafradek.TF2weapons.characters;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.building.EntityBuilding;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.building.EntitySentry;
import rafradek.TF2weapons.building.EntityTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemMonsterPlacerPlus extends Item {
	
	

	public ItemMonsterPlacerPlus()
    {
        this.setHasSubtypes(true);
        this.setCreativeTab(TF2weapons.tabutilitytf2);
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		 
        if (worldIn.isRemote)
        {
            return EnumActionResult.SUCCESS;
        }
        else if (!playerIn.canPlayerEdit(pos.offset(facing), facing, stack))
        {
            return EnumActionResult.FAIL;
        }
        else
        {
            IBlockState iblockstate = worldIn.getBlockState(pos);

            pos = pos.offset(facing);
            double d0 = 0.0D;

            if (facing == EnumFacing.UP && iblockstate.getBlock() instanceof BlockFence) //Forge: Fix Vanilla bug comparing state instead of block
            {
                d0 = 0.5D;
            }

            EntityLivingBase entity = spawnCreature(worldIn, stack.getItemDamage(), (double)pos.getX() + 0.5D, (double)pos.getY() + d0, (double)pos.getZ() + 0.5D,
            		stack.getTagCompound()!=null&&stack.getTagCompound().hasKey("SavedEntity")?stack.getTagCompound().getCompoundTag("SavedEntity"):null);

            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && stack.hasDisplayName())
                {
                    entity.setCustomNameTag(stack.getDisplayName());
                }

      

                if (!playerIn.capabilities.isCreativeMode)
                {
                    --stack.stackSize;
                }
                if(entity instanceof EntityBuilding){
    		     	((EntityBuilding)entity).setOwner(playerIn);
    		     	entity.rotationYaw=playerIn.rotationYawHead;
    		     	entity.renderYawOffset=playerIn.rotationYawHead;
    		     	entity.rotationYawHead=playerIn.rotationYawHead;
    		     	if(entity instanceof EntityTeleporter){
    		     		((EntityTeleporter) entity).setExit(stack.getItemDamage()>23);
    		     	}
    		     }
            }

            return EnumActionResult.SUCCESS;
        }
    }

    /**
     * Applies the data in the EntityTag tag of the given ItemStack to the given Entity.
     */

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        if (worldIn.isRemote)
        {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        }
        else
        {
            RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);

            if (raytraceresult != null && raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = raytraceresult.getBlockPos();

                if (!(worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid))
                {
                    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
                }
                else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemStackIn))
                {
                    EntityLivingBase entity = spawnCreature(worldIn, itemStackIn.getItemDamage(), (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D,itemStackIn.getTagCompound()!=null&&itemStackIn.getTagCompound().hasKey("SavedEntity")?itemStackIn.getTagCompound().getCompoundTag("SavedEntity"):null);

                    if (entity == null)
                    {
                        return new ActionResult(EnumActionResult.PASS, itemStackIn);
                    }
                    else
                    {
                        if (entity instanceof EntityLivingBase && itemStackIn.hasDisplayName())
                        {
                            entity.setCustomNameTag(itemStackIn.getDisplayName());
                        }


                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            --itemStackIn.stackSize;
                        }
                        if(entity instanceof EntityBuilding){
            		     	((EntityBuilding)entity).setOwner(playerIn);
            		     	entity.rotationYaw=playerIn.rotationYawHead;
            		     	entity.renderYawOffset=playerIn.rotationYawHead;
            		     	entity.rotationYawHead=playerIn.rotationYawHead;
            		     	/*if(entity instanceof EntityTeleporter){
            		     		((EntityTeleporter) entity).setExit(itemStackIn.getItemDamage()>23);
            		     	}*/
            		     }
                        playerIn.addStat(StatList.getObjectUseStats(this));
                        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
                    }
                }
                else
                {
                    return new ActionResult(EnumActionResult.FAIL, itemStackIn);
                }
            }
            else
            {
                return new ActionResult(EnumActionResult.PASS, itemStackIn);
            }
        }
    }


    /**
     * Spawns the creature specified by the egg's type in the location specified by the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static EntityLiving spawnCreature(World par0World, int par1, double par2, double par4, double par6, NBTTagCompound nbtdata)
    {
            EntityLiving entity = null;

            for (int j = 0; j < 1; ++j)
            {
            	if(par1/2==0){
                	entity = new EntityHeavy(par0World);
            	}else if(par1/2==1){
                	entity = new EntityScout(par0World);
            	}else if(par1/2==2){
                	entity = new EntitySniper(par0World);
            	}else if(par1/2==3){
                	entity = new EntitySoldier(par0World);
            	}else if(par1/2==4){
                	entity = new EntityPyro(par0World);
            	}else if(par1/2==5){
                	entity = new EntityDemoman(par0World);
            	}else if(par1/2==6){
                	entity = new EntityMedic(par0World);
            	}else if(par1/2==7){
                	entity = new EntitySpy(par0World);
            	}else if(par1/2==8){
                	entity = new EntityEngineer(par0World);
            	}else if(par1/2==9){
                	entity = new EntitySentry(par0World);
            	}else if(par1/2==10){
                	entity = new EntityDispenser(par0World);
            	}else if(par1/2==11){
                	entity = new EntityTeleporter(par0World);
            	}else if(par1/2==12){
                	entity = new EntityTeleporter(par0World);
            	}else if(par1/2==13){
                	entity = new EntitySaxtonHale(par0World);
            	}else if(par1==28){
                	entity = new EntityMonoculus(par0World);
            	}
                if (entity != null)
                {
                    EntityLiving entityliving = (EntityLiving)entity;
                    if(nbtdata!=null){
                    	entityliving.readFromNBT(nbtdata);
                    	//System.out.println("read");
                    }
                    entity.setLocationAndAngles(par2, par4, par6, MathHelper.wrapDegrees(par0World.rand.nextFloat() * 360.0F), 0.0F);
                    entityliving.rotationYawHead = entityliving.rotationYaw;
                    entityliving.renderYawOffset = entityliving.rotationYaw;
                    TF2CharacterAdditionalData data=new TF2CharacterAdditionalData();
                    data.team=par1%2;
                    entityliving.onInitialSpawn(par0World.getDifficultyForLocation(new BlockPos(entityliving)),data);
                    entityliving.playLivingSound();
                    if(entity instanceof EntityBuilding){
                    	((EntityBuilding)entity).setEntTeam(par1%2);
                    }
                    if(entity instanceof EntitySaxtonHale && par1%2==1){
                    	((EntitySaxtonHale)entity).setHostile();
                    }
                    if(!par0World.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty()){
                    	return null;
                    }
                    par0World.spawnEntityInWorld(entity);
                    
                }
                
            }

           return entity;
    }

    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List)
    {
    	for(int i=0;i<18;i++)
    		par3List.add(new ItemStack(par1, 1, i));
    	par3List.add(new ItemStack(par1, 1, 26));
    	par3List.add(new ItemStack(par1, 1, 27));
    	par3List.add(new ItemStack(par1, 1, 28));
    }

    public String getItemStackDisplayName(ItemStack p_77653_1_)
    {
        String s = ("" + I18n.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        int i=p_77653_1_.getItemDamage()/2;
        String s1="Heavy";
        switch(i){
        case 1: s1="Scout"; break;
        case 2: s1="Sniper"; break;
        case 3: s1="Soldier"; break;
        case 4: s1="Pyro"; break;
        case 5: s1="Demoman"; break;
        case 6: s1="Medic"; break;
        case 7: s1="Spy"; break;
        case 8: s1="Engineer"; break;
        case 13: s1="Saxton Hale"; break;
        }
        if(p_77653_1_.getItemDamage()==27){
        	s1= s1.concat(" (Hostile)");
        }
        return s.concat(" "+s1);
    }
}
