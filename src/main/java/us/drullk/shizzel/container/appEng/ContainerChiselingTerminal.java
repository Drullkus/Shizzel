package us.drullk.shizzel.container.appEng;

import java.util.ArrayList;
import java.util.List;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.container.implementations.ContainerCraftAmount;
import appeng.core.sync.GuiBridge;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import us.drullk.shizzel.appEng.PartChiselingTerminal;
import us.drullk.shizzel.gui.appEng.GUIChiselingTerminal;
import us.drullk.shizzel.networking.appEng.PacketChiselingTerminalClient;
import us.drullk.shizzel.utils.GuiHelper;
import us.drullk.shizzel.utils.Helper;

public class ContainerChiselingTerminal extends ContainerWithPlayerInventory implements IMEMonitorHandlerReceiver<IAEItemStack>
{
    public static int VIEW_SLOT_XPOS = 206, VIEW_SLOT_YPOS = 8;
    private static int playerInvPosY = 86;
    private static int hotbarInvPosY = playerInvPosY + (renderSlotSize * 3) + 5;
    private int firstViewSlotNumber = -1, lastViewSlotNumber = -1;
    private PartChiselingTerminal chiselTerm;
    private EntityPlayer entityPlayer;
    private IMEMonitor<IAEItemStack> MEMonitor;
    private PlayerSource playerSource;
    private int chiselFilterSlotNumber = -1;

    public ContainerChiselingTerminal(PartChiselingTerminal chiselingTerminal, EntityPlayer player)
    {
        this.chiselTerm = chiselingTerminal;
        this.entityPlayer = player;
        this.playerSource = new PlayerSource(player, chiselingTerminal);

        this.bindPlayerInventory(player.inventory, ContainerChiselingTerminal.playerInvPosY, ContainerChiselingTerminal.hotbarInvPosY);

        Slot chiselFilteringSlot = new Slot(this.chiselTerm, PartChiselingTerminal.chiselFilter, -66, -120);
        this.addSlotToContainer(chiselFilteringSlot);

        chiselFilterSlotNumber = chiselFilteringSlot.slotNumber;

        SlotRestrictive viewSlot = null;
        for (int viewSlotID = PartChiselingTerminal.VIEW_SLOT_MIN; viewSlotID <= PartChiselingTerminal.VIEW_SLOT_MAX; viewSlotID++)
        {
            int row = viewSlotID - PartChiselingTerminal.VIEW_SLOT_MIN;
            int yPos = ContainerChiselingTerminal.VIEW_SLOT_YPOS + (row * ContainerWithPlayerInventory.renderSlotSize);

            viewSlot = new SlotRestrictive(this.chiselTerm, viewSlotID, ContainerChiselingTerminal.VIEW_SLOT_XPOS, yPos);

            this.addSlotToContainer(viewSlot);

            if (row == 0)
            {
                this.firstViewSlotNumber = viewSlot.slotNumber;
            }
        }

        if (viewSlot != null)
        {
            this.lastViewSlotNumber = viewSlot.slotNumber;
        }

        if (Helper.isServerSide())
        {
            this.registerForUpdates();

            this.MEMonitor = chiselingTerminal.getItemInventory();

            if (this.MEMonitor != null)
            {
                this.MEMonitor.addListener(this, null);
            }
        }
    }

    private ItemStack[] getViewCells()
    {
        List<ItemStack> viewCells = new ArrayList<ItemStack>();

        Slot viewSlot;
        for (int viewSlotIndex = this.firstViewSlotNumber; viewSlotIndex <= this.lastViewSlotNumber; viewSlotIndex++)
        {
            // Get the slot
            viewSlot = this.getSlot(viewSlotIndex);

            // Ensure the slot is not empty
            if (!viewSlot.getHasStack())
            {
                continue;
            }

            // Add the cell
            viewCells.add(viewSlot.getStack());
        }

        return viewCells.toArray(new ItemStack[viewCells.size()]);
    }

    private boolean mergeWithViewCells(final ItemStack itemStack)
    {
        // Ensure the item a view cell
        if (!this.chiselTerm.isItemValidForSlot(PartChiselingTerminal.VIEW_SLOT_MIN, itemStack))
        {
            return false;
        }

        Slot viewSlot;
        for (int viewSlotIndex = this.firstViewSlotNumber; viewSlotIndex <= this.lastViewSlotNumber; viewSlotIndex++)
        {
            // Get the slot
            viewSlot = this.getSlot(viewSlotIndex);

            // Is there a slot?
            if (viewSlot == null)
            {
                // Somehow, there is a null slot
                continue;
            }

            // Ensure the slot is empty
            if (viewSlot.getHasStack())
            {
                continue;
            }

            // Insert the view cell
            viewSlot.putStack(itemStack.copy());

            // Clear the source stack
            itemStack.stackSize = 0;

            // Merge/move complete
            return true;
        }

        // Unable to move
        return false;
    }

    public void onViewCellChange()
    {
        // Only client side
        if (Helper.isClientSide())
        {
            // Update the gui
            this.updateGUIViewCells();
        }
    }

    @SideOnly(Side.CLIENT)
    private void updateGUIViewCells()
    {
        // Get the current screen being displayed to the user
        Gui gui = Minecraft.getMinecraft().currentScreen;

        // Is that screen the gui for the ACT?
        if (gui instanceof GUIChiselingTerminal)
        {
            ((GUIChiselingTerminal) gui).onViewCellsChanged(this.getViewCells());
        }
    }

    public void registerForUpdates()
    {
        this.chiselTerm.registerListener(this);
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber)
    {
        // Is this client side?
        if (Helper.isClientSide())
        {
            // Do nothing.
            return null;
        }

        // Get the slot that was shift-clicked
        Slot slot = (Slot) this.inventorySlots.get(slotNumber);

        // Is there a valid slot with and item?
        if ((slot != null) && (slot.getHasStack()))
        {
            boolean didMerge = false;

            // Get the itemstack in the slot
            ItemStack slotStack = slot.getStack();

            // Attempt to merge with the ME network
            didMerge = this.mergeWithMENetwork(slotStack);

            // Did we merge?
            if (!didMerge)
            {
                // Attempt to merge with the hotbar
                didMerge = this.mergeSlotWithHotbarInventory(slotStack);

                // Did we merge?
                if (!didMerge)
                {
                    // Attempt to merge with the player inventory
                    didMerge = this.mergeSlotWithPlayerInventory(slotStack);
                }
            }
            // Was the slot clicked in the player or hotbar inventory?
            else if (this.slotClickedWasInPlayerInventory(slotNumber) || this.slotClickedWasInHotbarInventory(slotNumber))
            {
                // Did we merge?
                if (!didMerge)
                {
                    // Attempt to merge with view cells
                    didMerge = this.mergeWithViewCells(slotStack);

                    // Did we merge?
                    if (!didMerge)
                    {
                        // Attempt to merge with the ME network
                        didMerge = this.mergeWithMENetwork(slotStack);

                        // Did we merge?
                        if (!didMerge)
                        {
                            // Attempt to swap hotbar<->player inventory
                            didMerge = this.swapSlotInventoryHotbar(slotNumber, slotStack);
                        }
                    }
                }
            }
            // Was the slot clicked a view cell?
            else if ((slotNumber >= this.firstViewSlotNumber) && (slotNumber <= this.lastViewSlotNumber))
            {
                // Attempt to merge with the hotbar
                didMerge = this.mergeSlotWithHotbarInventory(slotStack);

                // Did we merge?
                if (!didMerge)
                {
                    // Attempt to merge with the player inventory
                    didMerge = this.mergeSlotWithPlayerInventory(slotStack);
                }
            }

            // Did we merge?
            if (didMerge)
            {

                // Did the merger drain the stack?
                if ((slotStack == null) || (slotStack.stackSize == 0))
                {
                    // Set the slot to have no item
                    slot.putStack(null);
                }
                else
                {
                    // Inform the slot its stack changed;
                    slot.onSlotChanged();
                }

                // Send changes
                this.detectAndSendChanges();
            }

        }

        // All taken care of!
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return true;
    }

    @Override
    public boolean isValid(Object o)
    {
        return true;
    }

    @Override
    public void postChange(IBaseMonitor<IAEItemStack> iBaseMonitor, Iterable<IAEItemStack> iterable, BaseActionSource baseActionSource)
    {
        for (IAEItemStack change : iterable)
        {
            IAEItemStack newAmount = this.MEMonitor.getStorageList().findPrecise(change);

            if (newAmount == null)
            {
                newAmount = change.copy();

                newAmount.setStackSize(0);
            }

            new PacketChiselingTerminalClient().createChangeUpdate(this.entityPlayer, newAmount).sendPacketToPlayer();
        }
    }

    @Override
    public void onListUpdate()
    {
        //Useless
    }

    @Override
    public void onContainerClosed(final EntityPlayer player)
    {
        // Pass to super
        super.onContainerClosed(player);

        if (this.chiselTerm != null)
        {
            this.chiselTerm.removeListener(this);
        }

        // Is this server side?
        if (Helper.isServerSide())
        {
            if (this.MEMonitor != null)
            {
                this.MEMonitor.removeListener(this);
            }
        }
    }

    public void changeSlotsYOffset(final int deltaY)
    {
        for (Object slotObj : this.inventorySlots)
        {
            // Get the slot
            Slot slot = (Slot) slotObj;

            // Skip view slots
            if ((slot.slotNumber >= this.firstViewSlotNumber) && (slot.slotNumber <= this.lastViewSlotNumber))
            {
                continue;
            }

            // Adjust Y pos
            slot.yDisplayPosition += deltaY;
        }
    }

    public void onClientRequestFullUpdate(final EntityPlayer player)
    {
        // Send the sorting info
        new PacketChiselingTerminalClient().createSortingUpdate(player, this.chiselTerm.getSortOrder(),
                this.chiselTerm.getSortingDirection(), this.chiselTerm.getViewMode()).sendPacketToPlayer();

        // Ensure we have a monitor
        if (this.MEMonitor != null)
        {
            // Get the full list
            IItemList<IAEItemStack> fullList = this.MEMonitor.getStorageList();

            // Send to the client
            new PacketChiselingTerminalClient().createFullListUpdate(player, fullList).sendPacketToPlayer();
        }
    }

    public void onClientRequestExtract(final EntityPlayer player, final IAEItemStack requestedStack, final int mouseButton, final boolean isShiftHeld)
    {
        // Ensure there is a player
        if (player == null)
        {
            return;
        }

        // Ensure there is an itemstack
        if ((requestedStack == null) || (requestedStack.getStackSize() == 0))
        {
            return;
        }

        // Get the maximum stack size for the requested itemstack
        int maxStackSize = requestedStack.getItemStack().getMaxStackSize();

        // Determine the amount to extract
        int amountToExtract = 0;
        switch (mouseButton)
        {
        case GuiHelper.MOUSE_BUTTON_LEFT:
            // Full amount up to maxStackSize
            amountToExtract = (int) Math.min(maxStackSize, requestedStack.getStackSize());
            break;

        case GuiHelper.MOUSE_BUTTON_RIGHT:
            // Is shift being held?
            if (isShiftHeld)
            {
                // Extract 1
                amountToExtract = 1;
            }
            else
            {
                // Half amount up to half of maxStackSize
                double halfRequest = requestedStack.getStackSize() / 2.0D;
                double halfMax = maxStackSize / 2.0D;
                halfRequest = Math.ceil(halfRequest);
                halfMax = Math.ceil(halfMax);
                amountToExtract = (int) Math.min(halfMax, halfRequest);
            }
            break;

        case GuiHelper.MOUSE_WHEEL_MOTION:
            // Shift must be held
            if (isShiftHeld)
            {
                // Extract 1
                amountToExtract = 1;
            }
        }

        // Ensure we have some amount to extract
        if (amountToExtract <= 0)
        {
            // Nothing to extract
            return;
        }

        // Create the stack to extract
        IAEItemStack toExtract = requestedStack.copy();

        // Set the size
        toExtract.setStackSize(amountToExtract);

        // Simulate the extraction
        IAEItemStack extractedStack = this.MEMonitor.extractItems(toExtract, Actionable.SIMULATE, this.playerSource);

        // Did we extract anything?
        if ((extractedStack != null) && (extractedStack.getStackSize() > 0))
        {
            // Was this a left-click and is shift being held?
            if ((mouseButton == GuiHelper.MOUSE_BUTTON_LEFT) && isShiftHeld)
            {
                // Can we merge the item with the player inventory
                if (player.inventory.addItemStackToInventory(extractedStack.getItemStack()))
                {
                    // Merged with player inventory, extract the item
                    this.MEMonitor.extractItems(toExtract, Actionable.MODULATE, this.playerSource);

                    // Do not attempt to merge with what the player is holding.
                    return;
                }

            }

            // Get what the player is holding
            ItemStack playerHolding = player.inventory.getItemStack();

            // Is the player holding anything?
            if (playerHolding != null)
            {
                // Can we merge with what the player is holding?
                if ((playerHolding.stackSize < maxStackSize) && (playerHolding.isItemEqual(extractedStack.getItemStack())))
                {
                    // Determine how much room is left in the player holding stack
                    amountToExtract = Math.min(amountToExtract, maxStackSize - playerHolding.stackSize);

                    // Is there any room?
                    if (amountToExtract <= 0)
                    {
                        // Can't merge, not enough space
                        return;
                    }

                    // Increment what the player is holding
                    playerHolding.stackSize += amountToExtract;

                    // Set what the player is holding
                    player.inventory.setItemStack(playerHolding);

                    // Adjust extraction size
                    toExtract.setStackSize(amountToExtract);
                }
                else
                {
                    // Can't merge, not enough space or items don't match
                    return;
                }
            }
            else
            {
                // Set the extracted item(s) as what the player is holding
                player.inventory.setItemStack(extractedStack.getItemStack());
            }

            // Extract the item(s) from the network
            this.MEMonitor.extractItems(toExtract, Actionable.MODULATE, this.playerSource);

            // Send the update to the client
            new PacketChiselingTerminalClient().createPlayerHoldingUpdate(player,
                    AEApi.instance().storage().createItemStack(player.inventory.getItemStack())).sendPacketToPlayer();
        }
    }

    public void onClientRequestDeposit(final EntityPlayer player, final int mouseButton)
    {
        // Ensure there is a player
        if (player == null)
        {
            return;
        }

        // Get what the player is holding
        ItemStack playerHolding = player.inventory.getItemStack();

        // Is the player holding anything?
        if (playerHolding == null)
        {
            return;
        }

        // Create the AE itemstack representation of the itemstack
        IAEItemStack toInjectStack = AEApi.instance().storage().createItemStack(playerHolding);

        // Was it a right click or wheel movement?
        boolean depositOne = (mouseButton == GuiHelper.MOUSE_BUTTON_RIGHT) || (mouseButton == GuiHelper.MOUSE_WHEEL_MOTION);

        if (depositOne)
        {
            // Set stack size to 1
            toInjectStack.setStackSize(1);
        }

        // Attempt to inject
        IAEItemStack leftOverStack = this.MEMonitor.injectItems(toInjectStack, Actionable.MODULATE, this.playerSource);

        // Was there anything left over?
        if ((leftOverStack != null) && (leftOverStack.getStackSize() > 0))
        {
            // Were we only trying to inject one?
            if (toInjectStack.getStackSize() == 1)
            {
                // No changes made
                return;
            }

            // Set what was left over as the itemstack being held
            player.inventory.setItemStack(leftOverStack.getItemStack());
        }
        else
        {
            // Are we only depositing one, and there was more than 1 item?
            if ((depositOne) && (playerHolding.stackSize > 1))
            {
                // Set the player holding one less
                playerHolding.stackSize--;
                player.inventory.setItemStack(playerHolding);

                // Set the leftover stack to match
                leftOverStack = AEApi.instance().storage().createItemStack(playerHolding);
            }
            else
            {
                // Set the player as holding nothing
                player.inventory.setItemStack(null);
            }
        }

        new PacketChiselingTerminalClient().createPlayerHoldingUpdate(player, leftOverStack).sendPacketToPlayer();
    }

    public void onClientRequestDepositRegion(final EntityPlayer player, final int slotNumber)
    {
        List<Slot> slotsToDeposit = null;

        // Was the slot part of the player inventory?
        if (this.slotClickedWasInPlayerInventory(slotNumber))
        {
            // Get the items in the player inventory
            slotsToDeposit = this.getNonEmptySlotsFromPlayerInventory();
        }
        // Was the slot part of the hotbar?
        else if (this.slotClickedWasInHotbarInventory(slotNumber))
        {
            // Get the items in the hotbar
            slotsToDeposit = this.getNonEmptySlotsFromHotbar();
        }

        // Do we have any slots to transfer?
        if (slotsToDeposit != null)
        {
            for (Slot slot : slotsToDeposit)
            {
                // Ensure the slot is not null and has a stack
                if ((slot == null) || (!slot.getHasStack()))
                {
                    continue;
                }

                // Set the stack
                ItemStack slotStack = slot.getStack();

                // Inject into the ME network
                boolean didMerge = this.mergeWithMENetwork(slotStack);

                // Did any merge?
                if (!didMerge)
                {
                    continue;
                }

                // Did the merger drain the stack?
                if ((slotStack == null) || (slotStack.stackSize == 0))
                {
                    // Set the slot to have no item
                    slot.putStack(null);
                }
                else
                {
                    // Inform the slot its stack changed;
                    slot.onSlotChanged();
                }
            }

            // Update
            this.detectAndSendChanges();
        }
    }

    private boolean mergeWithMENetwork(final ItemStack itemStack)
    {
        // Attempt to place in the ME system
        IAEItemStack toInject = AEApi.instance().storage().createItemStack(itemStack);

        // Get what is left over after the injection
        IAEItemStack leftOver = this.MEMonitor.injectItems(toInject, Actionable.MODULATE, this.playerSource);

        // Do we have any left over?
        if ((leftOver != null) && (leftOver.getStackSize() > 0))
        {
            // Did we inject any?
            if (leftOver.getStackSize() == toInject.getStackSize())
            {
                // No injection occurred
                return false;
            }

            // Some was injected, adjust the slot stack size
            itemStack.stackSize = (int) leftOver.getStackSize();

            return true;
        }

        // All was injected
        itemStack.stackSize = 0;

        return true;
    }

    public void onClientRequestSetSort(final SortOrder order, final SortDir dir, final ViewItems viewMode)
    {
        this.chiselTerm.setSorts(order, dir, viewMode);
    }

    public void onClientRequestAutoCraft(final EntityPlayer player, final IAEItemStack result)
    {
        Platform.openGUI(player, this.chiselTerm.getHostTile(), this.chiselTerm.getSide(),
                GuiBridge.GUI_CRAFTING_AMOUNT);

        if (player.openContainer instanceof ContainerCraftAmount)
        {
            ContainerCraftAmount cca = (ContainerCraftAmount) this.entityPlayer.openContainer;

            cca.craftingItem.putStack(result.getItemStack());
            cca.whatToMake = result;

            cca.detectAndSendChanges();
        }
    }
}
