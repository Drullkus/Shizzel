package us.drullk.shizzel.appEng.enumList;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import appeng.api.config.Upgrades;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import us.drullk.shizzel.appEng.AEPartAbstract;
import us.drullk.shizzel.appEng.PartChiselingTerminal;

public enum AEParts
{
    PartChiselingTerminal("shizzel.appengparts.terminal.chiseling", PartChiselingTerminal.class, null);

    private Class<? extends AEPartAbstract> partClass;

    private String groupName;

    private Map<Upgrades, Integer> upgrades = new HashMap<Upgrades, Integer>();

    AEParts(String unlocalizedName, final Class<? extends AEPartAbstract> partClass, final String groupName)
    {
        // Set the class
        this.partClass = partClass;

        // Set the group name
        this.groupName = groupName;
    }

    AEParts(String unlocalizedName, final Class<? extends AEPartAbstract> partClass, final String groupName,
            final Pair<Upgrades, Integer>... upgrades)
    {
        this(unlocalizedName, partClass, groupName);

        for (Pair<Upgrades, Integer> pair : upgrades)
        {
            this.upgrades.put(pair.getKey(), pair.getValue());
        }

    }

    public AEPartAbstract createPartInstance(final ItemStack itemStack) throws InstantiationException, IllegalAccessException
    {
        // Create a new instance of the part
        AEPartAbstract part = this.partClass.newInstance();

        // Setup based on the itemStack
        part.setupPartFromItem(itemStack);

        // Return the newly created part
        return part;

    }

    public Map<Upgrades, Integer> getUpgrades()
    {
        return this.upgrades;
    }

    public static AEParts getPartFromDamageValue(final ItemStack itemStack)
    {
        int clamped = MathHelper.clamp_int(itemStack.getItemDamage(), 0, AEParts.values().length - 1);

        return AEParts.values()[clamped];
    }

    public Map<Upgrades, Integer> getPartFromDamageValue()
    {
        return this.upgrades;
    }

    public String getGroupName()
    {
        return this.groupName;
    }

    public ItemStack getStack()
    {
        return AEItems.AEPartItem.getStackWithDamage(this.ordinal());
    }
}
