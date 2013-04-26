package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import buildtowin.BuildToWin;
import buildtowin.util.Color;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorBlockRenderer implements ISimpleBlockRenderingHandler {
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        IColoredBlock coloredBlock = (IColoredBlock) block;
        
        coloredBlock.switchIconToStandard();
        
        if (Minecraft.isAmbientOcclusionEnabled()) {
            renderer.renderStandardBlockWithAmbientOcclusion(block, x, y, z, 1.0F, 1.0F, 1.0F);
        } else {
            renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, 1.0F, 1.0F, 1.0F);
        }
        
        coloredBlock.switchIconToOverlay();
        Color color = coloredBlock.getColor(world, x, y, z);
        
        if (Minecraft.isAmbientOcclusionEnabled()) {
            renderer.renderStandardBlockWithAmbientOcclusion(block, x, y, z, color.r, color.g, color.b);
        } else {
            renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, color.r, color.g, color.b);
        }
        
        coloredBlock.switchIconToStandard();
        
        return true;
    }
    
    @Override
    public boolean shouldRender3DInInventory() {
        return false;
    }
    
    @Override
    public int getRenderId() {
        return BuildToWin.coloredBlockRenderId;
    }
}
