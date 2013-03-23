package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlueprintRenderer implements ISimpleBlockRenderingHandler {
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        renderer.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean wasRendered = false;
        
        float red = 0.3F;
        float green = 0.3F;
        float blue = 1.0F;
        float alpha = 0.7F;
        
        if (block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
            tessellator.setBrightness(renderer.renderMinY > 0.0D ? 0 : block.getMixedBrightnessForBlock(world, x, y - 1, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            renderer.renderBottomFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 0));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
            tessellator.setBrightness(renderer.renderMaxY < 1.0D ? 0 : block.getMixedBrightnessForBlock(world, x, y + 1, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            renderer.renderTopFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 1));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
            tessellator.setBrightness(renderer.renderMinZ > 0.0D ? 0 : block.getMixedBrightnessForBlock(world, x, y, z - 1));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            renderer.renderEastFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 2));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
            tessellator.setBrightness(renderer.renderMaxZ < 1.0D ? 0 : block.getMixedBrightnessForBlock(world, x, y, z + 1));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            renderer.renderWestFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 3));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
            tessellator.setBrightness(renderer.renderMinX > 0.0D ? 0 : block.getMixedBrightnessForBlock(world, x - 1, y, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            renderer.renderNorthFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 4));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
            tessellator.setBrightness(renderer.renderMaxX < 1.0D ? 0 : block.getMixedBrightnessForBlock(world, x + 1, y, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            renderer.renderSouthFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 5));
            wasRendered = true;
        }
        
        return wasRendered;
    }
    
    @Override
    public boolean shouldRender3DInInventory() {
        return false;
    }
    
    @Override
    public int getRenderId() {
        return BuildToWin.blueprintRenderingId;
    }
}
