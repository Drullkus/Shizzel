package us.drullk.shizzel.forestry;

import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.forestry.bees.BeeManager;

public class Forestry
{
    public static void init()
    {
        Shizzel.logger.info("Chiseling Bees: Many hours of fun under endless hours of unchiseling.");
        BeeManager.getBeeRoot();
        BeeManager.setupAlleles();
    }

    public static void postInit()
    {
        BeeManager.lateBeeInit();
    }
}
