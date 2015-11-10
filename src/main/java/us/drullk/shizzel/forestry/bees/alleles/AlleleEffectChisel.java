package us.drullk.shizzel.forestry.bees.alleles;

import java.util.List;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.IEffectData;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import team.chisel.api.carving.ICarvingGroup;
import team.chisel.api.carving.ICarvingVariation;
import team.chisel.carving.Carving;
import team.chisel.utils.General;
import us.drullk.shizzel.forestry.bees.BeeManager;

public class AlleleEffectChisel extends AlleleEffect
{
    public AlleleEffectChisel(String id, boolean isDominant, int timeoutBeeTicks)
    {
        super(id, isDominant, timeoutBeeTicks);
    }

    @Override
    public IEffectData validateStorage(IEffectData storedData)
    {
        if (storedData == null || !(storedData instanceof us.drullk.shizzel.forestry.bees.alleles.EffectData))
        {
            storedData = new us.drullk.shizzel.forestry.bees.alleles.EffectData(1, 0, 0);
        }
        return storedData;
    }

    @Override
    protected IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing)
    {
        World world = housing.getWorld();
        ChunkCoordinates coords = housing.getCoordinates();
        IBeeModifier beeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);

        // Get random coords within territory
        int xRange = (int) (beeModifier.getTerritoryModifier(genome, 1f) * genome.getTerritory()[0]);
        int yRange = (int) (beeModifier.getTerritoryModifier(genome, 1f) * genome.getTerritory()[1]);
        int zRange = (int) (beeModifier.getTerritoryModifier(genome, 1f) * genome.getTerritory()[2]);

        int xCoord = coords.posX + world.rand.nextInt(xRange) - xRange / 2;
        int yCoord = coords.posY + world.rand.nextInt(yRange) - yRange / 2;
        int zCoord = coords.posZ + world.rand.nextInt(zRange) - zRange / 2;

        // Future: https://github.com/LatvianModder/FTBUtilities/blob/1.7.10/src/main/java/latmod/ftbu/mod/handlers/FTBUPlayerEventHandler.java#L169
        // LatMod will implement a canInteract(World, UUID, x, y, z, leftClick) version, will help prevent griefing

        Block block = world.getBlock(xCoord, yCoord, zCoord);
        int metadata = world.getBlockMetadata(xCoord, yCoord, zCoord);

        //Shizzel.logger.info("DEBUG - " + block.getUnlocalizedName());

        if ((block != null) && (block != Blocks.air) && (block != Blocks.wooden_door) && (Item.getItemFromBlock(block) != null))
        {
            //Shizzel.logger.info("DEBUG - " + block.getUnlocalizedName());

            if (Carving.chisel.getGroup(block, metadata) != null)
            {
                ICarvingGroup group = Carving.chisel.getGroup(block, metadata);

                List<ICarvingVariation> list = group.getVariations();

                main:
                for (ItemStack stack : OreDictionary.getOres(group.getOreName()))
                {
                    ICarvingVariation v = General.getVariation(stack);
                    for (ICarvingVariation check : list)
                    {
                        if (check.getBlock() == v.getBlock() && check.getBlockMeta() == v.getBlockMeta())
                        {
                            continue main;
                        }
                    }
                    list.add(General.getVariation(stack));
                }

                ICarvingVariation[] variations = list.toArray(new ICarvingVariation[] {});

                for (int i = 0; i < variations.length; i++)
                {
                    ICarvingVariation v = variations[i];

                    if (v.getBlock() == block && v.getBlockMeta() == metadata)
                    {
                        //Shizzel.logger.info("DEBUG - " + block.getUnlocalizedName());
                        world.setBlock(xCoord, yCoord, zCoord, variations[i + 1 < variations.length ? i + 1 : 0].getBlock(), variations[i + 1 < variations.length ? i + 1 : 0].getBlockMeta(), 3);
                    }
                }
            }
        }
        storedData.setInteger(0, 0);

        return storedData;
    }
}
