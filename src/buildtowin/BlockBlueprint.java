package buildtowin;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlueprint extends BlockContainer {
    protected BlockBlueprint(int id) {
        super(id, Material.glass);
        
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
    }
    
    @Override
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
        TileEntityBuildingController buildingController = BuildToWin.getBuildingControllerList(par1World).getBuildingController(par5EntityPlayer);
        
        if (buildingController != null) {
            BlockData blockData = buildingController.getBlockData(par2, par3, par4);
            
            if (blockData != null) {
                if (buildingController.getDeadline() == 0) {
                    BuildToWin.printChatMessage(par1World, "The game has not started yet.");
                } else if (par5EntityPlayer.inventory.getCurrentItem() != null) {
                    if (par5EntityPlayer.inventory.getCurrentItem().itemID == blockData.id) {
                        par1World.setBlock(par2, par3, par4, blockData.id, blockData.metadata, 3);
                        par5EntityPlayer.inventory.consumeInventoryItem(blockData.id);
                    }
                }
            } else {
                BuildToWin.printChatMessage(par1World, "This blueprint does not belong to your Building Controller.");
            }
        } else {
            BuildToWin.printChatMessage(par1World, "Please connect to the Building Controller.");
        }
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return BuildToWin.blueprintRenderingId;
    }
    
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    
    @Override
    public TileEntityBlueprint createNewTileEntity(World world) {
        return new TileEntityBlueprint();
    }
    
    public TileEntityBlueprint getTileEntity(IBlockAccess world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) == BuildToWin.getBlueprint().blockID) {
            return (TileEntityBlueprint) world.getBlockTileEntity(x, y, z);
        }
        
        return null;
    }
    
    public BlockData getBlockData(IBlockAccess world, int x, int y, int z) {
        TileEntityBlueprint blueprint = this.getTileEntity(world, x, y, z);
        
        if (blueprint != null) {
            return new BlockData(x, y, z, blueprint.getBlockId(), blueprint.getMetadata());
        }
        
        return null;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
    }
}
