package us.drullk.shizzel.networking.appEng;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.common.base.Charsets;

import appeng.api.parts.IPartHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.util.item.AEItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import us.drullk.shizzel.appEng.AEPartAbstract;

public abstract class AbstractPacket implements IMessage
{
    private static final int COMPRESSED_BUFFER_SIZE = 1048576;

    public EntityPlayer player;

    protected byte mode;

    protected boolean useCompression;

    public AbstractPacket()
    {
        this.player = null;
        this.mode = -1;
        this.useCompression = false;
    }

    @SideOnly(Side.CLIENT)
    private static World getClientWorld()
    {
        return Minecraft.getMinecraft().theWorld;
    }

    protected static IAEItemStack readAEItemStack(final ByteBuf stream)
    {
        IAEItemStack itemStack;
        try
        {
            itemStack = AEItemStack.loadItemStackFromPacket(stream);

            return itemStack;
        }
        catch (IOException e)
        {
        }

        return null;

    }

    protected static ItemStack readItemstack(final ByteBuf stream)
    {
        return ByteBufUtils.readItemStack(stream);
    }

    protected static AEPartAbstract readPart(final ByteBuf stream)
    {
        ForgeDirection side = ForgeDirection.getOrientation(stream.readInt());

        IPartHost host = (IPartHost) AbstractPacket.readTileEntity(stream);

        return (AEPartAbstract) host.getPart(side);
    }

    protected static EntityPlayer readPlayer(final ByteBuf stream)
    {
        EntityPlayer player = null;

        if (stream.readBoolean())
        {
            World playerWorld = readWorld(stream);
            player = playerWorld.getPlayerEntityByName(readString(stream));
        }

        return player;
    }

    protected static String readString(final ByteBuf stream)
    {
        byte[] stringBytes = new byte[stream.readInt()];

        stream.readBytes(stringBytes);

        return new String(stringBytes, Charsets.UTF_8);
    }

    protected static TileEntity readTileEntity(final ByteBuf stream)
    {
        World world = AbstractPacket.readWorld(stream);

        return world.getTileEntity(stream.readInt(), stream.readInt(), stream.readInt());
    }

    protected static World readWorld(final ByteBuf stream)
    {
        World world = DimensionManager.getWorld(stream.readInt());

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            if (world == null)
            {
                world = getClientWorld();
            }
        }

        return world;
    }

    protected static void writeAEItemStack(final IAEItemStack itemStack, final ByteBuf stream)
    {
        // Do we have a valid stack?
        if (itemStack != null)
        {
            // Write into the stream
            try
            {
                itemStack.writeToPacket(stream);
            }
            catch (IOException e)
            {
            }
        }
    }

    protected static void writeItemstack(final ItemStack stack, final ByteBuf stream)
    {
        ByteBufUtils.writeItemStack(stream, stack);
    }

    protected static void writePart(final AEPartAbstract part, final ByteBuf stream)
    {
        stream.writeInt(part.getSide().ordinal());

        writeTileEntity(part.getHost().getTile(), stream);
    }

    @SuppressWarnings("null")
    protected static void writePlayer(final EntityPlayer player, final ByteBuf stream)
    {
        boolean validPlayer = (player != null);

        stream.writeBoolean(validPlayer);

        if (validPlayer)
        {
            writeWorld(player.worldObj, stream);
            writeString(player.getCommandSenderName(), stream);
        }
    }

    protected static void writeString(final String string, final ByteBuf stream)
    {
        byte[] stringBytes = string.getBytes(Charsets.UTF_8);

        stream.writeInt(stringBytes.length);

        stream.writeBytes(stringBytes);
    }

    /**
     * Writes a tile entity to the stream.
     *
     * @param entity
     * @param stream
     */
    protected static void writeTileEntity(final TileEntity entity, final ByteBuf stream)
    {
        writeWorld(entity.getWorldObj(), stream);
        stream.writeInt(entity.xCoord);
        stream.writeInt(entity.yCoord);
        stream.writeInt(entity.zCoord);
    }

    /**
     * Writes a world to the stream.
     *
     * @param world
     * @param stream
     */
    protected static void writeWorld(final World world, final ByteBuf stream)
    {
        stream.writeInt(world.provider.dimensionId);
    }

    private void fromCompressedBytes(final ByteBuf packetStream)
    {
        // Create a new data stream
        ByteBuf decompressedStream = Unpooled.buffer(AbstractPacket.COMPRESSED_BUFFER_SIZE);

        GZIPInputStream decompressor = null;
        try
        {
            // Create the decompressor
            decompressor = new GZIPInputStream(new InputStream()
            {

                @Override
                public int read() throws IOException
                {
                    // Is there anymore data to read from the packet stream?
                    if (packetStream.readableBytes() <= 0)
                    {
                        // Return end marker
                        return -1;
                    }

                    // Return the byte
                    return packetStream.readByte() & 0xFF;
                }
            });

            // Create a temporary holding array
            byte[] holding = new byte[512];

            // Decompress
            while (decompressor.available() != 0)
            {
                // Read into the holding array
                int bytesRead = decompressor.read(holding);

                // Did we read any data?
                if (bytesRead > 0)
                {
                    // Write the holding array into the decompressed stream
                    decompressedStream.writeBytes(holding, 0, bytesRead);
                }
            }

            // Close the decompressor
            decompressor.close();

            // Reset stream position
            decompressedStream.readerIndex(0);

            // Pass to subclass
            this.readData(decompressedStream);

        }
        catch (IOException e)
        {
            // Failed
            if (decompressor != null)
            {
                try
                {
                    decompressor.close();
                }
                catch (IOException e1)
                {
                }
            }
        }
    }

    /**
     * Creates a new stream, calls to the subclass to write
     * into it, then compresses it into the packet stream.
     *
     * @param packetStream
     */
    private void toCompressedBytes(final ByteBuf packetStream)
    {
        // Create a new data stream
        ByteBuf streamToCompress = Unpooled.buffer(AbstractPacket.COMPRESSED_BUFFER_SIZE);

        // Pass to subclass
        this.writeData(streamToCompress);

        GZIPOutputStream compressor = null;
        try
        {
            // Create the compressor
            compressor = new GZIPOutputStream(new OutputStream()
            {

                @Override
                public void write(final int byteToWrite) throws IOException
                {
                    // Write the byte to the packet stream
                    packetStream.writeByte(byteToWrite & 0xFF);
                }
            })
            {
                {
                    this.def.setLevel(Deflater.BEST_COMPRESSION);
                }
            };

            // Compress
            compressor.write(streamToCompress.array(), 0, streamToCompress.writerIndex());

            // Close the compressor
            compressor.close();
        }
        catch (IOException e)
        {
            // Failed

            if (compressor != null)
            {
                try
                {
                    compressor.close();
                }
                catch (IOException e1)
                {
                }
            }
        }
    }

    /**
     * Allows subclasses to read data from the specified stream.
     *
     * @param stream
     */
    protected abstract void readData(ByteBuf stream);

    /**
     * Allows subclasses to write data into the specified stream.
     *
     * @param stream
     */
    protected abstract void writeData(ByteBuf stream);

    /**
     * Packet has been read and action can now take place.
     */
    public abstract void execute();

    /**
     * Reads data from the packet stream.
     */
    @Override
    public void fromBytes(final ByteBuf stream)
    {
        this.mode = stream.readByte();
        this.player = AbstractPacket.readPlayer(stream);
        this.useCompression = stream.readBoolean();

        // Is there a compressed substream?
        if (this.useCompression)
        {
            this.fromCompressedBytes(stream);
        }
        else
        {
            // Pass stream directly to subclass
            this.readData(stream);
        }
    }

    @Override
    public void toBytes(final ByteBuf stream)
    {
        // Write the mode
        stream.writeByte(this.mode);

        // Write the player
        AbstractPacket.writePlayer(this.player, stream);

        // Write if there is a compressed sub-stream.
        stream.writeBoolean(this.useCompression);

        // Is compression enabled?
        if (this.useCompression)
        {
            this.toCompressedBytes(stream);
        }
        else
        {
            // No compression, subclass writes directly into stream.
            this.writeData(stream);
        }
    }

}
