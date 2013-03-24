package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
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
        TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(par5EntityPlayer);
        
        BlockData blockData = buildingController.getBlockData(par2, par3, par4);
        
        if (blockData != null) {
            if (buildingController.getDeadline() == 0) {
                if (par1World.isRemote) {
                    Minecraft mc = FMLClientHandler.instance().getClient();
                    
                    mc.ingameGUI.getChatGUI().printChatMessage(
                            "<BuildToWin> The game has not started yet, " + par5EntityPlayer.getEntityName() + ".");
                }
            } else if (par5EntityPlayer.inventory.getCurrentItem() != null) {
                if (par5EntityPlayer.inventory.getCurrentItem().itemID == blockData.id) {
                    par1World.setBlock(par2, par3, par4, blockData.id);
                    par5EntityPlayer.inventory.consumeInventoryItem(blockData.id);
                }
            }
        } else if (par1World.isRemote) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            
            mc.ingameGUI.getChatGUI().printChatMessage(
                    "<BuildToWin> This blueprint does not belong to your Building Controller, " + par5EntityPlayer.getEntityName() + ".");
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
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, 1 - par5);
    }
    
    @Override
    public TileEntityBlueprint createNewTileEntity(World world) {
        return new TileEntityBlueprint();
    }
    
    @Override
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        TileEntityBlueprint te = (TileEntityBlueprint) par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
        
        if (te.getBlockId() != 0) {
            return Block.blocksList[te.getBlockId()].getBlockTextureFromSide(0);
        } else {
            return Block.blocksList[1].getBlockTextureFromSide(0);
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
    }
}
