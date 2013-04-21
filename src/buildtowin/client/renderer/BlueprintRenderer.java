package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityBlueprint;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlueprintRenderer extends RenderBlocks implements ISimpleBlockRenderingHandler {
    
    private float red, green, blue, alpha;
    
    private IBlockAccess realWorld;
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int par2, int par3, int par4, Block block, int modelId, RenderBlocks renderer) {
        this.realWorld = world;
        this.blockAccess = new FakeWorld(world);
        
        TileEntityBlueprint blueprint = (TileEntityBlueprint) world.getBlockTileEntity(par2, par3, par4);
        
        int existingColor = blueprint.getColor() % (TileEntityBlueprint.colors.length / 4);
        
        this.red = TileEntityBlueprint.colors[existingColor * 4];
        this.green = TileEntityBlueprint.colors[existingColor * 4 + 1];
        this.blue = TileEntityBlueprint.colors[existingColor * 4 + 2];
        this.alpha = TileEntityBlueprint.colors[existingColor * 4 + 3];
        
        Block fakeBlock = Block.blocksList[blueprint.getBlockData().savedId];
        
        if (fakeBlock != null) {
            if (fakeBlock.getRenderType() == 0) {
                fakeBlock.setBlockBoundsBasedOnState(this.blockAccess, par2, par3, par4);
                this.setRenderBoundsFromBlock(fakeBlock);
                return this.renderStandardBlock(fakeBlock, par2, par3, par4);
            }
            
            fakeBlock.setBlockBoundsBasedOnState(this.blockAccess, par2, par3, par4);
            this.setRenderBoundsFromBlock(fakeBlock);
            
            switch (fakeBlock.getRenderType()) {
            case 31:
                return this.renderBlockLog(fakeBlock, par2, par3, par4);
            case 1:
                return this.renderCrossedSquares(fakeBlock, par2, par3, par4);
            case 2:
                return this.renderBlockTorch(fakeBlock, par2, par3, par4);
            case 11:
                return this.renderBlockFence((BlockFence) fakeBlock, par2, par3, par4);
            case 10:
                return this.renderBlockStairs((BlockStairs) fakeBlock, par2, par3, par4);
            case 32:
                return this.renderBlockWall((BlockWall) fakeBlock, par2, par3, par4);
            case 21:
                return this.renderBlockFenceGate((BlockFenceGate) fakeBlock, par2, par3, par4);
            case 7:
                return this.renderBlockDoor(fakeBlock, par2, par3, par4);
            case 14:
                return this.renderBlockBed(fakeBlock, par2, par3, par4);
            case 35:
                return this.renderBlockAnvil((BlockAnvil) fakeBlock, par2, par3, par4);
            default:
                return this.renderStandardBlock(fakeBlock, par2, par3, par4);
            }
        } else {
            return false;
        }
    }
    
    @Override
    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        this.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        
        int brightness = block.getMixedBrightnessForBlock(this.blockAccess, x, y, z);
        
        if (this.realWorld.getBlockId(x, y - 1, z) != BuildToWin.blueprint.blockID
                && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x, y - 1, z, 0))) {
            tessellator.setBrightness(this.renderMinY > 0.0D ? brightness : block.getMixedBrightnessForBlock(this.blockAccess, x, y - 1, z));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.renderBottomFace(block, x, y, z, this.getBlockIcon(block, this.blockAccess, x, y, z, 0));
            
            flag = true;
        }
        
        if (this.realWorld.getBlockId(x, y + 1, z) != BuildToWin.blueprint.blockID
                && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x, y + 1, z, 1))) {
            tessellator.setBrightness(this.renderMaxY < 1.0D ? brightness : block.getMixedBrightnessForBlock(this.blockAccess, x, y + 1, z));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.renderTopFace(block, x, y, z, this.getBlockIcon(block, this.blockAccess, x, y, z, 1));
            
            flag = true;
        }
        
        if (this.realWorld.getBlockId(x, y, z - 1) != BuildToWin.blueprint.blockID
                && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x, y, z - 1, 2))) {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? brightness : block.getMixedBrightnessForBlock(this.blockAccess, x, y, z - 1));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.renderEastFace(block, x, y, z, this.getBlockIcon(block, this.blockAccess, x, y, z, 2));
            
            flag = true;
        }
        
        if (this.realWorld.getBlockId(x, y, z + 1) != BuildToWin.blueprint.blockID
                && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x, y, z + 1, 3))) {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? brightness : block.getMixedBrightnessForBlock(this.blockAccess, x, y, z + 1));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.renderWestFace(block, x, y, z, this.getBlockIcon(block, this.blockAccess, x, y, z, 3));
            
            flag = true;
        }
        
        if (this.realWorld.getBlockId(x - 1, y, z) != BuildToWin.blueprint.blockID
                && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x - 1, y, z, 4))) {
            tessellator.setBrightness(this.renderMinX > 0.0D ? brightness : block.getMixedBrightnessForBlock(this.blockAccess, x - 1, y, z));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.renderNorthFace(block, x, y, z, this.getBlockIcon(block, this.blockAccess, x, y, z, 4));
            
            flag = true;
        }
        
        if (this.realWorld.getBlockId(x + 1, y, z) != BuildToWin.blueprint.blockID
                && (this.renderAllFaces || block.shouldSideBeRendered(this.blockAccess, x + 1, y, z, 5))) {
            tessellator.setBrightness(this.renderMaxX < 1.0D ? brightness : block.getMixedBrightnessForBlock(this.blockAccess, x + 1, y, z));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.renderSouthFace(block, x, y, z, this.getBlockIcon(block, this.blockAccess, x, y, z, 5));
            
            flag = true;
        }
        
        return flag;
    }
    
    @Override
    public boolean renderBlockTorch(Block par1Block, int par2, int par3, int par4) {
        int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        
        double d0 = 0.4000000059604645D;
        double d1 = 0.5D - d0;
        double d2 = 0.20000000298023224D;
        
        if (l == 1) {
            this.renderTorchAtAngle(par1Block, par2 - d1, par3 + d2, par4, -d0, 0.0D, 0);
        } else if (l == 2) {
            this.renderTorchAtAngle(par1Block, par2 + d1, par3 + d2, par4, d0, 0.0D, 0);
        } else if (l == 3) {
            this.renderTorchAtAngle(par1Block, par2, par3 + d2, par4 - d1, 0.0D, -d0, 0);
        } else if (l == 4) {
            this.renderTorchAtAngle(par1Block, par2, par3 + d2, par4 + d1, 0.0D, d0, 0);
        } else {
            this.renderTorchAtAngle(par1Block, par2, par3, par4, 0.0D, 0.0D, 0);
        }
        
        return true;
    }
    
    @Override
    public boolean renderCrossedSquares(Block par1Block, int par2, int par3, int par4) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4));
        float f = 1.0F;
        int l = par1Block.colorMultiplier(this.blockAccess, par2, par3, par4);
        float f1 = (l >> 16 & 255) / 255.0F;
        float f2 = (l >> 8 & 255) / 255.0F;
        float f3 = (l & 255) / 255.0F;
        
        if (EntityRenderer.anaglyphEnable) {
            float f4 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            float f5 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f4;
            f2 = f5;
            f3 = f6;
        }
        
        tessellator.setColorRGBA_F(f * f1 * this.red, f * f2 * this.green, f * f3 * this.blue, this.alpha);
        double d0 = par2;
        double d1 = par3;
        double d2 = par4;
        
        if (par1Block == Block.tallGrass) {
            long i1 = par2 * 3129871 ^ par4 * 116129781L ^ par3;
            i1 = i1 * i1 * 42317861L + i1 * 11L;
            d0 += ((i1 >> 16 & 15L) / 15.0F - 0.5D) * 0.5D;
            d1 += ((i1 >> 20 & 15L) / 15.0F - 1.0D) * 0.2D;
            d2 += ((i1 >> 24 & 15L) / 15.0F - 0.5D) * 0.5D;
        }
        
        this.drawCrossedSquares(par1Block, this.blockAccess.getBlockMetadata(par2, par3, par4), d0, d1, d2, 1.0F);
        return true;
    }
    
    @Override
    public boolean renderBlockDoor(Block par1Block, int par2, int par3, int par4) {
        Tessellator tessellator = Tessellator.instance;
        int l = this.blockAccess.getBlockMetadata(par2, par3, par4);
        
        if ((l & 8) != 0) {
            if (this.blockAccess.getBlockId(par2, par3 - 1, par4) != par1Block.blockID) {
                return false;
            }
        } else if (this.blockAccess.getBlockId(par2, par3 + 1, par4) != par1Block.blockID) {
            return false;
        }
        
        boolean flag = false;
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        
        int i1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
        tessellator.setBrightness(this.renderMinY > 0.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 - 1, par4));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        this.renderBottomFace(par1Block, par2, par3, par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0));
        flag = true;
        
        tessellator.setBrightness(this.renderMaxY < 1.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        this.renderTopFace(par1Block, par2, par3, par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1));
        flag = true;
        
        tessellator.setBrightness(this.renderMinZ > 0.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        Icon icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2);
        this.renderEastFace(par1Block, par2, par3, par4, icon);
        flag = true;
        
        this.flipTexture = false;
        tessellator.setBrightness(this.renderMaxZ < 1.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3);
        this.renderWestFace(par1Block, par2, par3, par4, icon);
        flag = true;
        
        this.flipTexture = false;
        tessellator.setBrightness(this.renderMinX > 0.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4);
        this.renderNorthFace(par1Block, par2, par3, par4, icon);
        flag = true;
        
        this.flipTexture = false;
        tessellator.setBrightness(this.renderMaxX < 1.0D ? i1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5);
        this.renderSouthFace(par1Block, par2, par3, par4, icon);
        flag = true;
        
        this.flipTexture = false;
        
        return flag;
    }
    
    @Override
    public boolean renderBlockBed(Block par1Block, int par2, int par3, int par4) {
        Tessellator tessellator = Tessellator.instance;
        int i1 = par1Block.getBedDirection(this.blockAccess, par2, par3, par4);
        boolean flag = par1Block.isBedFoot(this.blockAccess, par2, par3, par4);
        float f = 0.5F;
        float f1 = 1.0F;
        float f2 = 0.8F;
        float f3 = 0.6F;
        int j1 = par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4);
        tessellator.setBrightness(j1);
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        Icon icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 0);
        
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }
        
        double d0 = icon.getMinU();
        double d1 = icon.getMaxU();
        double d2 = icon.getMinV();
        double d3 = icon.getMaxV();
        double d4 = par2 + this.renderMinX;
        double d5 = par2 + this.renderMaxX;
        double d6 = par3 + this.renderMinY + 0.1875D;
        double d7 = par4 + this.renderMinZ;
        double d8 = par4 + this.renderMaxZ;
        
        tessellator.addVertexWithUV(d4, d6, d8, d0, d3);
        tessellator.addVertexWithUV(d4, d6, d7, d0, d2);
        tessellator.addVertexWithUV(d5, d6, d7, d1, d2);
        tessellator.addVertexWithUV(d5, d6, d8, d1, d3);
        tessellator.setBrightness(par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3 + 1, par4));
        tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
        icon = this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 1);
        
        if (this.hasOverrideBlockTexture()) {
            icon = this.overrideBlockTexture;
        }
        
        d0 = icon.getMinU();
        d1 = icon.getMaxU();
        d2 = icon.getMinV();
        d3 = icon.getMaxV();
        d4 = d0;
        d5 = d1;
        d6 = d2;
        d7 = d2;
        d8 = d0;
        double d9 = d1;
        double d10 = d3;
        double d11 = d3;
        
        if (i1 == 0) {
            d5 = d0;
            d6 = d3;
            d8 = d1;
            d11 = d2;
        } else if (i1 == 2) {
            d4 = d1;
            d7 = d3;
            d9 = d0;
            d10 = d2;
        } else if (i1 == 3) {
            d4 = d1;
            d7 = d3;
            d9 = d0;
            d10 = d2;
            d5 = d0;
            d6 = d3;
            d8 = d1;
            d11 = d2;
        }
        
        double d12 = par2 + this.renderMinX;
        double d13 = par2 + this.renderMaxX;
        double d14 = par3 + this.renderMaxY;
        double d15 = par4 + this.renderMinZ;
        double d16 = par4 + this.renderMaxZ;
        
        tessellator.addVertexWithUV(d13, d14, d16, d8, d10);
        tessellator.addVertexWithUV(d13, d14, d15, d4, d6);
        tessellator.addVertexWithUV(d12, d14, d15, d5, d7);
        tessellator.addVertexWithUV(d12, d14, d16, d9, d11);
        int k1 = Direction.headInvisibleFace[i1];
        
        if (flag) {
            k1 = Direction.headInvisibleFace[Direction.footInvisibleFaceRemap[i1]];
        }
        
        byte b0 = 4;
        
        switch (i1) {
        case 0:
            b0 = 5;
            break;
        case 1:
            b0 = 3;
        case 2:
        default:
            break;
        case 3:
            b0 = 2;
        }
        
        if (k1 != 2 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 - 1, 2))) {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 - 1));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.flipTexture = b0 == 2;
            this.renderEastFace(par1Block, par2, par3, par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 2));
        }
        
        if (k1 != 3 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2, par3, par4 + 1, 3))) {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2, par3, par4 + 1));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.flipTexture = b0 == 3;
            this.renderWestFace(par1Block, par2, par3, par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 3));
        }
        
        if (k1 != 4 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 - 1, par3, par4, 4))) {
            tessellator.setBrightness(this.renderMinZ > 0.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 - 1, par3, par4));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.flipTexture = b0 == 4;
            this.renderNorthFace(par1Block, par2, par3, par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 4));
        }
        
        if (k1 != 5 && (this.renderAllFaces || par1Block.shouldSideBeRendered(this.blockAccess, par2 + 1, par3, par4, 5))) {
            tessellator.setBrightness(this.renderMaxZ < 1.0D ? j1 : par1Block.getMixedBrightnessForBlock(this.blockAccess, par2 + 1, par3, par4));
            tessellator.setColorRGBA_F(this.red, this.green, this.blue, this.alpha);
            this.flipTexture = b0 == 5;
            this.renderSouthFace(par1Block, par2, par3, par4, this.getBlockIcon(par1Block, this.blockAccess, par2, par3, par4, 5));
        }
        
        this.flipTexture = false;
        return true;
    }
    
    @Override
    public boolean shouldRender3DInInventory() {
        return false;
    }
    
    @Override
    public int getRenderId() {
        return BuildToWin.blueprintRenderId;
    }
}
