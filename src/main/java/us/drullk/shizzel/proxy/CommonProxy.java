package us.drullk.shizzel.proxy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.utils.EnumBlockTextures;

public class CommonProxy
{
    public CommonProxy()
    {
    }

    public void preInit()
    {

    }

    public void init()
    {

    }

    public void postInit()
    {

    }

    @SubscribeEvent
    public void registerTextures( final TextureStitchEvent.Pre event )
    {
        for( EnumBlockTextures texture : EnumBlockTextures.VALUES )
        {
            Shizzel.logger.info("Shizzel textures loaded.");
            texture.registerTexture( event.map );
        }
    }
}
