package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
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
        
        float c_red = 0.3F;
        float c_green = 0.3F;
        float c_blue = 1;
        float c_alpha = 0.8F;
        
        float var10 = 0.5F;
        float var11 = 1.0F;
        float var12 = 0.8F;
        float var13 = 0.6F;
        float var14 = var11 * c_red;
        float var15 = var11 * c_green;
        float var16 = var11 * c_blue;
        float var17 = var10;
        float var18 = var12;
        float var19 = var13;
        float var20 = var10;
        float var21 = var12;
        float var22 = var13;
        float var23 = var10;
        float var24 = var12;
        float var25 = var13;
        
        if (block != Block.grass)
        {
            var17 = var10 * c_red;
            var18 = var12 * c_red;
            var19 = var13 * c_red;
            var20 = var10 * c_green;
            var21 = var12 * c_green;
            var22 = var13 * c_green;
            var23 = var10 * c_blue;
            var24 = var12 * c_blue;
            var25 = var13 * c_blue;
        }
        
        int var26 = block.getMixedBrightnessForBlock(world, x, y, z);
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
            tessellator.setBrightness(renderer.renderMinY > 0.0D ? var26 : block.getMixedBrightnessForBlock(world, x, y - 1, z));
            tessellator.setColorRGBA_F(var17, var20, var23, c_alpha);
            renderer.renderBottomFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 0));
            wasRendered = true;
        }
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
            tessellator.setBrightness(renderer.renderMaxY < 1.0D ? var26 : block.getMixedBrightnessForBlock(world, x, y + 1, z));
            tessellator.setColorRGBA_F(var14, var15, var16, c_alpha);
            renderer.renderTopFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 1));
            wasRendered = true;
        }
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
            tessellator.setBrightness(renderer.renderMinZ > 0.0D ? var26 : block.getMixedBrightnessForBlock(world, x, y, z - 1));
            tessellator.setColorRGBA_F(var18, var21, var24, c_alpha);
            renderer.renderEastFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 2));
            wasRendered = true;
        }
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
            tessellator.setBrightness(renderer.renderMaxZ < 1.0D ? var26 : block.getMixedBrightnessForBlock(world, x, y, z + 1));
            tessellator.setColorRGBA_F(var18, var21, var24, c_alpha);
            renderer.renderWestFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 3));
            wasRendered = true;
        }
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
            tessellator.setBrightness(renderer.renderMinX > 0.0D ? var26 : block.getMixedBrightnessForBlock(world, x - 1, y, z));
            tessellator.setColorRGBA_F(var19, var22, var25, c_alpha);
            renderer.renderNorthFace(block, (double) x, (double) y, (double) z, renderer.getBlockIcon(block, world, x, y, z, 4));
            wasRendered = true;
        }
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
            tessellator.setBrightness(renderer.renderMaxX < 1.0D ? var26 : block.getMixedBrightnessForBlock(world, x + 1, y, z));
            tessellator.setColorRGBA_F(var19, var22, var25, c_alpha);
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
        return BuildToWin.renderID;
    }
}
