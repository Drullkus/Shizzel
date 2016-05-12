package us.drullk.shizzel;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import us.drullk.shizzel.block.Blocks;
import us.drullk.shizzel.item.Items;
import us.drullk.shizzel.proxy.CommonProxy;

@Mod(modid = ShizzelProps.MOD_ID, version = ShizzelProps.VERSION, name = ShizzelProps.MOD_NAME)
public class Shizzel implements ShizzelProps
{
    @SidedProxy(serverSide = "us.drullk.shizzel.proxy.CommonProxy", clientSide = "us.drullk.shizzel.proxy.ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Blocks.preInit();
        Items.preInit();
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Blocks.init();
        Items.init();
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        Blocks.postInit();
        Items.postInit();
        proxy.postInit();
    }
}
