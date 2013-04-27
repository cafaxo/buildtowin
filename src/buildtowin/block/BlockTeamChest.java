package buildtowin.block;

import net.minecraft.block.BlockChest;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityTeamChest;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.PlayerList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTeamChest extends BlockChest {
    
    public BlockTeamChest(int blockId) {
        super(blockId, 0);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setHardness(50.0F);
        this.setResistance(2000.0F);
        this.setUnlocalizedName("teamChest");
    }
    
    @Override
    public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider((EntityPlayer) par5EntityLiving, TileEntityTeamHub.class);
        
        if (teamHub != null) {
            TileEntityTeamChest teamChest = (TileEntityTeamChest) par1World.getBlockTileEntity(x, y, z);
            teamHub.getExtensionList().add(teamChest);
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World par1World) {
        return new TileEntityTeamChest();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("wood");
    }
}
