package us.drullk.shizzel.forestry.bees;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.*;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import us.drullk.shizzel.proxy.CommonProxy;
import us.drullk.shizzel.utils.Helper;

import java.awt.*;
import java.util.*;

public enum BeeSpecies implements IAlleleBeeSpecies, IIconProvider {

	CHISEL("Chiseling", "chisum", BeeClassification.CHISEL, 0x7d3903, 0xd8d8d8,
			EnumTemperature.NORMAL, EnumHumidity.NORMAL, true, false, true, false, false);

	public static void setupBeeSpecies() {
		CHISEL.registerGenomeTemplate(BeeGenomeManager.getTemplateChisel());
	}

	private String binomial;
	private String authority;
	private int primaryColour;
	private int secondaryColour;
	private EnumTemperature temperature;
	private EnumHumidity humidity;
	private boolean hasEffect;
	private boolean isSecret;
	private boolean isCounted;
	private boolean isActive;
	private boolean isNocturnal;
	private IClassification branch;
	private HashMap<ItemStack, Float> products;
	private HashMap<ItemStack, Float> specialties;
	private IAllele genomeTemplate[];
	private String uid;
	private boolean dominant;

	@SideOnly(Side.CLIENT)
	private IIcon[][] icons;

	private BeeSpecies(String speciesName, String genusName, IClassification classification, int firstColour, int secondColour,
			EnumTemperature preferredTemp, EnumHumidity preferredHumidity, boolean hasGlowEffect, boolean isSpeciesSecret, boolean isSpeciesCounted,
			boolean isSpeciesDominant, boolean isSpeciesNocturnal) {
		this.uid = "shizzel.species" + speciesName;
		this.dominant = isSpeciesDominant;
		AlleleManager.alleleRegistry.registerAllele(this);
		binomial = genusName;
		authority = "Drullkus";
		primaryColour = firstColour;
		secondaryColour = secondColour;
		temperature = preferredTemp;
		humidity = preferredHumidity;
		hasEffect = hasGlowEffect;
		isSecret = isSpeciesSecret;
		isCounted = isSpeciesCounted;
		products = new HashMap<ItemStack, Float>();
		specialties = new HashMap<ItemStack, Float>();
		this.branch = classification;
		this.branch.addMemberSpecies(this);
		this.isNocturnal = isSpeciesNocturnal;
		this.isActive = true;
	}

	public IAllele[] getGenome() {
		return genomeTemplate;
	}

	public BeeSpecies addProduct(ItemStack produce, float chance) {
		products.put(produce, chance);
		return this;
	}

	public BeeSpecies addSpecialty(ItemStack produce, float chance) {
		specialties.put(produce, chance);
		return this;
	}

	public ItemStack getBeeItem(EnumBeeType beeType) {
		return BeeManager.beeRoot.getMemberStack(BeeManager.beeRoot.getBee(null, BeeManager.beeRoot.templateAsGenome(genomeTemplate)), beeType.ordinal());
	}

	@Override
	public String getName() {
		return Helper.getLocalizedString(getUID());
	}

	@Override
	public String getDescription() {
		return Helper.getLocalizedString(getUID() + ".description");
	}

	@Override
	public String getUnlocalizedName() {
		return getUID();
	}

	@Override
	public EnumTemperature getTemperature() {
		return temperature;
	}

	@Override
	public EnumHumidity getHumidity() {
		return humidity;
	}

	@Override
	public boolean hasEffect() {
		return hasEffect;
	}

	public BeeSpecies setInactive() {
		this.isActive = false;
		AlleleManager.alleleRegistry.blacklistAllele(this.getUID());
		return this;
	}

	public boolean isActive() {
		return this.isActive;
	}

	@Override
	public boolean isSecret() {
		return isSecret;
	}

	@Override
	public boolean isCounted() {
		return isCounted;
	}

	@Override
	public String getBinomial() {
		return binomial;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public IClassification getBranch() {
		return this.branch;
	}

	@Override
	public Map<ItemStack, Float> getProductChances() {
		return products;
	}

	@Override
	public Map<ItemStack, Float> getSpecialtyChances() {
		return specialties;
	}

	@Override
	public String getUID() {
		return this.uid;
	}

	@Override
	public boolean isDominant() {
		return this.dominant;
	}

	@Override
	public IBeeRoot getRoot() {
		return BeeManager.beeRoot;
	}

	@Override
	public boolean isNocturnal() {
		return this.isNocturnal;
	}

	@Override
	public boolean isJubilant(IBeeGenome genome, IBeeHousing housing) {
		return true;
	}

	public void registerGenomeTemplate(IAllele[] genome) {
		genomeTemplate = genome;
		BeeManager.beeRoot.registerTemplate(getUID(), genome);
	}

	@Override
	public int getIconColour(int renderPass) {
		int value = 0xffffff;
		if (renderPass == 0) {
			if (this.primaryColour == -1) {
				int hue = (int) (System.currentTimeMillis() >> 2) % 360;
				value = Color.getHSBColor(hue / 360f, 0.75f, 0.80f).getRGB();
			} else {
				value = this.primaryColour;
			}
		} else if (renderPass == 1) {
			if (this.secondaryColour == -1) {
				int hue = (int) (System.currentTimeMillis() >> 3) % 360;
				hue += 60;
				hue = hue % 360;
				value = Color.getHSBColor(hue / 360f, 0.5f, 0.6f).getRGB();
			} else {
				value = this.secondaryColour;
			}
		}
		return value;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(EnumBeeType type, int renderPass) {
		return icons[type.ordinal()][Math.min(renderPass, 2)];
	}

	@Override
	public int getComplexity() {
		return 1 + getMutationPathLength(this, new ArrayList<IAllele>());
	}

	private int getMutationPathLength(IAllele species, ArrayList<IAllele> excludeSpecies) {
		int own = 1;
		int highest = 0;
		excludeSpecies.add(species);

		for (IMutation mutation : getRoot().getPaths(species, EnumBeeChromosome.SPECIES)) {
			if (!excludeSpecies.contains(mutation.getAllele0())) {
				int otherAdvance = getMutationPathLength(mutation.getAllele0(), excludeSpecies);
				if (otherAdvance > highest)
					highest = otherAdvance;
			}
			if (!excludeSpecies.contains(mutation.getAllele1())) {
				int otherAdvance = getMutationPathLength(mutation.getAllele1(), excludeSpecies);
				if (otherAdvance > highest)
					highest = otherAdvance;
			}
		}

		return own + (highest > 0 ? highest : 0);
	}

	@Override
	public float getResearchSuitability(ItemStack itemStack) {
		if (itemStack == null) {
			return 0f;
		}

		for (ItemStack product : this.products.keySet()) {
			if (itemStack.isItemEqual(product)) {
				return 1f;
			}
		}

		for (ItemStack specialty : this.specialties.keySet()) {
			if (specialty.isItemEqual(itemStack)) {
				return 1f;
			}
		}

		if (itemStack.getItem() == GameRegistry.findItem("Forestry", "honeyDrop")) {
			return 0.5f;
		} else if (itemStack.getItem() == GameRegistry.findItem("Forestry", "honeydew")) {
			return 0.7f;
		} else if (itemStack.getItem() == GameRegistry.findItem("Forestry", "beeCombs")) {
			return 0.4f;
		} else if (getRoot().isMember(itemStack)) {
			return 1.0f;
		} else {
			for (Map.Entry<ItemStack, Float> catalyst : BeeManager.beeRoot.getResearchCatalysts().entrySet()) {
				if (OreDictionary.itemMatches(itemStack, catalyst.getKey(), false)) {
					return catalyst.getValue();
				}
			}
		}

		return 0f;
	}

	@Override
	public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel) {
		System.out.println("Bounty level: " + bountyLevel);
		ArrayList<ItemStack> bounty = new ArrayList<ItemStack>();

		if (world.rand.nextFloat() < ((10f / bountyLevel))) {
			Collection<? extends IMutation> resultantMutations = getRoot().getCombinations(this);
			if (resultantMutations.size() > 0) {
				IMutation[] candidates = resultantMutations.toArray(new IMutation[resultantMutations.size()]);
				bounty.add(AlleleManager.alleleRegistry.getMutationNoteStack(researcher, candidates[world.rand.nextInt(candidates.length)]));
			}
		}

		for (ItemStack product : this.products.keySet()) {
			ItemStack copy = product.copy();
			copy.stackSize = 1 + world.rand.nextInt(bountyLevel / 2);
			bounty.add(copy);
		}

		for (ItemStack specialty : this.specialties.keySet()) {
			ItemStack copy = specialty.copy();
			copy.stackSize = world.rand.nextInt(bountyLevel / 3);
			if (copy.stackSize > 0) {
				bounty.add(copy);
			}
		}

		return bounty.toArray(new ItemStack[bounty.size()]);
	}

	@Override
	public String getEntityTexture() {
		return "/gfx/forestry/entities/bees/honeyBee.png";
	}

	@Override
	public void registerIcons(IIconRegister itemMap) {
		this.icons = new IIcon[EnumBeeType.values().length][3];

		String root = this.getIconPath();

		IIcon body1 = itemMap.registerIcon(root + "body1");

		for (int i = 0; i < EnumBeeType.values().length; i++) {
			if (EnumBeeType.values()[i] == EnumBeeType.NONE)
				continue;

			icons[i][0] = itemMap.registerIcon(root + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".outline");
			icons[i][1] = (EnumBeeType.values()[i] != EnumBeeType.LARVAE) ? body1 : itemMap.registerIcon(root
					+ EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body");
			icons[i][2] = itemMap.registerIcon(root + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body2");
		}
	}

	private String getIconPath() {
		String value;

		switch (this) {
		case CHISEL:
			value = "shizzel:bees/chisel/";
			break;

		default:
			value = "forestry:bees/default/";
			break;
		}

		return value;
	}

	public boolean canWorkInTemperature(EnumTemperature temp) {
		IAlleleTolerance tolerance = (IAlleleTolerance) genomeTemplate[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()];
		return AlleleManager.climateHelper
				.isWithinLimits(temp, EnumHumidity.NORMAL, temperature, tolerance.getValue(), EnumHumidity.NORMAL, EnumTolerance.NONE);
	}

	public boolean canWorkInHumidity(EnumHumidity humid) {
		IAlleleTolerance tolerance = (IAlleleTolerance) genomeTemplate[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()];
		return AlleleManager.climateHelper.isWithinLimits(EnumTemperature.NORMAL, humid, EnumTemperature.NORMAL, EnumTolerance.NONE, humidity,
				tolerance.getValue());
	}

	// --------- Unused Functions ---------------------------------------------

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(short texUID) {
		return icons[0][0];
	}
}
