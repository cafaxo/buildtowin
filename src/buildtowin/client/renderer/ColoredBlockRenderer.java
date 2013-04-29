package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Color;
import buildtowin.util.PlayerList;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ColoredBlockRenderer implements ISimpleBlockRenderingHandler {
    
    @Override
    public void renderInventoryBlock(Block par1Block, int metadata, int modelID, RenderBlocks renderer) {
        par1Block.setBlockBoundsForItemRender();
        renderer.setRenderBoundsFromBlock(par1Block);
        
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        IColoredBlock coloredBlock = (IColoredBlock) par1Block;
        
        coloredBlock.switchIconToStandard();
        
        this.renderInventoryBlock(par1Block, metadata, renderer, new Color(1.0F, 1.0F, 1.0F));
        
        coloredBlock.switchIconToOverlay();
        
        TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider(Minecraft.getMinecraft().thePlayer, TileEntityTeamHub.class);
        
        Color color = new Color(1.0F, 1.0F, 1.0F);
        
        if (teamHub != null) {
            color = Color.fromId(teamHub.getColor().id);
        }
        
        this.renderInventoryBlock(par1Block, metadata, renderer, color);
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
    
    public void renderBlockAsItem(Block block, RenderEngine renderEngine, RenderBlocks renderBlocks, int x, int y, float zLevel, Color color) {
        renderEngine.bindTexture("/terrain.png");
        
        GL11.glPushMatrix();
        
        GL11.glTranslatef(x - 2, y + 3, -3.0F + zLevel);
        GL11.glScalef(10.0F, 10.0F, 10.0F);
        GL11.glTranslatef(1.0F, 0.5F, 1.0F);
        GL11.glScalef(1.0F, 1.0F, -1.0F);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        
        block.setBlockBoundsForItemRender();
        renderBlocks.setRenderBoundsFromBlock(block);
        
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        IColoredBlock coloredBlock = (IColoredBlock) block;
        
        coloredBlock.switchIconToStandard();
        
        BuildToWin.coloredBlockRenderer.renderInventoryBlock(block, 0, renderBlocks, new Color(1.0F, 1.0F, 1.0F));
        
        coloredBlock.switchIconToOverlay();
        
        BuildToWin.coloredBlockRenderer.renderInventoryBlock(block, 0, renderBlocks, color);
        
        coloredBlock.switchIconToStandard();
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        
        GL11.glPopMatrix();
    }
    
    public void renderInventoryBlock(Block par1Block, int metadata, RenderBlocks renderer, Color color) {
        Tessellator tessellator = Tessellator.instance;
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        tessellator.setColorRGBA_F(color.r, color.g, color.b, 1.0F);
        renderer.renderBottomFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 0, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.setColorRGBA_F(color.r, color.g, color.b, 1.0F);
        renderer.renderTopFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 1, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.setColorRGBA_F(color.r, color.g, color.b, 1.0F);
        renderer.renderEastFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 2, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.setColorRGBA_F(color.r, color.g, color.b, 1.0F);
        renderer.renderWestFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 3, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        tessellator.setColorRGBA_F(color.r, color.g, color.b, 1.0F);
        renderer.renderNorthFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 4, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        tessellator.setColorRGBA_F(color.r, color.g, color.b, 1.0F);
        renderer.renderSouthFace(par1Block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(par1Block, 5, metadata));
        tessellator.draw();
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
