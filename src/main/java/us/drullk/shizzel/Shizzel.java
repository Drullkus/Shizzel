package us.drullk.shizzel;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.drullk.shizzel.proxy.CommonProxy;

@Mod(modid = Shizzel.MOD_ID, name = Shizzel.MOD_NAME, version = Shizzel.VERSION, dependencies = "after:Chisel;after:Forestry")
public class Shizzel {

	public static final String MOD_ID = "chisel";
	public static final String MOD_NAME = "Chisel";
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
		proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
