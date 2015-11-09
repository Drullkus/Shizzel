package us.drullk.shizzel.container.appEng;

import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.PlayerSource;
import appeng.api.networking.storage.IBaseMonitor;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.data.IAEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import us.drullk.shizzel.appEng.PartChiselingTerminal;
import us.drullk.shizzel.networking.appEng.PacketChiselingTerminalClient;
import us.drullk.shizzel.utils.Helper;

public class ContainerChiselingTerminal extends ContainerWithPlayerInventory implements IMEMonitorHandlerReceiver<IAEItemStack>
{
    public static int VIEW_SLOT_XPOS = 206, VIEW_SLOT_YPOS = 8;

    private static int playerInvPosY = 85;
    private static int hotbarInvPosY = playerInvPosY + (renderSlotSize * 3) + 4;
    private PartChiselingTerminal chiselTerm;
    private EntityPlayer entityPlayer;
    private IMEMonitor<IAEItemStack> MEMonitor;
    private PlayerSource playerSource;

    public ContainerChiselingTerminal(PartChiselingTerminal chiselingTerminal, EntityPlayer player)
    {
        this.chiselTerm = chiselingTerminal;
        this.entityPlayer = player;
        this.playerSource = new PlayerSource(player, chiselingTerminal);

        this.bindPlayerInventory(player.inventory, ContainerChiselingTerminal.playerInvPosY, ContainerChiselingTerminal.hotbarInvPosY);

        //TODO:Setup more chisel stuffs

        if(Helper.isServerSide())
        {
            this.registerForUpdates();

            this.MEMonitor = chiselingTerminal.getItemInventory();

            if(this.MEMonitor != null)
            {
                this.MEMonitor.addListener(this, null);
            }
        }
    }

    public void registerForUpdates()
    {
        this.chiselTerm.registerListener(this);
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player) {
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotNumber )
    {
        if(Helper.isClientSide())
        {
            return null;
        }

        Slot slot = (Slot)this.inventorySlots.get( slotNumber );

        if( ( slot != null ) && ( slot.getHasStack() ) )
        {
            boolean didMerge = false;

            ItemStack slotStack = slot.getStack();

            //TODO: Stack Marge stuff
        }

        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public boolean isValid(Object o) {
        return true;
    }

    @Override
    public void postChange(IBaseMonitor<IAEItemStack> iBaseMonitor, Iterable<IAEItemStack> iterable, BaseActionSource baseActionSource) {
        for( IAEItemStack change : iterable )
        {
            IAEItemStack newAmount = this.MEMonitor.getStorageList().findPrecise(change);

            if( newAmount == null )
            {
                newAmount = change.copy();

                newAmount.setStackSize( 0 );
            }

            new PacketChiselingTerminalClient().createChangeUpdate( this.entityPlayer, newAmount ).sendPacketToPlayer();
        }
    }

    @Override
    public void onListUpdate() {
        //Useless
    }


}
