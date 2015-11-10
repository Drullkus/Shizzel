package us.drullk.shizzel.appEng;

import cpw.mods.fml.common.registry.GameRegistry;
import us.drullk.shizzel.appEng.enumList.AEItems;

public class AppEngHandler
{
    public static void preInit()
    {
        for(AEItems item : AEItems.values() )
        {
            GameRegistry.registerItem(item.getItem(), item.getInternalName());
        }
    }

    public static void init()
    {

    }

    public static void postInit()
    {

    }
}
