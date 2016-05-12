package us.drullk.shizzel.item;

import net.minecraftforge.fml.common.registry.GameRegistry;
import us.drullk.shizzel.ShizzelProps;

public class Items {
    public static BeatingStick beatingStick;

    public static void preInit()
    {
        beatingStick = new BeatingStick();
        beatingStick.setRegistryName(ShizzelProps.MOD_ID + ":" + beatingStick.getUnlocalizedName());

        GameRegistry.register(beatingStick);
    }

    public static void init()
    {

    }

    public static void postInit()
    {

    }
}
