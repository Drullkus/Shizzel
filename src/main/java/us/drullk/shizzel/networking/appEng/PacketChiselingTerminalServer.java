package us.drullk.shizzel.networking.appEng;

import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;

public class PacketChiselingTerminalServer extends AbstractPacketServer
{
    private static final byte MODE_REQUEST_EXTRACTION = 2;

    private static final byte MODE_REQUEST_DEPOSIT = 3;

    private static final byte MODE_REQUEST_DEPOSIT_REGION = 5;

    public PacketChiselingTerminalServer createRequestDeposit(EntityPlayer player, int mouseButton)
    {
        this.entityPlayer = player;

        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT;

        return this;
    }

    public PacketChiselingTerminalServer createRequestExtract(EntityPlayer player, IAEItemStack itemStack, int mouseButton, boolean isShiftHeld)
    {
        // Set player
        this.entityPlayer = player;

        // Set mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_EXTRACTION;

        return this;
    }

    public PacketChiselingTerminalServer createRequestDepositRegion(EntityPlayer player, int slotNumber)
    {
        // Set the player
        this.entityPlayer = player;

        // Set the mode
        this.mode = PacketChiselingTerminalServer.MODE_REQUEST_DEPOSIT_REGION;

        return this;
    }
}
