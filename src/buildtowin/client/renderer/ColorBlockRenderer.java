package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import buildtowin.BuildToWin;
import buildtowin.util.Color;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColorBlockRenderer implements ISimpleBlockRenderingHandler {
    
    @Override
    public void renderInventoryBlock(Block par1Block, int metadata, int modelID, RenderBlocks renderer) {
        par1Block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(par1Block);
        
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        Tessellator tessellator = Tessellator.instance;
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderBottomFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 0, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderTopFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 1, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderEastFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 2, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderWestFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 3, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderNorthFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 4, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderSouthFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 5, metadata));
        tessellator.draw();
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
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
        return true;
    }
    
    @Override
    public int getRenderId() {
        return BuildToWin.coloredBlockRenderId;
    }
}
