package buildtowin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSand;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBlueprint extends BlockContainer {
    protected BlockBlueprint(int id) {
        super(id, Material.glass);
        this.setBlockUnbreakable();
    }
    
    @Override
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
        TileEntityBlockData te = (TileEntityBlockData) par1World.getBlockTileEntity(par2, par3, par4);
        
        if (te.getBlockId() != 0) {
            if (par5EntityPlayer.inventory.hasItem(te.getBlockId())) {
                par1World.setBlockAndMetadataWithNotify(par2, par3, par4, te.getBlockId(), 0, 3);
                par5EntityPlayer.inventory.consumeInventoryItem(te.getBlockId());
            }
        }
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        return 15050;
    }
    
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        TileEntityBlockData te = (TileEntityBlockData) par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
        
        if (te.getBlockId() != 0) {
            return Block.blocksList[te.getBlockId()].getBlockTextureFromSide(0);
        } else {
            return Block.blocksList[1].getBlockTextureFromSide(0);
        }
    }
    
    public Icon getBlockTextureFromSideAndMetadata(int par1, int par2) {
        return null;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void func_94332_a(IconRegister par1IconRegister) {
    }
    
    @Override
    public TileEntityBlockData createNewTileEntity(World world) {
        return new TileEntityBlockData();
    }
}
