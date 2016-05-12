package us.drullk.shizzel.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.HashMap;

public class CommonProxy {

    protected HashMap<Item, String> itemModelsToRegister = new HashMap<Item, String>();

    public void preInit()
    {

    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    public void makeModel(Block block, String resourceLocation)
    {
        makeModel(Item.getItemFromBlock(block), resourceLocation);
    }

    public void makeModel(Block block)
    {
        makeModel(Item.getItemFromBlock(block), block.getUnlocalizedName());
    }

    public void makeModel(Item item)
    {
        makeModel(item, item.getUnlocalizedName());
    }

    public void makeModel(Item item, String resourceLocation)
    {
        itemModelsToRegister.put(item, resourceLocation);
    }
}
