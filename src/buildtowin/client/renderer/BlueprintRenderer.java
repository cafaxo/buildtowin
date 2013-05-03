package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import buildtowin.BuildToWin;
import buildtowin.tileentity.IBlueprintProvider;
import buildtowin.tileentity.TileEntityBlueprint;
import buildtowin.util.Color;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlueprintRenderer extends RenderBlocks implements ISimpleBlockRenderingHandler {
    
    private FakeWorld fakeWorld = new FakeWorld();
    
    public BlueprintRenderer() {
        this.blockAccess = this.fakeWorld;
    }
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        this.fakeWorld.realWorld = world;
        
        TileEntityBlueprint blueprint = (TileEntityBlueprint) world.getBlockTileEntity(x, y, z);
        
        TessellatorColorReplacer tessellator = (TessellatorColorReplacer) Tessellator.instance;
        
        tessellator.replaceColor = true;
        
        Color replacedColor;
        
        IBlueprintProvider blueprintProvider = (IBlueprintProvider) blueprint.getBlueprintProvider();
        
        if (blueprintProvider != null) {
            replacedColor = blueprintProvider.getColor();
        } else {
            replacedColor = new Color(1.0F, 1.0F, 1.0F);
        }
        
        tessellator.red = replacedColor.r;
        tessellator.green = replacedColor.g;
        tessellator.blue = replacedColor.b;
        tessellator.alpha = 0.7F;
        
        Block fakeBlock = blueprint.getSavedBlock();
        boolean wasRendered = false;
        
        if (fakeBlock != null) {
            wasRendered = this.renderBlockByRenderType(fakeBlock, x, y, z);
        }
        
        tessellator.replaceColor = false;
        
        return wasRendered;
    }
    
    @Override
    public void renderFaceYNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
        if (!this.fakeWorld.isBlockOpaqueCube((int) par2, (int) par4 - 1, (int) par6)
                || this.fakeWorld.realWorld.getBlockId((int) par2, (int) par4 - 1, (int) par6) != BuildToWin.blueprint.blockID) {
            super.renderFaceYNeg(par1Block, par2, par4, par6, par8Icon);
        }
    }
    
    @Override
    public void renderFaceYPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
        if (!this.fakeWorld.isBlockOpaqueCube((int) par2, (int) par4 + 1, (int) par6)
                || this.fakeWorld.realWorld.getBlockId((int) par2, (int) par4 + 1, (int) par6) != BuildToWin.blueprint.blockID) {
            super.renderFaceYPos(par1Block, par2, par4, par6, par8Icon);
        }
    }
    
    @Override
    public void renderFaceZNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
        if (!this.fakeWorld.isBlockOpaqueCube((int) par2, (int) par4, (int) par6 - 1)
                || this.fakeWorld.realWorld.getBlockId((int) par2, (int) par4, (int) par6 - 1) != BuildToWin.blueprint.blockID) {
            super.renderFaceZNeg(par1Block, par2, par4, par6, par8Icon);
        }
    }
    
    @Override
    public void renderFaceZPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
        if (!this.fakeWorld.isBlockOpaqueCube((int) par2, (int) par4, (int) par6 + 1)
                || this.fakeWorld.realWorld.getBlockId((int) par2, (int) par4, (int) par6 + 1) != BuildToWin.blueprint.blockID) {
            super.renderFaceZPos(par1Block, par2, par4, par6, par8Icon);
        }
    }
    
    @Override
    public void renderFaceXNeg(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
        if (!this.fakeWorld.isBlockOpaqueCube((int) par2 - 1, (int) par4, (int) par6)
                || this.fakeWorld.realWorld.getBlockId((int) par2 - 1, (int) par4, (int) par6) != BuildToWin.blueprint.blockID) {
            super.renderFaceXNeg(par1Block, par2, par4, par6, par8Icon);
        }
    }
    
    @Override
    public void renderFaceXPos(Block par1Block, double par2, double par4, double par6, Icon par8Icon) {
        if (!this.fakeWorld.isBlockOpaqueCube((int) par2 + 1, (int) par4, (int) par6)
                || this.fakeWorld.realWorld.getBlockId((int) par2 + 1, (int) par4, (int) par6) != BuildToWin.blueprint.blockID) {
            super.renderFaceXPos(par1Block, par2, par4, par6, par8Icon);
        }
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
