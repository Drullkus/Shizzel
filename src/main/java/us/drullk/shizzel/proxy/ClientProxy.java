package us.drullk.shizzel.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.ShizzelProps;
import us.drullk.shizzel.item.Items;
import us.drullk.shizzel.rendering.ModelRegistry;

import java.util.Map;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit()
    {
        MinecraftForge.EVENT_BUS.register(ModelRegistry.instance);
    }

    @Override
    public void init()
    {
        registerModel(Items.beatingStick, 0, new ModelResourceLocation("stick", "inventory"));
        for(Map.Entry<Item, String> entry : itemModelsToRegister.entrySet())
        {
            registerModel(entry.getKey(), 0, new ModelResourceLocation(ShizzelProps.MOD_ID + ":" + entry.getValue(), "inventory"));
        }
    }

    private void registerModel(Block block, int meta, ModelResourceLocation rescLocation)
    {
        registerModel(Item.getItemFromBlock(block), meta, rescLocation);
    }

    private void registerModel(Item item, int meta, ModelResourceLocation rescLocation)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, rescLocation);
    }

    private void registerCustomItemModel(Item item, int meta, IBakedModel model)
    {
        ModelResourceLocation rescLocation = new ModelResourceLocation(ShizzelProps.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory");
        registerModel(item, meta, rescLocation);
        ModelRegistry.instance.register(rescLocation, model);
    }

}
