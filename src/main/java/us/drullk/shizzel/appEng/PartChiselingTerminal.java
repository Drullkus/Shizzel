package us.drullk.shizzel.appEng;

import java.util.ArrayList;
import java.util.List;

import appeng.api.config.SecurityPermissions;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.implementations.items.IMemoryCard;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.IConfigManager;
import appeng.items.storage.ItemViewCell;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import team.chisel.carving.Carving;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.enumList.AEParts;
import us.drullk.shizzel.appEng.enumList.EnumCache;
import us.drullk.shizzel.container.appEng.ContainerChiselingTerminal;
import us.drullk.shizzel.gui.appEng.GUIChiselingTerminal;
import us.drullk.shizzel.utils.EnumBlockTextures;
import us.drullk.shizzel.utils.Helper;

public class PartChiselingTerminal extends AEPartAbstractRotateable implements IInventory, IGridTickable, ITerminalHost
{
    private static int MY_INVENTORY_SIZE = 6;

    public static int CHISEL_FILTER_SLOT_INDEX = 0, VIEW_SLOT_MIN = 1, VIEW_SLOT_MAX = 5;

    private static String INVENTORY_NBT_KEY = "Shizel_Inventory";

    private static String SLOT_NBT_KEY = "Slot#";

    private static String SORT_ORDER_NBT_KEY = "SortOrder";

    private static String SORT_DIRECTION_NBT_KEY = "SortDirection";

    private static String VIEW_MODE_NBT_KEY = "ViewMode";

    private static double powerDrain = 0.5D;

    private static SortOrder DEFAULT_SORT_ORDER = SortOrder.NAME;

    private static SortDir DEFAULT_SORT_DIR = SortDir.ASCENDING;

    private static ViewItems DEFAULT_VIEW_MODE = ViewItems.ALL;

    private SortOrder sortingOrder = PartChiselingTerminal.DEFAULT_SORT_ORDER;

    private SortDir sortingDirection = PartChiselingTerminal.DEFAULT_SORT_DIR;

    private ViewItems viewMode = PartChiselingTerminal.DEFAULT_VIEW_MODE;

    private ItemStack[] slots = new ItemStack[PartChiselingTerminal.MY_INVENTORY_SIZE];

    private List<ContainerChiselingTerminal> listeners = new ArrayList<ContainerChiselingTerminal>();

    public PartChiselingTerminal()
    {
        super(AEParts.PartChiselingTerminal);
    }

    private boolean isSlotSafe(final int slotRequest)
    {
        return ((slotRequest >= 0) && (slotRequest < PartChiselingTerminal.MY_INVENTORY_SIZE));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventory(IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
        Tessellator ts = Tessellator.instance;

        IIcon side = EnumBlockTextures.BASE.getTexture();

        iPartRenderHelper.setTexture(side);
        iPartRenderHelper.setBounds(4.0F, 4.0F, 13.0F, 12.0F, 12.0F, 14.0F);
        iPartRenderHelper.renderInventoryBox(renderBlocks);

        iPartRenderHelper.setTexture(side, side, side, EnumBlockTextures.CHISELING_TERMINAL.getTextures()[3], side, side);
        iPartRenderHelper.setBounds(2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F);
        iPartRenderHelper.renderInventoryBox(renderBlocks);

        ts.setBrightness(0xD000D0);

        iPartRenderHelper.setInvColor(0xFFFFFF);

        iPartRenderHelper.renderInventoryFace(EnumBlockTextures.CHISELING_TERMINAL.getTextures()[3], ForgeDirection.SOUTH, renderBlocks);

        iPartRenderHelper.setBounds(3.0F, 3.0F, 15.0F, 13.0F, 13.0F, 16.0F);

        iPartRenderHelper.setInvColor(AEColor.Transparent.blackVariant);
        iPartRenderHelper.renderInventoryFace(EnumBlockTextures.CHISELING_TERMINAL.getTextures()[0], ForgeDirection.SOUTH, renderBlocks);

        iPartRenderHelper.setInvColor(AEColor.Transparent.mediumVariant);
        iPartRenderHelper.renderInventoryFace(EnumBlockTextures.CHISELING_TERMINAL.getTextures()[1], ForgeDirection.SOUTH, renderBlocks);

        iPartRenderHelper.setInvColor(AEColor.Transparent.whiteVariant);
        iPartRenderHelper.renderInventoryFace(EnumBlockTextures.CHISELING_TERMINAL.getTextures()[2], ForgeDirection.SOUTH, renderBlocks);

        //helper.setBounds( 5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F );
        //this.renderInventoryBusLights( helper, renderer );
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(int x, int y, int z, IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
        Tessellator ts = Tessellator.instance;

        IIcon side = EnumBlockTextures.BASE.getTexture();

        iPartRenderHelper.setTexture(side);
        iPartRenderHelper.setBounds(4.0F, 4.0F, 13.0F, 12.0F, 12.0F, 14.0F);
        iPartRenderHelper.renderBlock(x, y, z, renderBlocks);

        iPartRenderHelper.setTexture(side, side, side, EnumBlockTextures.CHISELING_TERMINAL.getTextures()[3], side, side);
        iPartRenderHelper.setBounds(2.0F, 2.0F, 14.0F, 14.0F, 14.0F, 16.0F);
        iPartRenderHelper.renderBlock(x, y, z, renderBlocks);

        if (this.isActive())
        {
            Tessellator.instance.setBrightness(0xD000D0);
        }

        this.rotateRenderer(renderBlocks, false);

        ts.setColorOpaque_I(0xFFFFFF);
        iPartRenderHelper.renderFace(x, y, z, EnumBlockTextures.CHISELING_TERMINAL.getTextures()[3], ForgeDirection.SOUTH, renderBlocks);

        iPartRenderHelper.setBounds(3.0F, 3.0F, 15.0F, 13.0F, 13.0F, 16.0F);
        ts.setColorOpaque_I(this.host.getColor().blackVariant);
        iPartRenderHelper.renderFace(x, y, z, EnumBlockTextures.CHISELING_TERMINAL.getTextures()[0], ForgeDirection.SOUTH, renderBlocks);

        ts.setColorOpaque_I(this.host.getColor().mediumVariant);
        iPartRenderHelper.renderFace(x, y, z, EnumBlockTextures.CHISELING_TERMINAL.getTextures()[1], ForgeDirection.SOUTH, renderBlocks);

        ts.setColorOpaque_I(this.host.getColor().whiteVariant);
        iPartRenderHelper.renderFace(x, y, z, EnumBlockTextures.CHISELING_TERMINAL.getTextures()[2], ForgeDirection.SOUTH, renderBlocks);

        this.rotateRenderer(renderBlocks, true);

        iPartRenderHelper.setBounds(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 13.0F);
        this.renderStaticBusLights(x, y, z, iPartRenderHelper, renderBlocks);
    }

    @Override
    public IIcon getBreakingTexture()
    {
        return EnumBlockTextures.BASE.getTexture();
    }

    @Override
    public int cableConnectionRenderTo()
    {
        return 3;
    }

    @Override
    public void getBoxes(IPartCollisionHelper iPartCollisionHelper)
    {
        iPartCollisionHelper.addBox(2.0D, 2.0D, 14.0D, 14.0D, 14.0D, 16.0D);
        iPartCollisionHelper.addBox(5.0D, 5.0D, 13.0D, 11.0D, 11.0D, 14.0D);
    }

    @Override
    public IConfigManager getConfigManager()
    {
        return null; // Useless
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode)
    {
        return new TickingRequest(2, 20, false, false); // Every 2 to 20 MC ticks
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode iGridNode, int ticksSinceLastRequest)
    {
        //Looks like it is a call to the Part's functions. Possibly important
        return TickRateModulation.IDLE;
    }

    @Override
    public int getSizeInventory()
    {
        return PartChiselingTerminal.MY_INVENTORY_SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int slotRequest)
    {
        return (this.isSlotSafe(slotRequest) ? this.slots[slotRequest] : null);
    }

    @Override
    public ItemStack decrStackSize(int slotRequest, int amount)
    {
        ItemStack returnedStack = null;

        // Prevent AOBE with slot array
        if (this.isSlotSafe(slotRequest))
        {
            ItemStack stackInSlot = this.slots[slotRequest];

            // Check if ItemStack is not null
            if (stackInSlot != null)
            {
                if (amount >= stackInSlot.stackSize)
                {
                    returnedStack = stackInSlot.copy();

                    this.slots[slotRequest].stackSize = 0;
                }
                else
                {
                    returnedStack = stackInSlot.splitStack(amount);
                }

                if (this.slots[slotRequest].stackSize == 0)
                {
                    this.slots[slotRequest] = null;
                }

                this.notifyListeners(slotRequest);
            }
        }

        return returnedStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotRequest)
    {
        return (this.isSlotSafe(slotRequest) ? this.slots[slotRequest] : null);
    }

    @Override
    public void setInventorySlotContents(int slotRequest, ItemStack stackInSlot)
    {
        if (this.setInventorySlotContentsNoListener(slotRequest, stackInSlot))
        {
            this.notifyListeners(slotRequest);
        }
    }

    public boolean setInventorySlotContentsNoListener(int slotRequest, ItemStack stackInSlot)
    {
        if (this.isSlotSafe(slotRequest))
        {
            this.slots[slotRequest] = stackInSlot;

            return true;
        }
        else
        {
            return false;
        }
    }

    private void notifyListeners(int slotRequest)
    {
        for (ContainerChiselingTerminal listener : this.listeners)
        {
            // Ensure the listener is still there
            if (listener != null)
            {
                listener.onViewCellChange();
            }
        }
    }

    @Override
    public String getInventoryName()
    {
        return Shizzel.MOD_ID + ".terminal.chiseling.inventory";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64; // lol
    }

    @Override
    public void markDirty()
    {
        this.markForSave();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
        // Useless
    }

    @Override
    public void closeInventory()
    {
        // Useless
    }

    @Override
    public boolean isItemValidForSlot(int slotRequest, ItemStack is)
    {
        if (this.isSlotSafe(slotRequest))
        {
            if (is == null)
            {
                return true;
            }

            if ((slotRequest == PartChiselingTerminal.CHISEL_FILTER_SLOT_INDEX))
            {
                return Helper.isBlockChiselable(Carving.chisel.getGroup(is));
            }

            if ((slotRequest >= PartChiselingTerminal.VIEW_SLOT_MIN) && (slotRequest <= PartChiselingTerminal.VIEW_SLOT_MAX))
            {
                // Is the stack a view slot?
                return (is.getItem() instanceof ItemViewCell);

            }

            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public IMEMonitor<IAEItemStack> getItemInventory()
    {
        return this.getGridBlock().getItemMonitor();
    }

    @Override
    public IMEMonitor<IAEFluidStack> getFluidInventory()
    {
        return null; // Useless
    }

    @Override
    public boolean doesPlayerHavePermissionToOpenGui(EntityPlayer player)
    {
        if (this.doesPlayerHavePermission(player, SecurityPermissions.EXTRACT) && this.doesPlayerHavePermission(player, SecurityPermissions.INJECT))
        {
            return true;
        }

        return false;
    }

    @Override
    public void getDrops(List<ItemStack> drops, boolean wrenched)
    {
        // Were we wrenched?
        if (wrenched)
        {
            // Inventory is saved when wrenched
            return;
        }

        for (int slotIndex = 0; slotIndex < PartChiselingTerminal.MY_INVENTORY_SIZE; slotIndex++)
        {
            ItemStack slotStack = this.slots[slotIndex];

            if (slotStack != null)
            {
                drops.add(slotStack);
            }
        }
    }

    @Override
    public double getIdlePowerUsage()
    {
        return PartChiselingTerminal.powerDrain;
    }

    @Override
    public int getLightLevel()
    { //Brightness of block
        return (this.isActive() ? 9 : 0);
    }

    @Override
    public Object getServerGuiElement(EntityPlayer player)
    {
        return new ContainerChiselingTerminal(this, player);
    }

    @Override
    public Object getClientGuiElement(EntityPlayer player)
    {
        return new GUIChiselingTerminal(this, player);
    }

    public SortDir getSortingDirection()
    {
        return this.sortingDirection;
    }

    public SortOrder getSortOrder()
    {
        return this.sortingOrder;
    }

    public ViewItems getViewMode()
    {
        return this.viewMode;
    }

    public World getWorldObj()
    {
        return this.TE.getWorldObj();
    }

    @Override
    public boolean onActivate(EntityPlayer entityPlayer, Vec3 pos)
    {
        ItemStack heldItem = entityPlayer.inventory.getCurrentItem();

        if ((heldItem != null) && (heldItem.getItem() instanceof IMemoryCard))
        {
            IMemoryCard memoryCard = (IMemoryCard) heldItem.getItem();

            String settingsName = memoryCard.getSettingsName(heldItem);

            //TODO: Do memorycard things

            return true;
        }

        return super.onActivate(entityPlayer, pos);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);

        if (data.hasKey(PartChiselingTerminal.INVENTORY_NBT_KEY))
        {
            NBTTagList nbtTagList = (NBTTagList) data.getTag(PartChiselingTerminal.INVENTORY_NBT_KEY);

            for (int listIndex = 0; listIndex < nbtTagList.tagCount(); listIndex++)
            {
                NBTTagCompound nbtCompound = nbtTagList.getCompoundTagAt(listIndex);

                int slotIndex = nbtCompound.getByte(PartChiselingTerminal.SLOT_NBT_KEY);

                if (this.isSlotSafe(slotIndex))
                {
                    ItemStack slotStack = ItemStack.loadItemStackFromNBT(nbtCompound);

                    // Is the slot the chisel slot slot?
                    if (slotIndex == PartChiselingTerminal.CHISEL_FILTER_SLOT_INDEX)
                    {
                        // Validate the block
                        System.out.println(slotStack);
                        if (!Helper.isBlockChiselable(Carving.chisel.getGroup(slotStack)))
                        {
                            // Invalid block data
                            slotStack = null;
                        }
                    }
                    System.out.println(slotStack);

                    this.slots[slotIndex] = slotStack;
                }
            }
        }

        if (data.hasKey(PartChiselingTerminal.SORT_ORDER_NBT_KEY))
        {
            this.sortingOrder = EnumCache.AE_SORT_ORDERS[data.getInteger(PartChiselingTerminal.SORT_ORDER_NBT_KEY)];
        }

        if (data.hasKey(PartChiselingTerminal.SORT_DIRECTION_NBT_KEY))
        {
            this.sortingDirection = EnumCache.AE_SORT_DIRECTIONS[data.getInteger(PartChiselingTerminal.SORT_DIRECTION_NBT_KEY)];
        }

        if (data.hasKey(PartChiselingTerminal.VIEW_MODE_NBT_KEY))
        {
            this.viewMode = EnumCache.AE_VIEW_ITEMS[data.getInteger(PartChiselingTerminal.VIEW_MODE_NBT_KEY)];
        }
    }

    public void registerListener(ContainerChiselingTerminal container)
    {
        if (!this.listeners.contains(container))
        {
            this.listeners.add(container);
        }
    }

    public void removeListener(ContainerChiselingTerminal container)
    {
        this.listeners.remove(container);
    }

    public void setSorts(SortOrder sortOrder, SortDir sortDirection, ViewItems viewMode)
    {
        this.sortingOrder = sortOrder;

        this.sortingDirection = sortDirection;

        this.viewMode = viewMode;

        this.markDirty();
    }

    @Override
    public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType)
    {
        super.writeToNBT(data, saveType);

        NBTTagList nbtList = new NBTTagList();

        for (int slotId = 0; slotId < PartChiselingTerminal.MY_INVENTORY_SIZE; slotId++)
        {
            if (this.slots[slotId] != null)
            {
                NBTTagCompound nbtCompound = new NBTTagCompound();

                nbtCompound.setByte(PartChiselingTerminal.SLOT_NBT_KEY, (byte) slotId);

                this.slots[slotId].writeToNBT(nbtCompound);

                nbtList.appendTag(nbtCompound);
            }
        }

        if (nbtList.tagCount() > 0)
        {
            data.setTag(PartChiselingTerminal.INVENTORY_NBT_KEY, nbtList);
        }

        if (this.sortingDirection != PartChiselingTerminal.DEFAULT_SORT_DIR)
        {
            data.setInteger(PartChiselingTerminal.SORT_DIRECTION_NBT_KEY, this.sortingDirection.ordinal());
        }

        if (this.sortingOrder != PartChiselingTerminal.DEFAULT_SORT_ORDER)
        {
            data.setInteger(PartChiselingTerminal.SORT_ORDER_NBT_KEY, this.sortingOrder.ordinal());
        }

        if (this.viewMode != PartChiselingTerminal.DEFAULT_VIEW_MODE)
        {
            data.setInteger(PartChiselingTerminal.VIEW_MODE_NBT_KEY, this.viewMode.ordinal());
        }
    }
}
