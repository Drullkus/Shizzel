package us.drullk.shizzel.appEng;

import appeng.api.networking.*;
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

import java.util.EnumSet;

public class AEGridBlock implements IGridBlock {

	private AEPartAbstract part;
	private AEColor color;
	private IGrid grid;
	private int usedChannels;

	public AEGridBlock(AEPartAbstract partAbstract)
	{
		this.part = partAbstract;
	}

    public IMEMonitor<IAEItemStack> getItemMonitor()
    {
        IStorageGrid storageGrid = this.getStorageGrid();

        if(storageGrid == null)
        {
            return null;
        }

        return storageGrid.getItemInventory();
    }

    public IStorageGrid getStorageGrid()
    {
        IGrid grid = this.getGrid();

        if(grid == null)
        {
            return null;
        }

        return (IStorageGrid)grid.getCache(IStorageGrid.class);
    }

    public final IGrid getGrid()
    {
        IGridNode node = this.part.getGridNode();

        if(node != null)
        {
            return node.getGrid();
        }

        return null;
    }

    public ISecurityGrid getSecurityGrid()
    {
        IGrid grid = this.getGrid();

        if( grid == null )
        {
            return null;
        }

        return (ISecurityGrid)grid.getCache( ISecurityGrid.class );
    }

	public IEnergyGrid getEnergyGrid()
	{
		// Get the grid
		IGrid grid = this.getGrid();

		// Ensure we have a grid
		if( grid == null )
		{
			return null;
		}

		// Return the energy grid
		return grid.getCache( IEnergyGrid.class );
	}

	@Override
	public double getIdlePowerUsage() {
		return this.part.getPowerUsage();
	}

	@Override public EnumSet<GridFlags> getFlags() {
		return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
	}

	@Override public boolean isWorldAccessible() {
		return false;
	}

	@Override public DimensionalCoord getLocation() {
		return this.part.getLocation();
	}

	@Override public AEColor getGridColor() {
		return (color != null ? color : AEColor.Transparent);
	}

	@Override public void onGridNotification(GridNotification gridNotification) {

	}

	@Override public void setNetworkStatus(IGrid iGrid, int i)
	{
		this.grid = iGrid;
		this.usedChannels = i;
	}

	@Override public EnumSet<ForgeDirection> getConnectableSides() {
		return EnumSet.noneOf( ForgeDirection.class );
	}

	@Override public IGridHost getMachine() {
		return this.part;
	}

	@Override public void gridChanged() {

	}

	@Override public ItemStack getMachineRepresentation() {
		return this.part.getItemStack(PartItemStack.Network);
	}
}
