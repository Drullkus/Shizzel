package us.drullk.shizzel.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.util.StatCollector;

public class Helper
{

    /*public enum MoonPhase {
    	FULL("full"),
    	WANING_GIBBOUS("gibbousWaning"),
    	WANING_HALF("halfWaning"),
    	WANING_CRESCENT("crescentWaning"),
    	NEW("new"),
    	WAXING_CRESCENT("crecentWaxing"),
    	WAXING_HALF("halfWaxing"),
    	WAXING_GIBBOUS("gibbousWaxing");

    	private String phaseName;

    	private MoonPhase(String name) {
    		this.phaseName = name;
    	}

    	public boolean isBetween(MoonPhase first, MoonPhase second) {
    		boolean flag = false;

    		if (first.ordinal() <= second.ordinal()) {
    			// Straightforward.
    			flag = first.ordinal() <= this.ordinal() && this.ordinal() <= second.ordinal();
    		} else {
    			// Wraps around the boundary.
    			flag = (first.ordinal() <= this.ordinal() && this.ordinal() <= WAXING_GIBBOUS.ordinal()) || (FULL.ordinal() <= this.ordinal() && this.ordinal() <= second.ordinal());
    		}

    		return flag;
    	}

    	public String getLocalizedName() {
    		return getLocalizedString("moon." + this.phaseName);
    	}

    	public String getLocalizedNameAlt() {
    		String response = getLocalizedString("moon.alt." + this.phaseName);
    		if (response.isEmpty()) {
    			return getLocalizedName();
    		}
    		return response;
    	}

    	public static MoonPhase getMoonPhase(World w) {
    		return getMoonPhaseFromTime(w.getWorldTime());
    	}

    	public static MoonPhase getMoonPhaseFromTime(long time) {
    		return MoonPhase.values()[(int) ((time - 6000) / 24000L) % 8];
    	}
    }//*/

    public static String getLocalizedString(String key, Object... objects)
    {
        if (StatCollector.canTranslate(key))
        {
            return String.format(StatCollector.translateToLocal(key), objects);
        }
        else
        {
            return String.format(StatCollector.translateToFallback(key), objects);
        }
    }

    private static FMLCommonHandler FCH = FMLCommonHandler.instance();

    public static final boolean isClientSide()
    {
        return Helper.FCH.getEffectiveSide().isClient();
    }

    public static final boolean isServerSide()
    {
        return Helper.FCH.getEffectiveSide().isServer();
    }
}
