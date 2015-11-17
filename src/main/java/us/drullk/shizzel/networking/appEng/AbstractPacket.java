package us.drullk.shizzel.networking.appEng;

import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.IOException;

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
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }

    public abstract void execute();
    protected abstract void readData( ByteBuf stream );
    protected abstract void writeData( ByteBuf stream );

    protected static IAEItemStack readAEItemStack( final ByteBuf stream )
    {
        IAEItemStack itemStack;
        try
        {
            itemStack = AEItemStack.loadItemStackFromPacket(stream);

            return itemStack;
        }
        catch( IOException e )
        {
        }
        return null;
    }

    protected static void writeAEItemStack( final IAEItemStack itemStack, final ByteBuf stream )
    {
        // Do we have a valid stack?
        if( itemStack != null )
        {
            // Write into the stream
            try
            {
                itemStack.writeToPacket( stream );
            }
            catch( IOException e )
            {
            }
        }
    }
}
