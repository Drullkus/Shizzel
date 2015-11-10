package us.drullk.shizzel.forestry.bees;

import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.AlleleManager;

public class BeeManager
{
    public static IBeeRoot beeRoot;

    public static void getBeeRoot()
    {
        beeRoot = (IBeeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
    }

    public static void setupAlleles()
    {
        Allele.setupAdditionalAlleles();
        BeeSpecies.setupBeeSpecies();
        //Allele.registerDeprecatedAlleleReplacements(); //Not used as there are no alleles to remove/exchange
    }

    public static void lateBeeInit()
    {
        BeeMutation.setupMutations();
        BeeProductHelper.initBaseProducts();
    }
}
