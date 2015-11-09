package us.drullk.shizzel.networking.appEng;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractPacketClient extends AbstractPacket
{
    @SideOnly(Side.CLIENT)
    protected abstract void wrappedExecute();

    public void sendPacketToPlayer()
    {
        ChannelHandler.sendPacketToPlayer( this, this.entityPlayer );
    }
}
