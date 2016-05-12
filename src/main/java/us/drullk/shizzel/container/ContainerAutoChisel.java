package us.drullk.shizzel.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerAutoChisel extends Container
{
    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }
}
