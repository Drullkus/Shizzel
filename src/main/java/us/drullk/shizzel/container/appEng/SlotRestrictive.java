package us.drullk.shizzel.container.appEng;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRestrictive extends Slot
{
    private int itemIndex;

    public SlotRestrictive(IInventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);

        this.itemIndex = index;
    }

    @Override
    public boolean isItemValid(final ItemStack itemstack)
    {
        return this.inventory.isItemValidForSlot(this.itemIndex, itemstack);
    }
}
