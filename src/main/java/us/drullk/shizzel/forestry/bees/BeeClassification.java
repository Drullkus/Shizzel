package us.drullk.shizzel.forestry.bees;

import java.util.ArrayList;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IClassification;
import us.drullk.shizzel.utils.Helper;

public enum BeeClassification implements IClassification
{

    CHISEL("Chisel", "Chisum");

    private String uID;

    private String latin;

    private ArrayList<IAlleleSpecies> species;

    private IClassification parent;

    private EnumClassLevel level;

    private BeeClassification(String name, String scientific)
    {
        this.uID = "classification." + name.toLowerCase();
        this.latin = scientific;
        this.level = IClassification.EnumClassLevel.GENUS;
        this.species = new ArrayList<IAlleleSpecies>();
        this.parent = AlleleManager.alleleRegistry.getClassification("family.apidae");
        AlleleManager.alleleRegistry.registerClassification(this);
    }

    @Override
    public EnumClassLevel getLevel()
    {
        return this.level;
    }

    @Override
    public String getUID()
    {
        return this.uID;
    }

    @Override
    public String getName()
    {
        return Helper.getLocalizedString(this.getUID());
    }

    @Override
    public String getScientific()
    {
        return this.latin;
    }

    @Override
    public String getDescription()
    {
        return Helper.getLocalizedString(this.getUID() + ".description");
    }

    @Override
    public IClassification[] getMemberGroups()
    {
        return null;
    }

    @Override
    public void addMemberGroup(IClassification group)
    {

    }

    @Override
    public IAlleleSpecies[] getMemberSpecies()
    {
        return this.species.toArray(new IAlleleSpecies[this.species.size()]);
    }

    @Override
    public void addMemberSpecies(IAlleleSpecies species)
    {
        if (!this.species.contains(species))
        {
            this.species.add(species);
        }
    }

    @Override
    public IClassification getParent()
    {
        return this.parent;
    }

    @Override
    public void setParent(IClassification parent)
    {
        this.parent = parent;
    }
}
