package us.drullk.shizzel.appEng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.core.WorldSettings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import us.drullk.shizzel.appEng.enumList.AEParts;
import us.drullk.shizzel.utils.EnumBlockTextures;
import us.drullk.shizzel.utils.Helper;

public abstract class AEPartAbstract implements IPart, IGridHost, IActionHost
{

    protected IGridNode node;

    protected IPartHost host;

    protected ForgeDirection cableSide;

    protected TileEntity TE;

    protected boolean recevingRedstonePower;

    protected boolean isActive;

    protected AEGridBlock gridBlock;

    protected int ownerID = -1;

    protected double powerUsage;

    public final ItemStack associatedItem;

    public AEPartAbstract(final AEParts associatedPart)
    {
        this.associatedItem = associatedPart.getStack();
    }

    @MENetworkEventSubscribe
    public void setPower(MENetworkPowerStatusChange statusChange)
    {
        if (this.node != null)
        {
            this.isActive = this.node.isActive();
            this.host.markForUpdate();
        }
    }

    public double getPowerUsage()
    {
        return this.powerUsage;
    }

    public DimensionalCoord getLocation()
    {
        return new DimensionalCoord(this.TE.getWorldObj(), this.TE.xCoord, this.TE.yCoord, this.TE.zCoord);
    }

    public final void markForSave()
    {
        if (this.host != null)
        {
            this.host.markForSave();
        }
    }

    protected boolean doesPlayerHavePermission(EntityPlayer player, SecurityPermissions permission)
    {
        ISecurityGrid sGrid = this.gridBlock.getSecurityGrid();

        if (sGrid == null)
        {
            return false;
        }

        return sGrid.hasPermission(player, permission);
    }

    public boolean doesPlayerHavePermissionToOpenGui(EntityPlayer player)
    {
        return false;
    }

    public boolean isActive()
    {
        if (Helper.isServerSide())
        {
            if (this.node != null)
            {
                this.isActive = this.node.isActive();
            }
        }

        return this.isActive;
    }

    public Object getServerGuiElement(EntityPlayer player)
    {
        return null;
    }

    public ForgeDirection getSide()
    {
        return this.cableSide;
    }

    @Override
    public IGridNode getActionableNode()
    {
        return this.node;
    }

    public AEGridBlock getGridBlock()
    {
        return this.gridBlock;
    }

    @Override
    public IGridNode getGridNode()
    {
        return this.node;
    }

    @Override
    public final IGridNode getGridNode(ForgeDirection forgeDirection)
    {
        return this.node;
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection forgeDirection)
    {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak()
    {
        List<ItemStack> drops = new ArrayList<ItemStack>();

        drops.add(this.getItemStack(null));

        this.getDrops(drops, false);

        appeng.util.Platform.spawnDrops(this.TE.getWorldObj(), this.TE.xCoord, this.TE.yCoord, this.TE.zCoord, drops);

        this.host.removePart(this.cableSide, false);
    }

    @Override
    public ItemStack getItemStack(PartItemStack partItemStack)
    {
        // Get the itemstack
        ItemStack itemStack = this.associatedItem.copy();

        // Save NBT data if the part was wrenched or creatively picked
        if ((partItemStack == PartItemStack.Wrench) || (partItemStack == PartItemStack.Pick))
        {
            // Create the item tag
            NBTTagCompound itemNBT = new NBTTagCompound();

            // Write the data
            this.writeToNBT(itemNBT);

            // Set the tag
            if (!itemNBT.hasNoTags())
            {
                itemStack.setTagCompound(itemNBT);
            }
        }

        return itemStack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public abstract void renderInventory(IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks);

    @Override
    @SideOnly(Side.CLIENT)
    public abstract void renderStatic(int i, int i1, int i2, IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks);

    @Override
    @SideOnly(Side.CLIENT)
    public void renderDynamic(double v, double v1, double v2, IPartRenderHelper iPartRenderHelper, RenderBlocks renderBlocks)
    {
    }

    @Override
    public abstract IIcon getBreakingTexture();

    @Override
    public boolean requireDynamicRender()
    {
        return false;
    }

    @Override
    public boolean isSolid()
    {
        return false;
    }

    @Override
    public boolean canConnectRedstone()
    {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound)
    {
        if (this.node != null)
        {
            NBTTagCompound nodeTag = new NBTTagCompound();

            this.node.saveToNBT("node0", nodeTag);

            nbtTagCompound.setTag("node", nodeTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound)
    {
        if (nbtTagCompound.hasKey("node") && this.node != null)
        {
            this.node.loadFromNBT("node0", nbtTagCompound.getCompoundTag("node"));

            this.ownerID = nbtTagCompound.getInteger("Owner");

            this.node.updateState();
        }
    }

    @Override
    public int getLightLevel()
    {
        return this.isActive ? 15 : 0;
    }

    @Override
    public boolean isLadder(EntityLivingBase entityLivingBase)
    {
        return false;
    }

    @Override
    public void onNeighborChanged()
    {
        if (this.TE != null)
        {
            World world = this.TE.getWorldObj();

            int x = this.TE.xCoord;
            int y = this.TE.yCoord;
            int z = this.TE.zCoord;

            this.recevingRedstonePower = world.isBlockIndirectlyGettingPowered(x, y, z);
        }
    }

    @Override
    public int isProvidingStrongPower()
    {
        return 0;
    }

    @Override
    public int isProvidingWeakPower()
    {
        return 0;
    }

    @Override
    public void writeToStream(ByteBuf byteBuf) throws IOException
    {
        byteBuf.writeBoolean(this.node != null && this.node.isActive());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean readFromStream(ByteBuf byteBuf) throws IOException
    {
        boolean oldActive = this.isActive;

        this.isActive = byteBuf.readBoolean();

        return (oldActive != this.isActive);
    }

    @Override
    public void onEntityCollision(Entity entity)
    {

    }

    @Override
    public void removeFromWorld()
    {
        if (this.node != null)
        {
            this.node.destroy();
        }
    }

    @Override
    public void addToWorld()
    {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
        {
            this.gridBlock = new AEGridBlock(this);

            this.node = AEApi.instance().createGridNode(this.gridBlock);

            if (this.node != null)
            {
                this.node.setPlayerID(this.ownerID);
                this.node.updateState();
            }

            this.setPower(null);

            this.onNeighborChanged();
        }
    }

    @Override
    public IGridNode getExternalFacingNode()
    {
        return null;
    }

    @Override
    public void setPartHostInfo(ForgeDirection forgeDirection, IPartHost iPartHost, TileEntity tileEntity)
    {
        this.cableSide = forgeDirection;
        this.host = iPartHost;
        this.TE = tileEntity;
    }

    @Override
    public boolean onActivate(EntityPlayer entityPlayer, Vec3 vec3)
    {

        if (entityPlayer.isSneaking())
        {
            return false;
        }

        // Is this server side?
        if (Helper.isServerSide())
        {
            // Launch the gui
            Helper.launchGui(this, entityPlayer, this.TE.getWorldObj(), this.TE.xCoord, this.TE.yCoord, this.TE.zCoord);
        }
        return false;
    }

    @Override
    public boolean onShiftActivate(EntityPlayer entityPlayer, Vec3 vec3)
    {
        return false;
    }

    @Override
    public void getDrops(List<ItemStack> list, boolean b)
    {

    }

    @Override
    public abstract int cableConnectionRenderTo();

    @Override
    public void randomDisplayTick(World world, int i, int i1, int i2, Random random)
    {
        //TODO: I could do something visually here. Maybe.
    }

    @Override
    public void onPlacement(EntityPlayer entityPlayer, ItemStack itemStack, ForgeDirection forgeDirection)
    {
        this.ownerID = WorldSettings.getInstance().getPlayerID(entityPlayer.getGameProfile());
    }

    @Override
    public boolean canBePlacedOn(BusSupport busSupport)
    {
        return busSupport != BusSupport.DENSE_CABLE;
    }

    @Override
    public abstract void getBoxes(IPartCollisionHelper iPartCollisionHelper);

    public void setupPartFromItem(final ItemStack itemPart)
    {
        if (itemPart.hasTagCompound())
        {
            this.readFromNBT(itemPart.getTagCompound());
        }
    }

    public final void markForUpdate()
    {
        if( this.host != null )
        {
            this.host.markForUpdate();
        }
    }

    @SideOnly(Side.CLIENT)
    public void renderStaticBusLights( final int x, final int y, final int z, final IPartRenderHelper helper, final RenderBlocks renderer )
    {
        IIcon busColorTexture = EnumBlockTextures.BUS_COLOR.getTextures()[0];

        IIcon sideTexture = EnumBlockTextures.BUS_COLOR.getTextures()[2];

        helper.setTexture( busColorTexture, busColorTexture, sideTexture, sideTexture, busColorTexture, busColorTexture );

        // Render the box
        helper.renderBlock( x, y, z, renderer );

        // Are we active?
        if( this.isActive() )
        {
            // Set the brightness
            Tessellator.instance.setBrightness( 0xD000D0 );

            // Set the color to match the cable
            Tessellator.instance.setColorOpaque_I( this.host.getColor().blackVariant );
        }
        else
        {
            // Set the color to black
            Tessellator.instance.setColorOpaque_I( 0 );
        }

        IIcon lightTexture = EnumBlockTextures.BUS_COLOR.getTextures()[1];

        helper.renderFace( x, y, z, lightTexture, ForgeDirection.UP, renderer );
        helper.renderFace( x, y, z, lightTexture, ForgeDirection.DOWN, renderer );
        helper.renderFace( x, y, z, lightTexture, ForgeDirection.NORTH, renderer );
        helper.renderFace( x, y, z, lightTexture, ForgeDirection.EAST, renderer );
        helper.renderFace( x, y, z, lightTexture, ForgeDirection.SOUTH, renderer );
        helper.renderFace( x, y, z, lightTexture, ForgeDirection.WEST, renderer );
    }
}
