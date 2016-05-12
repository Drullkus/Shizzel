package us.drullk.shizzel.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import us.drullk.shizzel.tileentity.TileAutoChisel;

public class BlockMachine extends BlockContainer
{
    public BlockMachine() {
        super(Material.iron);
        this.setUnlocalizedName("shizzelMachine");
        this.setHarvestLevel("pickaxe",1);
        this.setHardness(3.0f);
        this.setResistance(9.0f);
        this.setSoundType(SoundType.METAL);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileAutoChisel();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
