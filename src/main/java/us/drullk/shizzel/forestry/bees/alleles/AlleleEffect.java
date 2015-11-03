package us.drullk.shizzel.forestry.bees.alleles;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IEffectData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import us.drullk.shizzel.forestry.bees.Allele;

import java.util.List;

public abstract class AlleleEffect extends Allele implements IAlleleBeeEffect
{
	protected int throttle;
	protected boolean combinable;

	public AlleleEffect(String id, boolean isDominant, int timeout)
	{
		super("effect" + id, isDominant);
		this.throttle = timeout;
		combinable = false;
	}

	@Override
	public boolean isCombinable()
	{
		return combinable;
	}

	public AlleleEffect setIsCombinable(boolean canCombine) {
		combinable = canCombine;
		return this;
	}

	@Override
	public abstract IEffectData validateStorage(IEffectData storedData);

	@Override
	public final IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing)
	{
		int count = storedData.getInteger(0);
		if (count >= this.throttle)
		{
			storedData = this.doEffectThrottled(genome, storedData, housing);
		}
		else
		{
			storedData.setInteger(0, count + 1);
		}
		return storedData;
	}

	/**
	 * @param genome
	 * @param storedData
	 * @param housing
	 * @return
	 */
	protected abstract IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

	@Override
	public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing)
	{
		return Allele.forestryBaseEffect.doFX(genome, storedData, housing);
	}

	@SuppressWarnings("unchecked")
	protected List<Entity> getEntitiesWithinRange(IBeeGenome genome, IBeeHousing housing)
	{
		// Get the size of the affected area
		int[] area = genome.getTerritory();
		ChunkCoordinates coords = housing.getCoordinates();

		// Calculate offset
		int[] min = new int[3];
		int[] max = new int[3];
		min[0] = coords.posX - area[0] / 2;
		max[0] = coords.posX + area[0] / 2;

		min[1] = coords.posY - area[1] / 2;
		max[1] = coords.posY + area[1] / 2;

		min[2] = coords.posZ - area[2] / 2;
		max[2] = coords.posZ + area[2] / 2;

		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(min[0], min[1], min[2], max[0], max[1], max[2]);
		return (List<Entity>)housing.getWorld().getEntitiesWithinAABB(EntityPlayer.class, bounds);
	}

}