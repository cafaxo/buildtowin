package buildtowin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.EntityRenderer;
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
    public boolean renderWorldBlock(IBlockAccess world, int par2, int par3, int par4, Block block, int modelId, RenderBlocks renderer) {
        this.fakeWorld = new FakeWorld(world);
        this.blockAccess = this.fakeWorld;
        
        this.renderMaxX = renderer.renderMaxX;
        this.renderMaxY = renderer.renderMaxY;
        this.renderMaxZ = renderer.renderMaxZ;
        this.renderMinX = renderer.renderMinX;
        this.renderMinX = renderer.renderMinX;
        this.renderMinY = renderer.renderMinY;
        this.renderMinZ = renderer.renderMinZ;
        
        TileEntityBlueprint te = (TileEntityBlueprint) world.getBlockTileEntity(par2, par3, par4);
        Block fakeBlock = Block.blocksList[te.getBlockId()];
        if (fakeBlock != null) {
            switch (fakeBlock.getRenderType()) {
            case 31:
                return this.renderBlockLog(fakeBlock, par2, par3, par4);
            case 1:
                return this.renderCrossedSquares(fakeBlock, par2, par3, par4);
            case 2:
                this.fakeWorld.overrideSurroundingBlueprints(par2, par3, par4);
                return this.renderBlockTorch(fakeBlock, par2, par3, par4);
            case 11:
                this.fakeWorld.overrideSurroundingBlueprints(par2, par3, par4);
                return this.renderBlockFence((BlockFence) fakeBlock, par2, par3, par4);
            case 10:
                this.fakeWorld.overrideSurroundingBlueprints(par2, par3, par4);
                return this.renderBlockStairs((BlockStairs) fakeBlock, par2, par3, par4);
            case 32:
                this.fakeWorld.overrideSurroundingBlueprints(par2, par3, par4);
                return this.renderBlockWall((BlockWall) fakeBlock, par2, par3, par4);
            case 21:
                this.fakeWorld.overrideSurroundingBlueprints(par2, par3, par4);
                return this.renderBlockFenceGate((BlockFenceGate) fakeBlock, par2, par3, par4);
            default:
                return this.renderStandardBlock(fakeBlock, par2, par3, par4);
            }
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
    public boolean renderBlockTorch(Block par1Block, int par2, int par3, int par4) {
        int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorRGBA_F(0.3F, 0.3F, 1.0F, 0.7F);
        double d0 = 0.4000000059604645D;
        double d1 = 0.5D - d0;
        double d2 = 0.20000000298023224D;
        
        if (l == 1)
        {
            this.renderTorchAtAngle(par1Block, (double) par2 - d1, (double) par3 + d2, (double) par4, -d0, 0.0D, 0);
        }
        else if (l == 2)
        {
            this.renderTorchAtAngle(par1Block, (double) par2 + d1, (double) par3 + d2, (double) par4, d0, 0.0D, 0);
        }
        else if (l == 3)
        {
            this.renderTorchAtAngle(par1Block, (double) par2, (double) par3 + d2, (double) par4 - d1, 0.0D, -d0, 0);
        }
        else if (l == 4)
        {
            this.renderTorchAtAngle(par1Block, (double) par2, (double) par3 + d2, (double) par4 + d1, 0.0D, d0, 0);
        }
        else
        {
            this.renderTorchAtAngle(par1Block, (double) par2, (double) par3, (double) par4, 0.0D, 0.0D, 0);
        }
        
        return true;
    }
    
    @Override
    public boolean renderCrossedSquares(Block par1Block, int par2, int par3, int par4) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        float f = 1.0F;
        int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (float) (l >> 16 & 255) / 255.0F;
        float f2 = (float) (l >> 8 & 255) / 255.0F;
        float f3 = (float) (l & 255) / 255.0F;
        
        if (EntityRenderer.anaglyphEnable)
        {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }
        
        tessellator.setColorRGBA_F(f * f1 * 0.3F, f * f2 * 0.3F, f * f3, 0.7F);
        double d0 = (double) par2;
        double d1 = (double) par3;
        double d2 = (double) par4;
        
        if (par1Block == Block.tallGrass)
        {
            long i1 = (long) (par2 * 3129871) ^ (long) par4 * 116129781L ^ (long) par3;
            i1 = i1 * i1 * 42317861L + i1 * 11L;
            d0 += ((double) ((float) (i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
            d1 += ((double) ((float) (i1 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
            d2 += ((double) ((float) (i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
        }
        
        this.drawCrossedSquares(par1Block, this.blockAccess.getBlockMetadata(par2, par3, par4), d0, d1, d2, 1.0F);
        return true;
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
