package us.drullk.shizzel.networking.appEng;

import appeng.api.AEApi;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import us.drullk.shizzel.appEng.enumList.EnumCache;
import us.drullk.shizzel.gui.appEng.GUIChiselingTerminal;

import java.util.Iterator;

public class PacketChiselingTerminalClient extends AbstractPacketClient
{
    private static final byte MODE_RECEIVE_CHANGE = 0, MODE_RECEIVE_FULL_LIST = 1, MODE_RECEIVE_PLAYER_HOLDING = 2, MODE_RECEIVE_SORTS = 3;

    private IAEItemStack changedStack;
    private IItemList<IAEItemStack> itemList;

    private SortOrder sortOrder;
    private SortDir sortDirection;
    private ViewItems viewMode;
    private boolean isHeldEmpty;

    public PacketChiselingTerminalClient createChangeUpdate(EntityPlayer entPlayer, IAEItemStack change)
    {
        this.entityPlayer = entPlayer;

        this.mode = PacketChiselingTerminalClient.MODE_RECEIVE_CHANGE;

        this.changedStack = change;

        return this;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected void wrappedExecute()
    {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof GUIChiselingTerminal)
        {
            switch (this.mode)
            {
            case PacketChiselingTerminalClient.MODE_RECEIVE_FULL_LIST:
                ((GUIChiselingTerminal) gui).onReceiveFullList(this.itemList);
                break;

            case PacketChiselingTerminalClient.MODE_RECEIVE_CHANGE:
                ((GUIChiselingTerminal) gui).onReceiveChange(this.changedStack);
                break;

            case PacketChiselingTerminalClient.MODE_RECEIVE_PLAYER_HOLDING:
                ((GUIChiselingTerminal) gui).onReceivePlayerHeld(this.changedStack);
                break;

            case PacketChiselingTerminalClient.MODE_RECEIVE_SORTS:
                ((GUIChiselingTerminal) gui).onReceiveSorting(this.sortOrder, this.sortDirection, this.viewMode);
                break;
            }
        }
    }

    public PacketChiselingTerminalClient createPlayerHoldingUpdate( final EntityPlayer player, final IAEItemStack heldItem )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalClient.MODE_RECEIVE_PLAYER_HOLDING;

        // Set the held item
        this.changedStack = heldItem;

        // Is the player holding anything?
        this.isHeldEmpty = ( heldItem == null );

        return this;
    }


    public PacketChiselingTerminalClient createSortingUpdate( final EntityPlayer player, final SortOrder order, final SortDir direction,
            final ViewItems viewMode )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalClient.MODE_RECEIVE_SORTS;

        // Set the sorts
        this.sortDirection = direction;
        this.sortOrder = order;
        this.viewMode = viewMode;

        return this;
    }


    public PacketChiselingTerminalClient createFullListUpdate( final EntityPlayer player, final IItemList<IAEItemStack> fullList )
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalClient.MODE_RECEIVE_FULL_LIST;

        // Enable compression
        this.useCompression = true;

        // Set the full list
        this.itemList = fullList;

        return this;
    }

    @Override
    public void execute()
    {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        // Is that screen the gui for the ACT?
        if( gui instanceof GUIChiselingTerminal )
        {
            switch ( this.mode )
            {
            case PacketChiselingTerminalClient.MODE_RECEIVE_FULL_LIST:
                // Set the item list
                ( (GUIChiselingTerminal)gui ).onReceiveFullList(this.itemList);
                break;

            case PacketChiselingTerminalClient.MODE_RECEIVE_CHANGE:
                // Update the item list
                ( (GUIChiselingTerminal)gui ).onReceiveChange(this.changedStack);
                break;

            case PacketChiselingTerminalClient.MODE_RECEIVE_PLAYER_HOLDING:
                // Set the held item
                ( (GUIChiselingTerminal)gui ).onReceivePlayerHeld(this.changedStack);
                break;

            case PacketChiselingTerminalClient.MODE_RECEIVE_SORTS:
                ( (GUIChiselingTerminal)gui ).onReceiveSorting(this.sortOrder, this.sortDirection, this.viewMode);
                break;
            }
        }
    }

    @Override
    public void readData( final ByteBuf stream )
    {

        switch ( this.mode )
        {
        case PacketChiselingTerminalClient.MODE_RECEIVE_FULL_LIST:
            // Create a new list
            this.itemList = AEApi.instance().storage().createItemList();

            // Read how many items there are
            int count = stream.readInt();

            for( int i = 0; i < count; i++ )
            {
                // Also ensure there are bytes to read
                if( stream.readableBytes() <= 0 )
                {
                    break;
                }

                // Read the itemstack
                IAEItemStack itemStack = AbstractPacket.readAEItemStack( stream );

                // Ensure it is not null
                if( itemStack != null )
                {
                    // Add to the list
                    this.itemList.add( itemStack );
                }
            }
            break;

        case PacketChiselingTerminalClient.MODE_RECEIVE_CHANGE:
            // Read the change amount
            int changeAmount = stream.readInt();

            // Read the item
            this.changedStack = AbstractPacket.readAEItemStack( stream );

            // Adjust it's size
            this.changedStack.setStackSize( changeAmount );

            break;

        case PacketChiselingTerminalClient.MODE_RECEIVE_PLAYER_HOLDING:
            // Read if the itemstack is empty
            this.isHeldEmpty = stream.readBoolean();

            // Is it not empty?
            if( !this.isHeldEmpty )
            {
                this.changedStack = AbstractPacket.readAEItemStack( stream );
            }
            else
            {
                this.changedStack = null;
            }
            break;

        case PacketChiselingTerminalClient.MODE_RECEIVE_SORTS:
            // Read sorts
            this.sortDirection = EnumCache.AE_SORT_DIRECTIONS[stream.readInt()];
            this.sortOrder = EnumCache.AE_SORT_ORDERS[stream.readInt()];
            this.viewMode = EnumCache.AE_VIEW_ITEMS[stream.readInt()];
            break;
        }
    }

    @Override
    protected void writeData(ByteBuf stream)
    {
        switch ( this.mode )
        {
        case PacketChiselingTerminalClient.MODE_RECEIVE_FULL_LIST:
            // Is the list null?
            if( this.itemList == null )
            {
                // No items
                stream.writeInt( 0 );
                return;
            }

            // Write how many items there are
            stream.writeInt( this.itemList.size() );

            // Get the iterator
            Iterator<IAEItemStack> listIterator = this.itemList.iterator();

            // Write each item
            while( listIterator.hasNext() )
            {
                AbstractPacket.writeAEItemStack( listIterator.next(), stream );
            }
            break;

        case PacketChiselingTerminalClient.MODE_RECEIVE_CHANGE:
            // Write the change amount
            stream.writeInt( (int)this.changedStack.getStackSize() );

            // Write the change
            AbstractPacket.writeAEItemStack( this.changedStack, stream );
            break;

        case PacketChiselingTerminalClient.MODE_RECEIVE_PLAYER_HOLDING:
            // Write if the held item is empty
            stream.writeBoolean( this.isHeldEmpty );

            // Is it not empty?
            if( !this.isHeldEmpty )
            {
                // Write the stack
                AbstractPacket.writeAEItemStack( this.changedStack, stream );
            }

            break;

        case PacketChiselingTerminalClient.MODE_RECEIVE_SORTS:
            // Write the sorts
            stream.writeInt( this.sortDirection.ordinal() );
            stream.writeInt( this.sortOrder.ordinal() );
            stream.writeInt( this.viewMode.ordinal() );
            break;
        }
    }
}
