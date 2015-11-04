package us.drullk.shizzel;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.drullk.shizzel.forestry.bees.BeeManager;
import us.drullk.shizzel.proxy.CommonProxy;

@Mod(modid = Shizzel.MOD_ID, name = Shizzel.MOD_NAME, version = Shizzel.VERSION, dependencies = "required-after:chisel;after:Forestry")
public class Shizzel {

	public static final String MOD_ID = "shizzel";
	public static final String MOD_NAME = "Shizzel";
	public static final String VERSION = "@VERSION@";

	public static final Logger logger = LogManager.getLogger(MOD_NAME);

	@Mod.Instance(MOD_ID)
	public static Shizzel instance;

	public Shizzel() {
		//Stuff
	}

	@SidedProxy(clientSide = "us.drullk.shizzel.proxy.ClientProxy", serverSide = "us.drullk.shizzel.proxy.CommonProxy")
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger.info("Shizzel's shenangans have begun! Prepare yourself, Chisel!");
		proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();

		if (Loader.isModLoaded("Forestry"))
		{
			logger.info("Chiseling Bees: Many hours of fun under endless hours of unchiseling.");

			BeeManager.getBeeRoot();
			BeeManager.setupAlleles();
		}
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();

		if (Loader.isModLoaded("Forestry"))
		{
			BeeManager.lateBeeInit();
		}
	}
}
