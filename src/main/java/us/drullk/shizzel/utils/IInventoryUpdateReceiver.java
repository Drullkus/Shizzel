package us.drullk.shizzel.utils;

import net.minecraft.inventory.IInventory;

public interface IInventoryUpdateReceiver
{
    public void onInventoryChanged(IInventory sourceInventory);
}
