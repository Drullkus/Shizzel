package us.drullk.shizzel.networking.appEng;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import us.drullk.shizzel.Shizzel;

public class ChannelHandler
{
    public static SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Shizzel.MOD_ID);

    public static void registerMessages()
    {
        byte discriminator = 0;
    }

    public static void sendPacketToAllAround(AbstractPacket packet, int dimension, double x, double y, double z, int range)
    {
        NetworkRegistry.TargetPoint p = new NetworkRegistry.TargetPoint(dimension, x, y, z, range);
        wrapper.sendToAllAround(packet, p);
    }

    public static void sendPacketToPlayer( final AbstractPacket packet, final EntityPlayer player )
    {
        wrapper.sendTo( packet, (EntityPlayerMP)player );
    }

    public static void sendPacketToServer( final AbstractPacket packet )
    {
        wrapper.sendToServer( packet );
    }
}
