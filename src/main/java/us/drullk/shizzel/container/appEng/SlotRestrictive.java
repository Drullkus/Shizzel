package us.drullk.shizzel.container.appEng;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRestrictive extends Slot
{
    private int index;

    public SlotRestrictive(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
    {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);

        this.index = p_i1824_2_;
    }

    @Override
    public boolean isItemValid(final ItemStack itemstack)
    {
        return this.inventory.isItemValidForSlot(this.index, itemstack);
    }
}
