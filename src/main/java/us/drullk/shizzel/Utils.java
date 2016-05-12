package us.drullk.shizzel;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import team.chisel.api.carving.CarvingUtils;
import team.chisel.api.carving.ICarvingGroup;
import team.chisel.api.carving.ICarvingRegistry;
import team.chisel.api.carving.ICarvingVariation;

import java.util.List;

import static team.chisel.api.carving.CarvingUtils.getChiselRegistry;

public class Utils
{
    public static boolean isChiselableItem(ItemStack item)
    {
        return getChiselRegistry().getGroup(item) != null;
    }

    public static ItemStack getNextChiselVariationStack(ItemStack item)
    {
        ICarvingRegistry carvingRegistry = CarvingUtils.getChiselRegistry();

        if (carvingRegistry != null)
        {
            List<ItemStack> itemList = carvingRegistry.getItemsForChiseling(item);

            for (int i = 0; i < itemList.size(); i++)
            {
                if (itemList.get(i).equals(item))
                {
                    return itemList.get(i + 1 >= itemList.size() ? 0 : i + 1 );
                }
            }
        }

        return null;
    }

    // boolean defines success or failure
    public static boolean chiselBlockInWorld(World world, BlockPos pos)
    {
        IBlockState blockState = world.getBlockState(pos);
        ICarvingRegistry carvingRegistry = CarvingUtils.getChiselRegistry();

        if (carvingRegistry != null)
        {
            ICarvingGroup stateCarvingGroup = carvingRegistry.getGroup(blockState);

            if (stateCarvingGroup != null)
            {
                List<ICarvingVariation> variationList = stateCarvingGroup.getVariations();

                for (int i = 0; i < variationList.size(); i++)
                {
                    if (variationList.get(i).getBlockState().equals(world.getBlockState(pos)))
                    {
                        world.setBlockState(pos, variationList.get(i + 1 >= variationList.size() ? 0 : i + 1 ).getBlockState());
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
