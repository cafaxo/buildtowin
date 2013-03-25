package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlueprintRenderer extends RenderBlocks implements ISimpleBlockRenderingHandler {
    FakeWorld fakeWorld;
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        this.fakeWorld = new FakeWorld(world);
        this.blockAccess = this.fakeWorld;
        
        this.fakeWorld.overrideSurroundingBlueprints(x, y, z);
        
        this.renderMaxX = renderer.renderMaxX;
        this.renderMaxY = renderer.renderMaxY;
        this.renderMaxZ = renderer.renderMaxZ;
        this.renderMinX = renderer.renderMinX;
        this.renderMinX = renderer.renderMinX;
        this.renderMinY = renderer.renderMinY;
        this.renderMinZ = renderer.renderMinZ;
        
        TileEntityBlueprint te = (TileEntityBlueprint) world.getBlockTileEntity(x, y, z);
        Block fakeBlock = Block.blocksList[te.getBlockId()];
        
        switch (fakeBlock.getRenderType()) {
        case 0:
            return this.renderStandardBlock(block, x, y, z);
        case 31:
            return this.renderBlockLog(fakeBlock, x, y, z);
        case 11:
            return this.renderBlockFence((BlockFence) fakeBlock, x, y, z);
        }
        
        return false;
    }
    
    @Override
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        this.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean wasRendered = false;
        
        float red = 0.3F;
        float green = 0.3F;
        float blue = 1.0F;
        float alpha = 0.7F;
        
        if (block.shouldSideBeRendered(fakeWorld, x, y - 1, z, 0)) {
            tessellator.setBrightness(this.renderMinY > 0.0D ? 0 : block.getMixedBrightnessForBlock(fakeWorld, x, y - 1, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            this.renderBottomFace(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, fakeWorld, x, y, z, 0));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(fakeWorld, x, y + 1, z, 1)) {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? 0 : block.getMixedBrightnessForBlock(fakeWorld, x, y + 1, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            this.renderTopFace(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, fakeWorld, x, y, z, 1));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(fakeWorld, x, y, z - 1, 2)) {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? 0 : block.getMixedBrightnessForBlock(fakeWorld, x, y, z - 1));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            this.renderEastFace(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, fakeWorld, x, y, z, 2));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(fakeWorld, x, y, z + 1, 3)) {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? 0 : block.getMixedBrightnessForBlock(fakeWorld, x, y, z + 1));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            this.renderWestFace(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, fakeWorld, x, y, z, 3));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(fakeWorld, x - 1, y, z, 4)) {
            tessellator.setBrightness(this.renderMinX > 0.0D ? 0 : block.getMixedBrightnessForBlock(fakeWorld, x - 1, y, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            this.renderNorthFace(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, fakeWorld, x, y, z, 4));
            wasRendered = true;
        }
        
        if (block.shouldSideBeRendered(fakeWorld, x + 1, y, z, 5)) {
            tessellator.setBrightness(this.renderMaxX < 1.0D ? 0 : block.getMixedBrightnessForBlock(fakeWorld, x + 1, y, z));
            tessellator.setColorRGBA_F(red, green, blue, alpha);
            this.renderSouthFace(block, (double) x, (double) y, (double) z, this.getBlockIcon(block, fakeWorld, x, y, z, 5));
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
