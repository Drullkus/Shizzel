package us.drullk.shizzel.container.appEng;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerWithPlayerInventory extends Container
{
    private static int rows = 3;

    private static int columns = 9;

    protected static final int renderSlotSize = 18;

    private static final int renderXOffset = 8;

    private int firstPlayerSlotNumber = -1, lastPlayerSlotNumber = -1;

    private int firstHotbarSlotNumber = -1, lastHotbarSlotNumber = -1;

    public final void bindPlayerInventory( final IInventory playerInventory, final int inventoryOffsetY, final int hotbarPositionY )
    {

        // Hot-bar ID's 0-8
        Slot hotbarSlot = null;
        for( int column = 0; column < ContainerWithPlayerInventory.columns; column++ )
        {
            // Create the slot
            hotbarSlot = new Slot( playerInventory, column, ContainerWithPlayerInventory.renderXOffset +
                    ( column * ContainerWithPlayerInventory.renderSlotSize ), hotbarPositionY );

            // Add the slot
            this.addSlotToContainer( hotbarSlot );

            // Check first
            if( column == 0 )
            {
                this.firstHotbarSlotNumber = hotbarSlot.slotNumber;
            }
        }

        // Set last
        if( hotbarSlot != null )
        {
            this.lastHotbarSlotNumber = hotbarSlot.slotNumber;
        }

        // Main inventory ID's 9-36
        Slot inventorySlot = null;
        for( int row = 0; row < ContainerWithPlayerInventory.rows; row++ )
        {
            for( int column = 0; column < ContainerWithPlayerInventory.columns; column++ )
            {
                // Create the slot
                inventorySlot = new Slot( playerInventory, ContainerWithPlayerInventory.columns +
                        ( column + ( row * ContainerWithPlayerInventory.columns ) ), ContainerWithPlayerInventory.renderXOffset +
                        ( column * ContainerWithPlayerInventory.renderSlotSize ), ( row * ContainerWithPlayerInventory.renderSlotSize ) +
                        inventoryOffsetY );

                // Add the slot
                this.addSlotToContainer( inventorySlot );

                // Check first
                if( ( row + column ) == 0 )
                {
                    this.firstPlayerSlotNumber = inventorySlot.slotNumber;
                }
            }
        }

        // Set last
        if( inventorySlot != null )
        {
            this.lastPlayerSlotNumber = inventorySlot.slotNumber;
        }
    }

    protected final boolean slotClickedWasInPlayerInventory(final int slotNumber)
    {
        return (slotNumber >= this.firstPlayerSlotNumber) && (slotNumber <= this.lastPlayerSlotNumber);
    }

    public final List<Slot> getNonEmptySlotsFromPlayerInventory()
    {
        List<Slot> pSlots = new ArrayList<Slot>();

        for (int slotNumber = this.firstPlayerSlotNumber; slotNumber <= this.lastPlayerSlotNumber; slotNumber++)
        {
            // Get the slot
            Slot pSlot = this.getSlot(slotNumber);

            // Is the slot not-empty
            if (pSlot.getHasStack())
            {
                // Add to the list
                pSlots.add(pSlot);
            }
        }

        return pSlots;
    }

    protected final boolean slotClickedWasInHotbarInventory(final int slotNumber)
    {
        return (slotNumber >= this.firstHotbarSlotNumber) && (slotNumber <= this.lastHotbarSlotNumber);
    }

    public final List<Slot> getNonEmptySlotsFromHotbar()
    {
        List<Slot> hSlots = new ArrayList<Slot>();

        for (int slotNumber = this.firstHotbarSlotNumber; slotNumber <= this.lastHotbarSlotNumber; slotNumber++)
        {
            // Get the slot
            Slot hSlot = this.getSlot(slotNumber);

            // Is the slot not-empty
            if (hSlot.getHasStack())
            {
                // Add to the list
                hSlots.add(hSlot);
            }
        }

        return hSlots;
    }

    protected final boolean mergeSlotWithHotbarInventory(final ItemStack slotStack)
    {
        return this.mergeItemStack(slotStack, this.firstHotbarSlotNumber, this.lastHotbarSlotNumber + 1, false);
    }

    protected final boolean mergeSlotWithPlayerInventory(final ItemStack slotStack)
    {
        return this.mergeItemStack(slotStack, this.firstPlayerSlotNumber, this.lastPlayerSlotNumber + 1, false);
    }

    protected final boolean swapSlotInventoryHotbar(final int slotNumber, final ItemStack slotStack)
    {
        if (this.slotClickedWasInHotbarInventory(slotNumber))
        {
            return this.mergeSlotWithPlayerInventory(slotStack);
        }
        else if (this.slotClickedWasInPlayerInventory(slotNumber))
        {
            return this.mergeSlotWithHotbarInventory(slotStack);
        }

        return false;
    }
}
