package us.drullk.shizzel.forestry.bees;

import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleEffect;
import net.minecraft.util.StatCollector;
import us.drullk.shizzel.forestry.bees.alleles.AlleleEffectChisel;
import us.drullk.shizzel.utils.Helper;

public class Allele implements IAllele
{
	public static IAlleleBeeEffect forestryBaseEffect;
	public static IAlleleEffect chiselGrief;

	private String uid;
	private boolean dominant;

	public static void setupAdditionalAlleles() {
		forestryBaseEffect = (IAlleleBeeEffect) getBaseAllele("effectNone");

		chiselGrief = new AlleleEffectChisel("Chiseling", true, 100);
	}

	public Allele(String id, boolean isDominant) {
		this.uid = "shizzel." + id;
		this.dominant = isDominant;
		AlleleManager.alleleRegistry.registerAllele(this);
	}

	public static IAlleleBeeSpecies getBaseSpecies(String name) {
		return (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele((new StringBuilder()).append("forestry.species").append(name).toString());
	}

	@Override public String getUID() {
		return this.uid;
	}

	@Override public boolean isDominant() {
		return this.dominant;
	}

	@Override public String getName() {
		return Helper.getLocalizedString(getUID());
	}

	@Override public String getUnlocalizedName() {
		return this.uid;
	}

	//Helpers
	public static IAllele getBaseAllele(String name)
	{
		return AlleleManager.alleleRegistry.getAllele("forestry." + name);
	}
}
