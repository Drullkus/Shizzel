package us.drullk.shizzel.appEng;

import appeng.api.networking.*;
import appeng.api.parts.PartItemStack;
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

	@Override
	public double getIdlePowerUsage() {
		return part.getPowerUsage();
	}

	@Override public EnumSet<GridFlags> getFlags() {
		return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
	}

	@Override public boolean isWorldAccessible() {
		return false;
	}

	@Override public DimensionalCoord getLocation() {
		return part.getLocation();
	}

	@Override public AEColor getGridColor() {
		return color;
	}

	@Override public void onGridNotification(GridNotification gridNotification) {

	}

	@Override public void setNetworkStatus(IGrid iGrid, int i)
	{
		grid = iGrid;
		usedChannels = i;
	}

	@Override public EnumSet<ForgeDirection> getConnectableSides() {
		return EnumSet.noneOf( ForgeDirection.class );
	}

	@Override public IGridHost getMachine() {
		return part;
	}

	@Override public void gridChanged() {

	}

	@Override public ItemStack getMachineRepresentation() {
		return part.getItemStack(PartItemStack.Network);
	}
}
