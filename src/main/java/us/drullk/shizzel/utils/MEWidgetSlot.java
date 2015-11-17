package us.drullk.shizzel.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class MEWidgetSlot
        extends Container
{
    private PrivateInventory internalInventory;

    public MEWidgetSlot(int inventorySize) throws Exception
    {
        this.internalInventory = new PrivateInventory("TC Inventory Bridge", inventorySize, 1);
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return false;
    }

    public void addSlot(final int index, final int posX, final int posY)
    {
        // Create the slot
        Slot bridgeSlot = new Slot(this.internalInventory, index, posX, posY);

        // Add the slot
        this.addSlotToContainer(bridgeSlot);
    }

    public void setSlot(final int index, final ItemStack stack)
    {
        this.getSlot(index).putStack(stack);
    }
}
