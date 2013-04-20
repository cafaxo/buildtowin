package buildtowin.block;

import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityTeamChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTeamChest extends BlockChest {
    
    public BlockTeamChest(int blockId) {
        super(blockId, 0);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setUnlocalizedName("teamChest");
    }
    
    public TileEntity createNewTileEntity(World par1World) {
        return new TileEntityTeamChest();
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("wood");
    }
}
