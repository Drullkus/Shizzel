package us.drullk.shizzel.appEng;

import java.util.EnumSet;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class AEGridBlock
        implements IGridBlock
{
    /**
     * The part using this gridblock.
     */
    protected AEPartAbstract part;

    /**
     * Create the gridblock for the specified part.
     *
     * @param part
     */
    public AEGridBlock(final AEPartAbstract part)
    {
        this.part = part;
    }

    @Override
    public EnumSet<ForgeDirection> getConnectableSides()
    {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    public IEnergyGrid getEnergyGrid()
    {
        // Get the grid
        IGrid grid = this.getGrid();

        // Ensure we have a grid
        if (grid == null)
        {
            return null;
        }

        // Return the energy grid
        return grid.getCache(IEnergyGrid.class);
    }

    @Override
    public EnumSet<GridFlags> getFlags()
    {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    public final IGrid getGrid()
    {
        // Get the grid node
        IGridNode node = this.part.getGridNode();

        // Ensure we have a node
        if (node != null)
        {
            // Get the grid
            return node.getGrid();
        }

        return null;
    }

    @Override
    public AEColor getGridColor()
    {
        // Return transparent.
        return AEColor.Transparent;
    }

    /**
     * Gets how much power the part is using.
     */
    @Override
    public double getIdlePowerUsage()
    {
        return this.part.getIdlePowerUsage();
    }

    /**
     * Gets the AE item monitor for the grid.
     *
     * @return Monitor if valid grid, null otherwise.
     */
    public IMEMonitor<IAEItemStack> getItemMonitor()
    {
        // Get the storage grid
        IStorageGrid storageGrid = this.getStorageGrid();

        // Do we have a storage grid?
        if (storageGrid == null)
        {
            return null;
        }

        // Return the storage grid's item monitor.
        return storageGrid.getItemInventory();
    }

    /**
     * Gets the location of the part.
     */
    @Override
    public DimensionalCoord getLocation()
    {
        return this.part.getLocation();
    }

    /**
     * Gets the part
     */
    @Override
    public IGridHost getMachine()
    {
        return this.part;
    }

    /**
     * Gets the an itemstack based on the parts state.
     */
    @Override
    public ItemStack getMachineRepresentation()
    {
        return this.part.getItemStack(PartItemStack.Network);
    }

    /**
     * Gets the security grid
     *
     * @return
     */
    public ISecurityGrid getSecurityGrid()
    {
        // Get the grid.
        IGrid grid = this.getGrid();

        // Do we have a grid?
        if (grid == null)
        {
            return null;
        }

        // Get the security grid from the cache.
        return (ISecurityGrid) grid.getCache(ISecurityGrid.class);
    }

    /**
     * Gets the storage grid.
     *
     * @return
     */
    public IStorageGrid getStorageGrid()
    {
        // Get the grid.
        IGrid grid = this.getGrid();

        // Do we have a grid?
        if (grid == null)
        {
            return null;
        }

        // Get the storage grid from the cache.
        return (IStorageGrid) grid.getCache(IStorageGrid.class);
    }

    @Override
    public void gridChanged()
    {
        // Ignored
    }

    /**
     * Parts are not world accessable
     */
    @Override
    public boolean isWorldAccessible()
    {
        return false;
    }

    @Override
    public void onGridNotification(final GridNotification notification)
    {
        // Ignored
    }

    /**
     * Called to update the grid and the channels used.
     */
    @Override
    public final void setNetworkStatus(final IGrid grid, final int usedChannels)
    {
    }

}
