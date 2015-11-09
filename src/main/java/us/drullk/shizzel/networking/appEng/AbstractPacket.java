package us.drullk.shizzel.networking.appEng;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class AbstractPacket implements IMessage
{

    public EntityPlayer entityPlayer;
    public byte mode;
    protected boolean useCompression;

    public AbstractPacket()
    {
        this.entityPlayer = null;
        this.mode = -1;
        this.useCompression = false;
    }

    @SideOnly(Side.CLIENT)
    private static World getClientWorld()
    {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }
}
