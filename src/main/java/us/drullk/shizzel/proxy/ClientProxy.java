package us.drullk.shizzel.proxy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import us.drullk.shizzel.utils.EnumBlockTextures;

public class ClientProxy extends CommonProxy
{
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

    @SubscribeEvent
    public void registerTextures( final TextureStitchEvent.Pre event )
    {
        for(EnumBlockTextures texture : EnumBlockTextures.values())
        {
            texture.registerTexture(event.map);
        }
    }
}
