package us.drullk.shizzel.networking.appEng;

import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import us.drullk.shizzel.gui.appEng.GUIChiselingTerminal;

public class PacketChiselingTerminalClient extends AbstractPacketClient
{
    private static final byte MODE_RECEIVE_CHANGE = 0, MODE_RECEIVE_FULL_LIST = 1, MODE_RECEIVE_PLAYER_HOLDING = 2, MODE_RECEIVE_SORTS = 3,
            MODE_UPDATE_COSTS = 4;

    private IAEItemStack changedStack;
    private IItemList<IAEItemStack> itemList;
    private boolean isHeldEmpty;
    private SortOrder sortOrder;
    private SortDir sortDirection;
    private ViewItems viewMode;

    public PacketChiselingTerminalClient createChangeUpdate(EntityPlayer entPlayer, IAEItemStack change )
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

        if(gui instanceof GUIChiselingTerminal)
        {
            switch ( this.mode )
            {
                case PacketChiselingTerminalClient.MODE_RECEIVE_FULL_LIST:
                    ((GUIChiselingTerminal)gui).onReceiveFullList( this.itemList );
                    break;

                case PacketChiselingTerminalClient.MODE_RECEIVE_CHANGE:
                    ((GUIChiselingTerminal)gui).onReceiveChange( this.changedStack );
                    break;

                case PacketChiselingTerminalClient.MODE_RECEIVE_PLAYER_HOLDING:
                    ((GUIChiselingTerminal)gui).onReceivePlayerHeld( this.changedStack );
                    break;

                case PacketChiselingTerminalClient.MODE_RECEIVE_SORTS:
                    ((GUIChiselingTerminal)gui).onReceiveSorting( this.sortOrder, this.sortDirection, this.viewMode );
                    break;
            }
        }
    }
}
