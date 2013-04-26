package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityBlueprint;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlueprintRenderer implements ISimpleBlockRenderingHandler {
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        renderer.blockAccess = new FakeWorld(world);
        
        TileEntityBlueprint blueprint = (TileEntityBlueprint) world.getBlockTileEntity(x, y, z);
        
        TessellatorColorReplacer tessellator = (TessellatorColorReplacer) Tessellator.instance;
        
        tessellator.replaceColor = true;
        
        tessellator.red = blueprint.getColor().r;
        tessellator.green = blueprint.getColor().g;
        tessellator.blue = blueprint.getColor().b;
        tessellator.alpha = 0.7F;
        
        Block fakeBlock = blueprint.getBlockData().getSavedBlock();
        boolean wasRendered = false;
        
        if (fakeBlock != null) {
            wasRendered = renderer.renderBlockByRenderType(fakeBlock, x, y, z);
        }
        
        tessellator.replaceColor = false;
        renderer.blockAccess = world;
        
        return wasRendered;
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
