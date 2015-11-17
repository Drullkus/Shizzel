package us.drullk.shizzel.networking.appEng;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import us.drullk.shizzel.Shizzel;

public class ChannelHandler
{
    public static SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Shizzel.MOD_ID);

    public static void registerMessages()
    {
        byte discriminator = 0;

        wrapper.registerMessage(HandlerChiselingTerminalClient.class, PacketChiselingTerminalClient.class, discriminator++, Side.CLIENT);
        wrapper.registerMessage(HandlerChiselingTerminalServer.class, PacketChiselingTerminalServer.class, discriminator++, Side.SERVER);
    }

    public static void sendPacketToPlayer(final AbstractPacket packet, final EntityPlayer player)
    {
        wrapper.sendTo(packet, (EntityPlayerMP) player);
    }

    public static void sendPacketToServer(final AbstractPacket packet)
    {
        wrapper.sendToServer(packet);
    }
}
