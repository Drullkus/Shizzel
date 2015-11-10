package us.drullk.shizzel.forestry.bees;

import static us.drullk.shizzel.forestry.bees.BeeSpecies.CHISEL;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class BeeProductHelper
{

    public static void initBaseProducts()
    {
        CHISEL.addProduct(new ItemStack(GameRegistry.findItem("Forestry", "beeCombs"), 1, 3), 0.15f);
    }
}
