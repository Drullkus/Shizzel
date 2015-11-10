package us.drullk.shizzel.networking.appEng;

public abstract class AbstractPacketServer extends AbstractPacket
{
    public void sendPacketToServer()
    {
        ChannelHandler.sendPacketToServer(this);
    }
}
