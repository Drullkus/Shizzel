package us.drullk.shizzel.appEng;

import appeng.api.parts.PartItemStack;
import appeng.util.Platform;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import us.drullk.shizzel.appEng.enumList.AEParts;
import us.drullk.shizzel.utils.Helper;

import java.io.IOException;

public abstract class AEPartAbstractRotateable extends AEPartAbstract
{
    private byte renderRotation = 0;

    public AEPartAbstractRotateable(AEParts associatedPart)
    {
        super(associatedPart);
    }

    protected void rotateRenderer( final RenderBlocks renderer, final boolean reset )
    {
        int rot = ( reset ? 0 : this.renderRotation );
        renderer.uvRotateBottom = renderer.uvRotateEast = renderer.uvRotateNorth = renderer.uvRotateSouth = renderer.uvRotateTop = renderer.uvRotateWest = rot;
    }

    @Override
    public boolean onActivate( final EntityPlayer player, final Vec3 position )
    {
        TileEntity hte = this.TE;

        if( !player.isSneaking() && Platform.isWrench(player, player.inventory.getCurrentItem(), hte.xCoord, hte.yCoord, hte.zCoord) )
        {
            if( Helper.isServerSide() )
            {
                if( ( this.renderRotation > 3 ) || ( this.renderRotation < 0 ) )
                {
                    this.renderRotation = 0;
                }

                switch ( this.renderRotation )
                {
                case 0:
                    this.renderRotation = 1;
                    break;
                case 1:
                    this.renderRotation = 3;
                    break;
                case 2:
                    this.renderRotation = 0;
                    break;
                case 3:
                    this.renderRotation = 2;
                    break;
                }

                this.markForUpdate();
                this.markForSave();
            }
            return true;
        }

        return super.onActivate( player, position );
    }

    @Override
    public void readFromNBT( final NBTTagCompound data )
    {
        super.readFromNBT( data );

        if( data.hasKey( "partRotation" ) )
        {
            this.renderRotation = data.getByte( "partRotation" );
        }
    }

    @Override
    public boolean readFromStream( final ByteBuf stream ) throws IOException
    {
        boolean redraw = false;

        // Call super
        redraw |= super.readFromStream( stream );

        // Read the rotation
        byte streamRot = stream.readByte();

        // Did the rotaion change?
        if( this.renderRotation != streamRot )
        {
            this.renderRotation = streamRot;
            redraw |= true;
        }

        return redraw;
    }

    @Override
    public void writeToNBT( final NBTTagCompound data)
    {
        super.writeToNBT( data );

        if( this.renderRotation != 0 )
        {
            data.setByte( "partRotation", this.renderRotation );
        }
    }

    @Override
    public void writeToStream( final ByteBuf stream ) throws IOException
    {
        super.writeToStream( stream );

        stream.writeByte(this.renderRotation);
    }
}
