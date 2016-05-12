package us.drullk.shizzel.block;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.ShizzelProps;
import us.drullk.shizzel.tileentity.TileAutoChisel;

public class Blocks {
    public static BlockMachine autoChiseler;

    public static void preInit()
    {
        autoChiseler = new BlockMachine();
        autoChiseler.setRegistryName(ShizzelProps.MOD_ID + ":autochiseler");

        GameRegistry.register(autoChiseler);
        GameRegistry.register(new ItemBlock(autoChiseler).setRegistryName(autoChiseler.getRegistryName()));
    }

    public static void init()
    {
        GameRegistry.registerTileEntity(TileAutoChisel.class, "autoshizzel");
        Shizzel.proxy.makeModel(autoChiseler, "autochiseler");
    }

    public static void postInit()
    {

    }
}
