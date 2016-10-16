package rafradek.TF2weapons.decoration;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import rafradek.TF2weapons.TF2weapons;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class InventoryWearables extends InventoryBasic implements ICapabilityProvider , INBTSerializable<NBTTagList>{

	public InventoryWearables() {
		super("Wearables", false, 13);
		// TODO Auto-generated constructor stub
	}

	public boolean isEmpty(){
		for(int i=0;i<4;i++){
			if(this.getStackInSlot(i)!=null){
				return false;
			}
		}
		return true;
	}
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return TF2weapons.INVENTORY_CAP!=null && capability==TF2weapons.INVENTORY_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (TF2weapons.INVENTORY_CAP != null && capability == TF2weapons.INVENTORY_CAP) return TF2weapons.INVENTORY_CAP.cast(this);
		return null;
	}

	@Override
	public NBTTagList serializeNBT() {
		NBTTagList list=new NBTTagList();
		for(int i=0;i<this.getSizeInventory();i++){
			ItemStack itemstack = this.getStackInSlot(i);

            if (itemstack != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                list.appendTag(nbttagcompound);
            }
		}
		//System.out.println("Saving ");
		return list;
	}

	@Override
	public void deserializeNBT(NBTTagList nbt) {
		
        for (int i = 0; i < nbt.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbt.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot");
            this.setInventorySlotContents(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
        }
       // System.out.println("Reading ");
	}

}
