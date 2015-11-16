package us.drullk.shizzel.appEng.enumList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.ItemAEPart;

public enum AEItems
{
    AEPartItem("part.base", new ItemAEPart().setUnlocalizedName(Shizzel.MOD_ID + ".item.aepart"));

    private final String name;

    private Item item;

    AEItems(String name, Item item)
    {
        this.name = name;

        this.item = item;

        this.item.setCreativeTab(Shizzel.ShizzelTab);
    }

    public Item getItem()
    {
        return this.item;
    }

    public String getInternalName()
    {
        return this.name;
    }

    public ItemStack getStackWithDamage(final int damageValue)
    {
        return new ItemStack(this.item, 1, damageValue);
    }
}
