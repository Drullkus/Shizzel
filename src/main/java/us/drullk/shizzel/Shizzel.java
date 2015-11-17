package us.drullk.shizzel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import us.drullk.shizzel.appEng.AppEngHandler;
import us.drullk.shizzel.forestry.Forestry;
import us.drullk.shizzel.networking.appEng.ChannelHandler;
import us.drullk.shizzel.proxy.CommonProxy;
import us.drullk.shizzel.utils.ShizzelGUIHandler;

@Mod(modid = Shizzel.MOD_ID, name = Shizzel.MOD_NAME, version = Shizzel.VERSION, dependencies = "required-after:chisel;" +
        "after:Forestry;" +
        "after:appliedenergistics2")
public class Shizzel
{
    public static final String MOD_ID = "shizzel";

    public static final String MOD_NAME = "Shizzel";

    public static final String VERSION = "@VERSION@";

    public static final Logger logger = LogManager.getLogger(MOD_NAME);

    @Instance(MOD_ID)
    public static Shizzel instance;

    public Shizzel()
    {
        // Constructor
    }

    public static CreativeTabs ShizzelTab = new CreativeTabs("Shizzel")
    {

        @Override
        public ItemStack getIconItemStack()
        {
            return new ItemStack(Items.cooked_porkchop, 1);
        }

        @Override
        public Item getTabIconItem()
        {
            return Items.cooked_porkchop;
        }
    };

    @SidedProxy(clientSide = "us.drullk.shizzel.proxy.ClientProxy", serverSide = "us.drullk.shizzel.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger.info("Shizzel's shenangans have begun! Prepare yourself, Chisel!");
        proxy.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new ShizzelGUIHandler());

        if (Loader.isModLoaded("appliedenergistics2"))
        {
            AppEngHandler.preInit();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();

        ChannelHandler.registerMessages();

        if (Loader.isModLoaded("Forestry"))
        {
            Forestry.init();
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();

        if (Loader.isModLoaded("Forestry"))
        {
            Forestry.postInit();
        }
    }
}
