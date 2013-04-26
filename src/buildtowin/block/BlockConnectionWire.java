package buildtowin.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityConnectionWire;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockConnectionWire extends BlockContainer {
    
    private Icon iconDisconnected;
    
    public BlockConnectionWire(int blockId) {
        super(blockId, Material.circuits);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("connectionWire");
    }
    
    @Override
    public void onBlockAdded(World par1World, int x, int y, int z) {
        if (!par1World.isRemote) {
            TileEntityConnectionWire connectionWire = (TileEntityConnectionWire) par1World.getBlockTileEntity(x, y, z);
            connectionWire.refresh();
        }
    }
    
    @Override
    public void onNeighborBlockChange(World par1World, int x, int y, int z, int par5) {
        if (!par1World.isRemote) {
            TileEntityConnectionWire connectionWire = (TileEntityConnectionWire) par1World.getBlockTileEntity(x, y, z);
            connectionWire.refresh();
        }
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return BuildToWin.connectionWireRenderId;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityConnectionWire wire = (TileEntityConnectionWire) blockAccess.getBlockTileEntity(x, y, z);
        
        if (wire.isActivated()) {
            return this.blockIcon;
        } else {
            return this.iconDisconnected;
        }
    }
    
    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random) {
        TileEntityConnectionWire wire = (TileEntityConnectionWire) par1World.getBlockTileEntity(x, y, z);
        
        if (wire.isActivated()) {
            double d0 = x + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.2D;
            double d1 = y + 0.6625F;
            double d2 = z + 0.5D + (par5Random.nextFloat() - 0.5D) * 0.2D;
            float f = 1;
            float f1 = f * 0.6F + 0.4F;
            
            float f2 = f * f * 0.7F - 0.5F;
            float f3 = f * f * 0.6F - 0.7F;
            
            if (f2 < 0.0F) {
                f2 = 0.0F;
            }
            
            if (f3 < 0.0F) {
                f3 = 0.0F;
            }
            
            par1World.spawnParticle("reddust", d0, d1, d2, f1, f2, f3);
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityConnectionWire();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:connector_connected");
        this.iconDisconnected = par1IconRegister.registerIcon("buildtowin:connector_disconnected");
    }
}
