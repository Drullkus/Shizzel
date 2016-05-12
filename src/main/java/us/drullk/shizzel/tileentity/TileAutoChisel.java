package us.drullk.shizzel.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import us.drullk.shizzel.Utils;
import us.drullk.shizzel.container.ContainerAutoChisel;

public class TileAutoChisel extends TileEntityLockable implements ITickable, IInventory, ISidedInventory
{
    private static final int[] slotsTop = new int[]{0, 1};
    private static final int[] slotsBottom = new int[]{2};
    private static final int[] slotsSides = new int[]{0, 1};
    private ItemStack[] inventory;
    private String name;

    /* 5 slots:
    0 - Input
    1 - Chisel
    2 - Output
    3 - Upgrade 1 - Lesser Chisel Dmg
    4 - Upgrade 2 - RF power - More consumes less power
    */

    public TileAutoChisel()
    {
        this.inventory = new ItemStack[this.getSizeInventory()];
    }

    @Override
    public void update() {

    }

    public boolean isIndexWithinInv(int index)
    {
        return index <= inventory.length;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        NBTTagList tagList = tagCompound.getTagList("Items", 10);

        this.inventory = new ItemStack[this.getSizeInventory()];

        if (tagCompound.hasKey("CustomName", 8))
        {
            this.name = tagCompound.getString("CustomName");
        }

        for (int i = 0; i < tagList.tagCount(); ++i)
        {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);
            int j = compound.getByte("Slot") & 255;

            if (j >= 0 && j < this.inventory.length)
            {
                this.inventory[j] = ItemStack.loadItemStackFromNBT(tagCompound);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        NBTTagList tagList = new NBTTagList();

        for (int i = 0; i < this.inventory.length; ++i)
        {
            if (this.inventory[i] != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setByte("Slot", (byte)i);

                this.inventory[i].writeToNBT(nbttagcompound);

                tagList.appendTag(nbttagcompound);
            }
        }

        tagCompound.setTag("Items", tagList);

        if (this.hasCustomName())
        {
            tagCompound.setString("CustomName", this.name);
        }
    }

    @Override
    public int getSizeInventory() {
        return 5;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return isIndexWithinInv(i) ? inventory[i] : null;
    }

    @Override
    public ItemStack decrStackSize(int i, int i1) {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int i) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {

    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return false;
    }

    @Override
    public void openInventory(EntityPlayer entityPlayer) {

    }

    @Override
    public void closeInventory(EntityPlayer entityPlayer) {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        return false;
    }

    @Override
    public int getField(int i) {
        return 0;
    }

    @Override
    public void setField(int i, int i1) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        for(int i = 0; i < inventory.length; i++)
        {
            inventory[i] = null;
        }
    }

    @Override
    public String getName() {
        return this.hasCustomName() ? this.name : "container.autoshizzel";
    }

    @Override
    public boolean hasCustomName() {
        return this.name != null && !this.name.equals("");
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryPlayer, EntityPlayer entityPlayer) {
        return new ContainerAutoChisel();
    }

    @Override
    public String getGuiID() {
        return null;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing enumFacing) {
        return enumFacing == EnumFacing.DOWN ? slotsBottom : (enumFacing == EnumFacing.UP ? slotsTop : slotsSides);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing enumFacing) {
        return index == 0 ? Utils.isChiselableItem(itemStack) :
                index == 1 && (inventory[1] == null && (itemStack.getItem().equals(GameRegistry.findItem("Chisel", "chisel_iron")) || itemStack.getItem().equals(GameRegistry.findItem("Chisel", "chisel_diamond"))));
    } //TODO: Check if IChiselItem

    @Override
    public boolean canExtractItem(int index, ItemStack itemStack, EnumFacing enumFacing) {
        return index == 2;
    }
}
