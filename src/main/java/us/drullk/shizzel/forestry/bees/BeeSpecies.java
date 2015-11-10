package us.drullk.shizzel.forestry.bees;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IIconProvider;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleTolerance;
import forestry.api.genetics.IClassification;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import us.drullk.shizzel.utils.Helper;

public enum BeeSpecies implements IAlleleBeeSpecies,IIconProvider
{

    CHISEL("Chiseling", "chisum", BeeClassification.CHISEL, 0x7d3903, 0xd8d8d8,
            EnumTemperature.NORMAL, EnumHumidity.NORMAL, true, false, true, false, false);

    public static void setupBeeSpecies()
    {
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
            boolean isSpeciesDominant, boolean isSpeciesNocturnal)
    {
        this.uid = "shizzel.species" + speciesName;
        this.dominant = isSpeciesDominant;
        AlleleManager.alleleRegistry.registerAllele(this);
        this.binomial = genusName;
        this.authority = "Drullkus";
        this.primaryColour = firstColour;
        this.secondaryColour = secondColour;
        this.temperature = preferredTemp;
        this.humidity = preferredHumidity;
        this.hasEffect = hasGlowEffect;
        this.isSecret = isSpeciesSecret;
        this.isCounted = isSpeciesCounted;
        this.products = new HashMap<ItemStack, Float>();
        this.specialties = new HashMap<ItemStack, Float>();
        this.branch = classification;
        this.branch.addMemberSpecies(this);
        this.isNocturnal = isSpeciesNocturnal;
        this.isActive = true;
    }

    public IAllele[] getGenome()
    {
        return this.genomeTemplate;
    }

    public BeeSpecies addProduct(ItemStack produce, float chance)
    {
        this.products.put(produce, chance);
        return this;
    }

    public BeeSpecies addSpecialty(ItemStack produce, float chance)
    {
        this.specialties.put(produce, chance);
        return this;
    }

    public ItemStack getBeeItem(EnumBeeType beeType)
    {
        return BeeManager.beeRoot.getMemberStack(BeeManager.beeRoot.getBee(null, BeeManager.beeRoot.templateAsGenome(this.genomeTemplate)), beeType.ordinal());
    }

    @Override
    public String getName()
    {
        return Helper.getLocalizedString(this.getUID());
    }

    @Override
    public String getDescription()
    {
        return Helper.getLocalizedString(this.getUID() + ".description");
    }

    @Override
    public String getUnlocalizedName()
    {
        return this.getUID();
    }

    @Override
    public EnumTemperature getTemperature()
    {
        return this.temperature;
    }

    @Override
    public EnumHumidity getHumidity()
    {
        return this.humidity;
    }

    @Override
    public boolean hasEffect()
    {
        return this.hasEffect;
    }

    public BeeSpecies setInactive()
    {
        this.isActive = false;
        AlleleManager.alleleRegistry.blacklistAllele(this.getUID());
        return this;
    }

    public boolean isActive()
    {
        return this.isActive;
    }

    @Override
    public boolean isSecret()
    {
        return this.isSecret;
    }

    @Override
    public boolean isCounted()
    {
        return this.isCounted;
    }

    @Override
    public String getBinomial()
    {
        return this.binomial;
    }

    @Override
    public String getAuthority()
    {
        return this.authority;
    }

    @Override
    public IClassification getBranch()
    {
        return this.branch;
    }

    @Override
    public Map<ItemStack, Float> getProductChances()
    {
        return this.products;
    }

    @Override
    public Map<ItemStack, Float> getSpecialtyChances()
    {
        return this.specialties;
    }

    @Override
    public String getUID()
    {
        return this.uid;
    }

    @Override
    public boolean isDominant()
    {
        return this.dominant;
    }

    @Override
    public IBeeRoot getRoot()
    {
        return BeeManager.beeRoot;
    }

    @Override
    public boolean isNocturnal()
    {
        return this.isNocturnal;
    }

    @Override
    public boolean isJubilant(IBeeGenome genome, IBeeHousing housing)
    {
        return true;
    }

    public void registerGenomeTemplate(IAllele[] genome)
    {
        this.genomeTemplate = genome;
        BeeManager.beeRoot.registerTemplate(this.getUID(), genome);
    }

    @Override
    public int getIconColour(int renderPass)
    {
        int value = 0xffffff;
        if (renderPass == 0)
        {
            if (this.primaryColour == -1)
            {
                int hue = (int) (System.currentTimeMillis() >> 2) % 360;
                value = Color.getHSBColor(hue / 360f, 0.75f, 0.80f).getRGB();
            }
            else
            {
                value = this.primaryColour;
            }
        }
        else if (renderPass == 1)
        {
            if (this.secondaryColour == -1)
            {
                int hue = (int) (System.currentTimeMillis() >> 3) % 360;
                hue += 60;
                hue = hue % 360;
                value = Color.getHSBColor(hue / 360f, 0.5f, 0.6f).getRGB();
            }
            else
            {
                value = this.secondaryColour;
            }
        }
        return value;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIconProvider getIconProvider()
    {
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(EnumBeeType type, int renderPass)
    {
        return this.icons[type.ordinal()][Math.min(renderPass, 2)];
    }

    @Override
    public int getComplexity()
    {
        return 1 + this.getMutationPathLength(this, new ArrayList<IAllele>());
    }

    private int getMutationPathLength(IAllele species, ArrayList<IAllele> excludeSpecies)
    {
        int own = 1;
        int highest = 0;
        excludeSpecies.add(species);

        for (IMutation mutation : this.getRoot().getPaths(species, EnumBeeChromosome.SPECIES))
        {
            if (!excludeSpecies.contains(mutation.getAllele0()))
            {
                int otherAdvance = this.getMutationPathLength(mutation.getAllele0(), excludeSpecies);
                if (otherAdvance > highest)
                {
                    highest = otherAdvance;
                }
            }
            if (!excludeSpecies.contains(mutation.getAllele1()))
            {
                int otherAdvance = this.getMutationPathLength(mutation.getAllele1(), excludeSpecies);
                if (otherAdvance > highest)
                {
                    highest = otherAdvance;
                }
            }
        }

        return own + (highest > 0 ? highest : 0);
    }

    @Override
    public float getResearchSuitability(ItemStack itemStack)
    {
        if (itemStack == null)
        {
            return 0f;
        }

        for (ItemStack product : this.products.keySet())
        {
            if (itemStack.isItemEqual(product))
            {
                return 1f;
            }
        }

        for (ItemStack specialty : this.specialties.keySet())
        {
            if (specialty.isItemEqual(itemStack))
            {
                return 1f;
            }
        }

        if (itemStack.getItem() == GameRegistry.findItem("Forestry", "honeyDrop"))
        {
            return 0.5f;
        }
        else if (itemStack.getItem() == GameRegistry.findItem("Forestry", "honeydew"))
        {
            return 0.7f;
        }
        else if (itemStack.getItem() == GameRegistry.findItem("Forestry", "beeCombs"))
        {
            return 0.4f;
        }
        else if (this.getRoot().isMember(itemStack))
        {
            return 1.0f;
        }
        else
        {
            for (Map.Entry<ItemStack, Float> catalyst : BeeManager.beeRoot.getResearchCatalysts().entrySet())
            {
                if (OreDictionary.itemMatches(itemStack, catalyst.getKey(), false))
                {
                    return catalyst.getValue();
                }
            }
        }

        return 0f;
    }

    @Override
    public ItemStack[] getResearchBounty(World world, GameProfile researcher, IIndividual individual, int bountyLevel)
    {
        System.out.println("Bounty level: " + bountyLevel);
        ArrayList<ItemStack> bounty = new ArrayList<ItemStack>();

        if (world.rand.nextFloat() < ((10f / bountyLevel)))
        {
            Collection<? extends IMutation> resultantMutations = this.getRoot().getCombinations(this);
            if (resultantMutations.size() > 0)
            {
                IMutation[] candidates = resultantMutations.toArray(new IMutation[resultantMutations.size()]);
                bounty.add(AlleleManager.alleleRegistry.getMutationNoteStack(researcher, candidates[world.rand.nextInt(candidates.length)]));
            }
        }

        for (ItemStack product : this.products.keySet())
        {
            ItemStack copy = product.copy();
            copy.stackSize = 1 + world.rand.nextInt(bountyLevel / 2);
            bounty.add(copy);
        }

        for (ItemStack specialty : this.specialties.keySet())
        {
            ItemStack copy = specialty.copy();
            copy.stackSize = world.rand.nextInt(bountyLevel / 3);
            if (copy.stackSize > 0)
            {
                bounty.add(copy);
            }
        }

        return bounty.toArray(new ItemStack[bounty.size()]);
    }

    @Override
    public String getEntityTexture()
    {
        return "/gfx/forestry/entities/bees/honeyBee.png";
    }

    @Override
    public void registerIcons(IIconRegister itemMap)
    {
        this.icons = new IIcon[EnumBeeType.values().length][3];

        String root = this.getIconPath();

        IIcon body1 = itemMap.registerIcon(root + "body1");

        for (int i = 0; i < EnumBeeType.values().length; i++)
        {
            if (EnumBeeType.values()[i] == EnumBeeType.NONE)
            {
                continue;
            }

            this.icons[i][0] = itemMap.registerIcon(root + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".outline");
            this.icons[i][1] = (EnumBeeType.values()[i] != EnumBeeType.LARVAE) ? body1 : itemMap.registerIcon(root
                    + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body");
            this.icons[i][2] = itemMap.registerIcon(root + EnumBeeType.values()[i].toString().toLowerCase(Locale.ENGLISH) + ".body2");
        }
    }

    private String getIconPath()
    {
        String value;

        switch (this)
        {
        case CHISEL:
            value = "shizzel:bees/chisel/";
            break;

        default:
            value = "forestry:bees/default/";
            break;
        }

        return value;
    }

    public boolean canWorkInTemperature(EnumTemperature temp)
    {
        IAlleleTolerance tolerance = (IAlleleTolerance) this.genomeTemplate[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()];
        return AlleleManager.climateHelper
                .isWithinLimits(temp, EnumHumidity.NORMAL, this.temperature, tolerance.getValue(), EnumHumidity.NORMAL, EnumTolerance.NONE);
    }

    public boolean canWorkInHumidity(EnumHumidity humid)
    {
        IAlleleTolerance tolerance = (IAlleleTolerance) this.genomeTemplate[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()];
        return AlleleManager.climateHelper.isWithinLimits(EnumTemperature.NORMAL, humid, EnumTemperature.NORMAL, EnumTolerance.NONE, this.humidity,
                tolerance.getValue());
    }

    // --------- Unused Functions ---------------------------------------------

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(short texUID)
    {
        return this.icons[0][0];
    }
}
