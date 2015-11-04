package us.drullk.shizzel.appEng;

import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import us.drullk.shizzel.Shizzel;

public class PartChiselingTerminal extends AEPartAbstractRotateable implements IInventory, IGridTickable, ITerminalHost
{
    private static final int invSize = 1;
    private static final String NBTTagInv = "ShizzelAEInv";
    private static final String NBTTagSlot = "Slot#";
    private static final String NBTTagSortOrder = "SortOrder";
    private static final String NBTTagSortDirection = "SortDirection";
    private static final String NBTTagViewMode = "ViewMode";
    private static final double powerDrain = 0.5D;

    private static final SortOrder defSortOrder = SortOrder.NAME;
    private static final SortDir defSortDirection = SortDir.ASCENDING;
    private static final ViewItems defViewItems = ViewItems.ALL;

    private SortOrder sortOrder = PartChiselingTerminal.defSortOrder;
    private SortDir sortDirection = PartChiselingTerminal.defSortDirection;
    private ViewItems viewMode = PartChiselingTerminal.defViewItems;

    private final ItemStack[] slots = new ItemStack[PartChiselingTerminal.invSize];

    public PartChiselingTerminal()
    {
        // Constructor
    }

    private boolean isSlotSafe( final int slotRequest )
    {
        // Condition whether it's safe to get an item or no.
        return(( slotRequest >= 0) && (slotRequest < PartChiselingTerminal.invSize));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
        Tessellator ts = Tessellator.instance;

        //TODO: GUI stuff. Fun.
    }

    @Override
    public void renderStatic(int i, int i1, int i2, IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
        //TODO: Block Render.
    }

    @Override
    public IIcon getBreakingTexture() {
        return null; //TODO: Make texture for default breaking particles
    }

    @Override
    public int cableConnectionRenderTo() {
        return 3;
    }

    @Override
    public void getBoxes(IPartCollisionHelper iPartCollisionHelper) {
        //TODO: Collision Boxes
    }

    @Override
    public IConfigManager getConfigManager() {
        return null; // Useless
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(2, 20, false, false); // Every 2 to 20 MC ticks
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int i)
    {
        //TODO: Find out what this is.
        //Looks like it is a call to the Part's functions. Possibly important
        return null;
    }

    @Override
    public int getSizeInventory() {
        return PartChiselingTerminal.invSize;
    }

    @Override
    public ItemStack getStackInSlot(int slotRequest) {
        return (isSlotSafe(slotRequest) ? this.slots[slotRequest] : null);
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        return null; //TODO: Do later
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotRequest) {
        return (isSlotSafe(slotRequest) ? this.slots[slotRequest] : null);
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        //TODO: Figure this one out too
    }

    @Override
    public String getInventoryName() {
        return Shizzel.MOD_ID + ".terminal.chiseling.inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64; // lol
    }

    @Override
    public void markDirty() {
        //TODO: Do later as well
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {
        // Useless
    }

    @Override
    public void closeInventory() {
        // Useless
    }

    @Override
    public boolean isItemValidForSlot(int slotRequest, ItemStack is) {
        return false; //TODO: Later
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory() {
        return null; //TODO: Later
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory() {
        return null; //TODO: Later
    }
}
