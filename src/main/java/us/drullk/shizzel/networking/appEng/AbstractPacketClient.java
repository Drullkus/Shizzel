package us.drullk.shizzel.networking.appEng;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class AbstractPacketClient extends AbstractPacket
{
    @SideOnly(Side.CLIENT)
    protected abstract void wrappedExecute();

    @Override
    public final void execute()
    {
        // Ensure we have a player
        if (this.player == null)
        {
            return;
        }

        // Ensure this is client side
        if (FMLCommonHandler.instance().getEffectiveSide().isClient())
        {
            this.wrappedExecute();
        }
    }

    public void sendPacketToPlayer()
    {
        ChannelHandler.sendPacketToPlayer(this, this.player);
    }
}
