package buildtowin;

import java.util.Random;

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
                    if (par5EntityPlayer.inventory.getCurrentItem().itemID == buildingController.getItemId(blockData.id)) {
                        buildingController.removeBlueprint(blockData, false, false);
                        par5EntityPlayer.inventory.consumeInventoryItem(buildingController.getItemId(blockData.id));
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
        return BuildToWin.blueprintRenderId;
    }
    
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    
    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return 0;
    }
    
    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return 0;
    }
    
    @Override
    public TileEntityBlueprint createNewTileEntity(World world) {
        return new TileEntityBlueprint();
    }
    
    public BlockData getBlockData(IBlockAccess world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) == this.blockID) {
            TileEntityBlueprint blueprint = (TileEntityBlueprint) world.getBlockTileEntity(x, y, z);
            
            if (blueprint != null) {
                return new BlockData(x, y, z, blueprint.getBlockId(), blueprint.getMetadata());
            }
        }
        
        return null;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
    }
}
