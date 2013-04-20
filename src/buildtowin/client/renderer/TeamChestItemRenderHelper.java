package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityTeamChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TeamChestItemRenderHelper extends ChestItemRenderHelper {
    
    private TileEntityChest theChest = new TileEntityChest();
    
    private TileEntityEnderChest theEnderChest = new TileEntityEnderChest();
    
    private TileEntityTeamChest teamChest = new TileEntityTeamChest();
    
    public void renderChest(Block par1Block, int par2, float par3) {
        if (par1Block.blockID == Block.enderChest.blockID) {
            TileEntityRenderer.instance.renderTileEntityAt(this.theEnderChest, 0.0D, 0.0D, 0.0D, 0.0F);
        } else if (par1Block.blockID == BuildToWin.teamChest.blockID) {
            TileEntityRenderer.instance.renderTileEntityAt(this.teamChest, 0.0D, 0.0D, 0.0D, 0.0F);
        } else {
            TileEntityRenderer.instance.renderTileEntityAt(this.theChest, 0.0D, 0.0D, 0.0D, 0.0F);
        }
    }
}
