package us.drullk.shizzel.appEng;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.implementations.items.IItemGroup;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.enumList.AEParts;

import java.util.Map;
import java.util.Set;

public class ItemAEPart extends Item implements IPartItem, IItemGroup
{
    public ItemAEPart()
    {
        this.setMaxDamage( 0 );

        this.setHasSubtypes( true );

        AEApi.instance().partHelper().setItemBusRenderer( this );

        Map<Upgrades, Integer> possibleUpgradesList;

        for(AEParts part : AEParts.values() )
        {
            possibleUpgradesList = part.getUpgrades();

            for( Upgrades upgrade : possibleUpgradesList.keySet() )
            {
                upgrade.registerItem( new ItemStack( this, 1, part.ordinal() ), possibleUpgradesList.get( upgrade ).intValue() );
            }
        }
    }

    @Override
    public String getUnlocalizedGroupName(Set<ItemStack> set, ItemStack itemStack) {
        return AEParts.getPartFromDamageValue( itemStack ).getGroupName();
    }

    @Override
    public String getUnlocalizedName()
    {
        return Shizzel.MOD_ID + ".item.aepart";
    }

    @Override
    public IPart createPartFromItemStack(ItemStack itemStack)
    {
        IPart newPart = null;

        // Get the part
        AEParts part = AEParts.getPartFromDamageValue( itemStack );

        // Attempt to create a new instance of the part
        try
        {
            newPart = part.createPartInstance( itemStack );
        }
        catch( Throwable e )
        {
            Shizzel.logger.error( "Unable to create cable-part from item: %s", itemStack.getDisplayName() );

            e.printStackTrace();
        }

        // Return the part
        return newPart;
    }
}
