package us.drullk.shizzel.utils;

import appeng.api.parts.IPartHost;
import appeng.helpers.IPriorityHost;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.AEPartAbstract;

public class ShizzelGUIHandler implements IGuiHandler
{
    private static final int DIRECTION_OFFSET = ForgeDirection.values().length;

    private static AEPartAbstract getPart( final ForgeDirection tileSide, final World world, final int x, final int y, final int z )
    {
        IPartHost partHost = (IPartHost)( world.getTileEntity( x, y, z ) );

        if( partHost == null )
        {
            return null;
        }

        return (AEPartAbstract)( partHost.getPart( tileSide ) );
    }

    public static void launchGui( final int ID, final EntityPlayer player, final World world, final int x, final int y, final int z )
    {
        player.openGui( Shizzel.instance, ID + ShizzelGUIHandler.DIRECTION_OFFSET, world, x, y, z );
    }

    public static void launchGui( final AEPartAbstract part, final EntityPlayer player, final World world, final int x, final int y, final int z )
    {
        if( part.doesPlayerHavePermissionToOpenGui( player ) )
        {
            player.openGui( Shizzel.instance, part.getSide().ordinal(), world, x, y, z );
        }
    }

    private static Object getPartGuiElement( final ForgeDirection tileSide, final EntityPlayer player, final World world, final int x, final int y,
            final int z, final boolean isServerSide )
    {
        // Get the part
        AEPartAbstract part = ShizzelGUIHandler.getPart( tileSide, world, x, y, z );

        // Ensure we got the part
        if( part == null )
        {
            return null;
        }

        // Is this server side?
        if( isServerSide )
        {
            // Ask the part for its server element
            return part.getServerGuiElement( player );
        }

        // Ask the part for its client element
        return part.getClientGuiElement( player );
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        ForgeDirection side = ForgeDirection.getOrientation( ID );

        if( ( world != null ) && ( side != ForgeDirection.UNKNOWN ) )
        {
            return ShizzelGUIHandler.getPartGuiElement( side, player, world, x, y, z, true );
        }

        ID -= ShizzelGUIHandler.DIRECTION_OFFSET;

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        ForgeDirection side = ForgeDirection.getOrientation( ID );

        if( ( world != null ) && ( side != ForgeDirection.UNKNOWN ) )
        {
            return ShizzelGUIHandler.getPartGuiElement( side, player, world, x, y, z, false );
        }

        ID -= ShizzelGUIHandler.DIRECTION_OFFSET;

        return null;
    }
}
