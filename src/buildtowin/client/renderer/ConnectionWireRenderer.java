package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityConnectionWire;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ConnectionWireRenderer implements ISimpleBlockRenderingHandler {
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntityConnectionWire wire = (TileEntityConnectionWire) world.getBlockTileEntity(x, y, z);
        
        float minSize = 0.375F;
        float maxSize = 0.625F;
        
        block.setBlockBounds(minSize, minSize, minSize, maxSize, maxSize, maxSize);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
        
        if (wire.isConnected(ForgeDirection.DOWN)) {
            block.setBlockBounds(minSize, 0.0F, minSize, maxSize, minSize, maxSize);
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        if (wire.isConnected(ForgeDirection.UP)) {
            block.setBlockBounds(minSize, maxSize, minSize, maxSize, 1.0F, maxSize);
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        if (wire.isConnected(ForgeDirection.NORTH)) {
            block.setBlockBounds(minSize, minSize, 0.0F, maxSize, maxSize, minSize);
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        if (wire.isConnected(ForgeDirection.SOUTH)) {
            block.setBlockBounds(minSize, minSize, maxSize, maxSize, maxSize, 1.0F);
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        if (wire.isConnected(ForgeDirection.WEST)) {
            block.setBlockBounds(0.0F, minSize, minSize, minSize, maxSize, maxSize);
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        if (wire.isConnected(ForgeDirection.EAST)) {
            block.setBlockBounds(maxSize, minSize, minSize, 1.0F, maxSize, maxSize);
            renderer.setRenderBoundsFromBlock(block);
            renderer.renderStandardBlock(block, x, y, z);
        }
        
        block.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        return true;
    }
    
    @Override
    public boolean shouldRender3DInInventory() {
        return false;
    }
    
    @Override
    public int getRenderId() {
        return BuildToWin.connectionWireRenderId;
    }
}
