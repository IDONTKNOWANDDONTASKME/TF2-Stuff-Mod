package rafradek.TF2weapons.crafting;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.weapons.ItemAmmo;

public class ContainerAmmoFurnace extends Container {

	private final IInventory tileFurnace;
    private int cookTime;
    private int totalCookTime;
    private int furnaceBurnTime;
    private int currentItemBurnTime;

    public ContainerAmmoFurnace(InventoryPlayer playerInventory, IInventory furnaceInventory)
    {
        this.tileFurnace = furnaceInventory;
        for(int i =0; i< 9;i++){
        	this.addSlotToContainer(new Slot(furnaceInventory, i, 9+(i%3)*18, 17+(i/3)*18));
        }
        this.addSlotToContainer(new SlotFurnaceFuel(furnaceInventory, 9, 80, 53));
        for(int i =0; i< 9;i++){
        	this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, furnaceInventory, i+10, 116+(i%3)*18, 17+(i/3)*18));
        }
        

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.tileFurnace);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.listeners.size(); ++i)
        {
            IContainerListener icontainerlistener = (IContainerListener)this.listeners.get(i);

            if (this.cookTime != this.tileFurnace.getField(2))
            {
                icontainerlistener.sendProgressBarUpdate(this, 2, this.tileFurnace.getField(2));
            }

            if (this.furnaceBurnTime != this.tileFurnace.getField(0))
            {
                icontainerlistener.sendProgressBarUpdate(this, 0, this.tileFurnace.getField(0));
            }

            if (this.currentItemBurnTime != this.tileFurnace.getField(1))
            {
                icontainerlistener.sendProgressBarUpdate(this, 1, this.tileFurnace.getField(1));
            }

            if (this.totalCookTime != this.tileFurnace.getField(3))
            {
                icontainerlistener.sendProgressBarUpdate(this, 3, this.tileFurnace.getField(3));
            }
        }

        this.cookTime = this.tileFurnace.getField(2);
        this.furnaceBurnTime = this.tileFurnace.getField(0);
        this.currentItemBurnTime = this.tileFurnace.getField(1);
        this.totalCookTime = this.tileFurnace.getField(3);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tileFurnace.setField(id, data);
    }

    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.tileFurnace.isUseableByPlayer(playerIn);
    }

    /**
     * Take a stack from the specified inventory slot.
     */
    @Nullable
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index > 9 && index < 19)
            {
                if (!this.mergeItemStack(itemstack1, 19, 55, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index > 9)
            {
                if (itemstack1.getItem() instanceof ItemAmmo)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 9, false))
                    {
                        return null;
                    }
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1))
                {
                    if (!this.mergeItemStack(itemstack1, 9, 10, false))
                    {
                        return null;
                    }
                }
                else if (index >= 19 && index < 46)
                {
                    if (!this.mergeItemStack(itemstack1, 46, 55, false))
                    {
                        return null;
                    }
                }
                else if (index >= 46 && index < 55 && !this.mergeItemStack(itemstack1, 19, 46, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 19, 55, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }
}
