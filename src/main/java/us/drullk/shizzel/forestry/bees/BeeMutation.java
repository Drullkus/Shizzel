package us.drullk.shizzel.forestry.bees;

import java.util.ArrayList;
import java.util.Collection;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.oredict.OreDictionary;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import us.drullk.shizzel.utils.Helper;

public class BeeMutation implements IBeeMutation {

	public static void setupMutations()
	{
		IAlleleBeeSpecies baseA, baseB;
		BeeMutation mutation;

		new BeeMutation(Allele.getBaseSpecies("Industrious"), Allele.getBaseSpecies("Steadfast"), BeeSpecies.CHISEL, 10);
	}

	private IAlleleSpecies parent1;
	private IAlleleSpecies parent2;
	private IAllele mutationTemplate[];
	private int baseChance;
	private boolean isSecret;
	private boolean isMoonRestricted;
	//private Helper.MoonPhase moonPhaseStart;
	//private Helper.MoonPhase moonPhaseEnd;
	private float moonPhaseMutationBonus;
	private boolean requiresNight;
	private boolean requiresBlock;
	private Block requiredBlock;
	private int requiredBlockMeta;
	private String requiredBlockOreDictEntry;
	private String requiredBlockName;
	private BiomeDictionary.Type requiredBiomeType;

	public BeeMutation(IAlleleBeeSpecies species0, IAlleleBeeSpecies species1, BeeSpecies resultSpecies, int percentChance) {
		this(species0, species1, resultSpecies.getGenome(), percentChance);
	}

	public BeeMutation(IAlleleBeeSpecies species0, IAlleleBeeSpecies species1, IAllele[] resultSpeciesGenome, int percentChance) {
		this.parent1 = species0;
		this.parent2 = species1;
		this.mutationTemplate = resultSpeciesGenome;
		this.baseChance = percentChance;
		this.isSecret = false;
		this.isMoonRestricted = false;
		this.moonPhaseMutationBonus = -1f;
		this.requiresNight = false;
		this.requiresBlock = false;
		this.requiredBlockMeta = OreDictionary.WILDCARD_VALUE;
		this.requiredBlockOreDictEntry = null;
		this.requiredBiomeType = null;
		this.requiredBlockName = null;

		BeeManager.beeRoot.registerMutation(this);
	}

	@Override
	public float getChance(IBeeHousing housing, IAlleleBeeSpecies allele0, IAlleleBeeSpecies allele1, IBeeGenome genome0, IBeeGenome genome1) {
		float finalChance = 0f;
		float chance = this.baseChance * 1f;
		ChunkCoordinates housingCoords = housing.getCoordinates();

		if (this.arePartners(allele0, allele1)) {
			// This mutation applies. Continue calculation.
			/*if (this.moonPhaseStart != null && this.moonPhaseEnd != null) {
				// Only occurs during the phases.
				if (this.isMoonRestricted && !Helper.MoonPhase.getMoonPhase(housing.getWorld()).isBetween(this.moonPhaseStart, this.moonPhaseEnd)) {
					chance = 0;
				}
				else if (this.moonPhaseMutationBonus != -1f) {
					// There is a bonus to this mutation during moon phases...
					if (Helper.MoonPhase.getMoonPhase(housing.getWorld()).isBetween(this.moonPhaseStart, this.moonPhaseEnd)) {
						chance = (int)(chance * this.moonPhaseMutationBonus);
					}
				}
			}//*/

			if (this.requiresBlock) {
				Block blockBelow;
				int blockMeta;
				int i = 1;
				do {
					blockBelow = housing.getWorld().getBlock(housingCoords.posX, housingCoords.posY - i, housingCoords.posZ);
					blockMeta = housing.getWorld().getBlockMetadata(housingCoords.posX, housingCoords.posY - i, housingCoords.posZ);
					++i;
				}
				while (blockBelow != null && (blockBelow instanceof IBeeHousing || blockBelow == GameRegistry.findBlock("Forestry", "alveary")));

				if (this.requiredBlockOreDictEntry != null) {
					int[] dicId = OreDictionary.getOreIDs(new ItemStack(blockBelow, 1, blockMeta));
					if (dicId.length != 0) {
						if (!OreDictionary.getOreName(dicId[0]).equals(this.requiredBlockOreDictEntry)) {
							chance = 0;
						}
					}
					else {
						chance = 0;
					}
				}
				else if (this.requiredBlock != blockBelow ||
						(this.requiredBlockMeta != OreDictionary.WILDCARD_VALUE && this.requiredBlockMeta != blockMeta)) {
					chance = 0;
				}
			}

			if (this.requiredBiomeType != null) {
				BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(housing.getWorld().getBiomeGenForCoords(housingCoords.posX, housingCoords.posZ));
				boolean found = false;
				for (Type type : types) {
					if (this.requiredBiomeType == type) {
						found = true;
						break;
					}
				}
				if (!found) {
					chance = 0;
				}
			}

			if (this.requiresNight) {
				if (!housing.getWorld().isDaytime()) {
					chance = 0;
				}
			}

			IBeeModifier housingBeeModifier = BeeManager.beeRoot.createBeeHousingModifier(housing);
			IBeeModifier modeBeeModifier = BeeManager.beeRoot.getBeekeepingMode(housing.getWorld()).getBeeModifier();

			finalChance = Math.round(chance
					* housingBeeModifier.getMutationModifier(genome0, genome1, chance)
					* modeBeeModifier.getMutationModifier(genome0, genome1, chance));
		}

		return finalChance;
	}

	@Override
	public IAlleleSpecies getAllele0() {
		return parent1;
	}

	@Override
	public IAlleleSpecies getAllele1() {
		return parent2;
	}

	@Override
	public IAllele[] getTemplate() {
		return mutationTemplate;
	}

	@Override
	public float getBaseChance() {
		return baseChance;
	}

	@Override
	public boolean isPartner(IAllele allele) {
		return parent1.getUID().equals(allele.getUID()) || parent2.getUID().equals(allele.getUID());
	}

	@Override
	public IAllele getPartner(IAllele allele) {
		IAllele val = parent1;
		if (val.getUID().equals(allele.getUID()))
			val = parent2;
		return val;
	}

	@Override
	public Collection<String> getSpecialConditions() {
		ArrayList<String> conditions = new ArrayList<String>();

		/*if (this.isMoonRestricted && moonPhaseStart != null && moonPhaseEnd != null) {
			if (moonPhaseStart != moonPhaseEnd) {
				conditions.add(String.format(Helper.getLocalizedString("research.requiresPhase"),
						moonPhaseStart.getLocalizedNameAlt(), moonPhaseEnd.getLocalizedNameAlt()));
			}
			else {
				conditions.add(String.format(Helper.getLocalizedString("research.requiresPhaseSingle"),
						moonPhaseStart.getLocalizedName()));
			}
		}//*/

		if (this.requiresBlock) {
			if (this.requiredBlockName != null) {
				conditions.add(String.format(Helper.getLocalizedString("research.requiresBlock"),
						Helper.getLocalizedString(this.requiredBlockName)));
			}
			else if (this.requiredBlockOreDictEntry != null) {
				ArrayList<ItemStack> ores = OreDictionary.getOres(this.requiredBlockOreDictEntry);
				if (ores != null && 0 < ores.size()) {
					String displayName = ores.get(0).getDisplayName();
					conditions.add(String.format(Helper.getLocalizedString("research.requiresBlock"), displayName));
				}
			}
			else if (this.requiredBlock != null) {
				int meta = OreDictionary.WILDCARD_VALUE;
				if (this.requiredBlockMeta != OreDictionary.WILDCARD_VALUE) {
					meta = this.requiredBlockMeta;
				}
				String displayName = new ItemStack(this.requiredBlock, 1, meta).getDisplayName();
				conditions.add(String.format(Helper.getLocalizedString("research.requiresBlock"), displayName));
			}
		}

		if (this.requiredBiomeType != null) {
			String biomeName = this.requiredBiomeType.name().substring(0, 1) + this.requiredBiomeType.name().substring(1).toLowerCase();
			conditions.add(String.format(Helper.getLocalizedString("research.requiresBiome"), biomeName));
		}

		if (this.requiresNight) {
			conditions.add(Helper.getLocalizedString("research.requiresNight"));
		}

		return conditions;
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	public boolean arePartners(IAllele alleleA, IAllele alleleB) {
		return (this.parent1.getUID().equals(alleleA.getUID())) && this.parent2.getUID().equals(alleleB.getUID()) ||
				this.parent1.getUID().equals(alleleB.getUID()) && this.parent2.getUID().equals(alleleA.getUID());
	}

	public BeeMutation setSecret() {
		this.isSecret = true;

		return this;
	}

	public boolean isSecret() {
		return isSecret;
	}

	public BeeMutation setBlockRequired(Block block) {
		this.requiresBlock = true;
		this.requiredBlock = block;

		return this;
	}

	public BeeMutation setBlockAndMetaRequired(Block block, int meta) {
		this.requiresBlock = true;
		this.requiredBlock = block;
		this.requiredBlockMeta = meta;

		return this;
	}

	public BeeMutation setBlockRequired(String oreDictEntry) {
		this.requiresBlock = true;
		this.requiredBlockOreDictEntry = oreDictEntry;

		return this;
	}

	public BeeMutation setBlockRequiredNameOverride(String blockName) {
		this.requiredBlockName = blockName;

		return this;
	}

	/*public BeeMutation setMoonPhaseRestricted(Helper.MoonPhase phase) {
		setMoonPhaseRestricted(phase, phase);
		return this;
	}

	public BeeMutation setMoonPhaseRestricted(Helper.MoonPhase begin, Helper.MoonPhase end) {
		this.isMoonRestricted = true;
		this.moonPhaseStart = begin;
		this.moonPhaseEnd = end;

		return this;
	}

	public BeeMutation setMoonPhaseBonus(Helper.MoonPhase begin, Helper.MoonPhase end, float mutationBonus) {
		this.moonPhaseMutationBonus = mutationBonus;
		this.moonPhaseStart = begin;
		this.moonPhaseEnd = end;

		return this;
	}//*/

	public BeeMutation setBiomeRequired(BiomeDictionary.Type biomeType) {
		this.requiredBiomeType = biomeType;

		return this;
	}

	public BeeMutation setRequiresNight() {
		this.requiresNight = true;

		return this;
	}
}
