package us.drullk.shizzel.container.appEng;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public abstract class ContainerWithPlayerInventory extends Container
{
    private static int rows = 3;
    private static int columns = 9;
    protected static final int renderSlotSize = 18;
    private static final int renderXOffset = 7;
    private int firstPlayerSlotNumber = -1;
    private int lastPlayerSlotNumber = -1;
    private int firstHotbarSlotNumber = -1;
    private int lastHotbarSlotNumber = -1;

    public void bindPlayerInventory(IInventory playerInv, int invPlayerOffsetY, int invHotbarOffsetY)
    {
        Slot hotbarSlot = null;

        for(int c = 0; c < ContainerWithPlayerInventory.columns; c++)
        {
            hotbarSlot = new Slot(playerInv, c, ContainerWithPlayerInventory.renderXOffset +
                    (c * ContainerWithPlayerInventory.renderSlotSize), invHotbarOffsetY);

            this.addSlotToContainer(hotbarSlot);

            if(c == 0)
            {
                this.firstHotbarSlotNumber = hotbarSlot.slotNumber;
            }
        }

        if( hotbarSlot != null )
        {
            this.lastHotbarSlotNumber = hotbarSlot.slotNumber;
        }

        Slot inventorySlot = null;
        for( int r = 0; r < ContainerWithPlayerInventory.rows; r++ )
        {
            for( int c = 0; c < ContainerWithPlayerInventory.columns; c++ )
            {
                inventorySlot = new Slot( playerInv,
                        ContainerWithPlayerInventory.columns + (c + (r * ContainerWithPlayerInventory.columns)),
                        ContainerWithPlayerInventory.renderXOffset + (c * ContainerWithPlayerInventory.renderSlotSize),
                        (r * ContainerWithPlayerInventory.renderSlotSize) + invPlayerOffsetY);

                this.addSlotToContainer(inventorySlot);

                if((r + c) == 0)
                {
                    this.firstPlayerSlotNumber = inventorySlot.slotNumber;
                }
            }
        }

        if( inventorySlot != null )
        {
            this.lastPlayerSlotNumber = inventorySlot.slotNumber;
        }
    }
}
