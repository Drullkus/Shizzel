package us.drullk.shizzel.networking.appEng;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;

public class PacketChiselingTerminalServer extends AbstractPacketServer
{
    private static final byte MODE_REQUEST_FULL_LIST = 1;
    private static final byte MODE_REQUEST_EXTRACTION = 2;
    private static final byte MODE_REQUEST_DEPOSIT = 3;
    private static final byte MODE_REQUEST_CLEAR_GRID = 4;
    private static final byte MODE_REQUEST_DEPOSIT_REGION = 5;
    private static final byte MODE_REQUEST_SET_SORT = 6;
    private static final byte MODE_REQUEST_SET_GRID = 7;
    private static final byte MODE_REQUEST_AUTO_CRAFT = 8;

    private int mouseButton;
    private int slotNumber;
    private boolean isShiftHeld;

    private IAEItemStack itemStack;

    public PacketChiselingTerminalServer createRequestDeposit(EntityPlayer player, int mouseButton)
    {
        this.entityPlayer = player;

        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT;

        this.mouseButton = mouseButton;

        return this;
    }

    public PacketChiselingTerminalServer createRequestExtract(EntityPlayer player, IAEItemStack itemStack, int mouseButton, boolean isShiftHeld)
    {
        // Set player
        this.entityPlayer = player;

        // Set mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_EXTRACTION;

        // Set stack
        this.itemStack = itemStack;

        // Set mouse button
        this.mouseButton = mouseButton;

        // Set shift
        this.isShiftHeld = isShiftHeld;

        return this;
    }

    public PacketChiselingTerminalServer createRequestDepositRegion(EntityPlayer player, int slotNumber)
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT_REGION;

        // Set the slot number
        this.slotNumber = slotNumber;

        return this;
    }
}
