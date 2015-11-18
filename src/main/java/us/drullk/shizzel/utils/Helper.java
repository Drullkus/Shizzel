package us.drullk.shizzel.utils;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;
import team.chisel.api.carving.ICarvingGroup;
import team.chisel.api.carving.ICarvingVariation;
import team.chisel.carving.Carving;
import team.chisel.utils.General;

import java.util.List;

public class Helper
{

    /*
    public enum MoonPhase {

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
    }
    //*/

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

    public static boolean isClientSide()
    {
        return Helper.FCH.getEffectiveSide().isClient();
    }

    public static boolean isServerSide()
    {
        return Helper.FCH.getEffectiveSide().isServer();
    }

    public static ItemStack getNextStackInCarvingGroup(ItemStack itemStack)
    {
        ICarvingGroup carvingGroup = Carving.chisel.getGroup(itemStack);

        if(isBlockChiselable(carvingGroup))
        {
            ICarvingVariation[] variationsList = getChiselGroupSet(carvingGroup);

            for (int i = 0; i < variationsList.length; i++)
            {
                ICarvingVariation singleVariation = variationsList[i];

                if((singleVariation.getStack().getItem() == itemStack.getItem()) &&
                        (singleVariation.getStack().getItemDamage() == itemStack.getItemDamage()))
                {
                    return variationsList[i + 1 < variationsList.length ? i + 1 : 0].getStack();
                }
            }
        }

        return itemStack;
    }

    public static boolean isBlockChiselable(ICarvingGroup carvingGroup)
    {
        if (carvingGroup == null)
        {
            return false;
        }

        return true;
    }

    public static ICarvingVariation[] getChiselGroupSet(ICarvingGroup carvingGroup)
    {
        List<ICarvingVariation> carvingList = carvingGroup.getVariations();

        generateList:
        for(ItemStack is : OreDictionary.getOres(carvingGroup.getOreName()))
        {
            ICarvingVariation singleVariation = General.getVariation(is);

            for(ICarvingVariation entry : carvingList)
            {
                if (entry.getBlock() == singleVariation.getBlock() &&
                        entry.getBlockMeta() == singleVariation.getBlockMeta())
                {
                    continue generateList;
                }
            }
            carvingList.add(General.getVariation(is));
        }

        return carvingList.toArray(new ICarvingVariation[]{});
    }
}
