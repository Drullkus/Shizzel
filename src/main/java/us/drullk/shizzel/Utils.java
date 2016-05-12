package us.drullk.shizzel;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class Utils
{
    public static boolean isChiselableItem(ItemStack item)
    {
        return CarvingUtils.getChiselRegistry().getGroup(item) != null;
    }

    public static ItemStack getNextChiselVariationStack(ItemStack item)
    {
        List<ICarvingVariation> variationList = CarvingUtils.getChiselRegistry().getGroup(item).getVariations();

        for(int i = 0; i < variationList.size(); i++)
        {
            if(variationList.get(i).getStack().equals(item))
            {
                return variationList.get(i+1>=variationList.size() ? i+1 : 0).getStack();
            }
        }

        return null;
    }

    // boolean defines success or failure
    public static boolean chiselBlockInWorld(BlockPos pos)
    {
        return false;
    }
}
