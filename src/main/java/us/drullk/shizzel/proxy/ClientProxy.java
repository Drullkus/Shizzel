package us.drullk.shizzel.proxy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import us.drullk.shizzel.utils.EnumBlockTextures;

public class ClientProxy extends CommonProxy
{
    public ClientProxy()
    {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void preInit()
    {

    }

    @Override
    public void init()
    {

    }

    @Override
    public void postInit()
    {

    }

    @Override
    @SubscribeEvent
    public void registerTextures(final TextureStitchEvent.Pre event)
    {
        for (EnumBlockTextures texture : EnumBlockTextures.VALUES)
        {
            texture.registerTexture(event.map);
        }
    }
}
