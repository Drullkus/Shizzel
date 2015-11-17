package us.drullk.shizzel.container.appEng;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerWithPlayerInventory extends Container
{
    private static int rows = 3;

    private static int columns = 9;

    protected static final int renderSlotSize = 18;

    private static final int renderXOffset = 7;

    private int firstPlayerSlotNumber = -1, lastPlayerSlotNumber = -1;
    private int firstHotbarSlotNumber = -1, lastHotbarSlotNumber = -1;

    public void bindPlayerInventory(IInventory playerInv, int invPlayerOffsetY, int invHotbarOffsetY)
    {
        Slot hotbarSlot = null;

        for (int c = 0; c < ContainerWithPlayerInventory.columns; c++)
        {
            hotbarSlot = new Slot(playerInv, c, ContainerWithPlayerInventory.renderXOffset +
                    (c * ContainerWithPlayerInventory.renderSlotSize), invHotbarOffsetY);

            this.addSlotToContainer(hotbarSlot);

            if (c == 0)
            {
            }
        }

        if (hotbarSlot != null)
        {
        }

        Slot inventorySlot = null;
        for (int r = 0; r < ContainerWithPlayerInventory.rows; r++)
        {
            for (int c = 0; c < ContainerWithPlayerInventory.columns; c++)
            {
                inventorySlot = new Slot(playerInv,
                        ContainerWithPlayerInventory.columns + (c + (r * ContainerWithPlayerInventory.columns)),
                        ContainerWithPlayerInventory.renderXOffset + (c * ContainerWithPlayerInventory.renderSlotSize),
                        (r * ContainerWithPlayerInventory.renderSlotSize) + invPlayerOffsetY);

                this.addSlotToContainer(inventorySlot);

                if ((r + c) == 0)
                {
                }
            }
        }

        if (inventorySlot != null)
        {
        }
    }

    protected final boolean slotClickedWasInPlayerInventory( final int slotNumber )
    {
        return ( slotNumber >= this.firstPlayerSlotNumber ) && ( slotNumber <= this.lastPlayerSlotNumber );
    }

    public final List<Slot> getNonEmptySlotsFromPlayerInventory()
    {
        List<Slot> pSlots = new ArrayList<Slot>();

        for( int slotNumber = this.firstPlayerSlotNumber; slotNumber <= this.lastPlayerSlotNumber; slotNumber++ )
        {
            // Get the slot
            Slot pSlot = this.getSlot( slotNumber );

            // Is the slot not-empty
            if( pSlot.getHasStack() )
            {
                // Add to the list
                pSlots.add( pSlot );
            }
        }

        return pSlots;
    }

    protected final boolean slotClickedWasInHotbarInventory( final int slotNumber )
    {
        return ( slotNumber >= this.firstHotbarSlotNumber ) && ( slotNumber <= this.lastHotbarSlotNumber );
    }

    public final List<Slot> getNonEmptySlotsFromHotbar()
    {
        List<Slot> hSlots = new ArrayList<Slot>();

        for( int slotNumber = this.firstHotbarSlotNumber; slotNumber <= this.lastHotbarSlotNumber; slotNumber++ )
        {
            // Get the slot
            Slot hSlot = this.getSlot( slotNumber );

            // Is the slot not-empty
            if( hSlot.getHasStack() )
            {
                // Add to the list
                hSlots.add( hSlot );
            }
        }

        return hSlots;
    }

    protected final boolean mergeSlotWithHotbarInventory( final ItemStack slotStack )
    {
        return this.mergeItemStack( slotStack, this.firstHotbarSlotNumber, this.lastHotbarSlotNumber + 1, false );
    }

    protected final boolean mergeSlotWithPlayerInventory( final ItemStack slotStack )
    {
        return this.mergeItemStack( slotStack, this.firstPlayerSlotNumber, this.lastPlayerSlotNumber + 1, false );
    }

    protected final boolean swapSlotInventoryHotbar( final int slotNumber, final ItemStack slotStack )
    {
        if( this.slotClickedWasInHotbarInventory( slotNumber ) )
        {
            return this.mergeSlotWithPlayerInventory( slotStack );
        }
        else if( this.slotClickedWasInPlayerInventory( slotNumber ) )
        {
            return this.mergeSlotWithHotbarInventory( slotStack );
        }

        return false;
    }
}
