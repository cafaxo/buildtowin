package buildtowin.block;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.blueprint.BlockData;
import buildtowin.tileentity.TileEntityBlueprint;
import buildtowin.tileentity.TileEntityTeamHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBlueprint extends BlockContainer {
    
    public BlockBlueprint(int id) {
        super(id, Material.glass);
        
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
    }
    
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer entityPlayer) {
        if (!world.isRemote) {
            TileEntityTeamHub teamHub = TileEntityTeamHub.getTeamHub(entityPlayer);
            
            if (teamHub == null) {
                BuildToWin.sendChatMessage(entityPlayer, "Please connect to a Team Hub.");
                return;
            }
            
            if (teamHub.getGameHub().getDeadline() == 0) {
                BuildToWin.sendChatMessage(entityPlayer, "The game has not started yet.");
                return;
            }
            
            BlockData blockData = teamHub.getBlueprint().getBlockData(x, y, z);
            
            if (blockData == null) {
                BuildToWin.sendChatMessage(entityPlayer, "This blueprint does not belong to your Team Hub.");
                return;
            }
            
            if (entityPlayer.inventory.getCurrentItem() != null) {
                if (entityPlayer.inventory.getCurrentItem().itemID == teamHub.getBlueprint().getItemId(blockData.id)) {
                    teamHub.getBlueprint().removeBlueprint(x, y, z, false);
                    entityPlayer.inventory.consumeInventoryItem(blockData.id);
                }
            }
        }
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public int getRenderType() {
        return BuildToWin.blueprintRenderId;
    }
    
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    
    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return 0;
    }
    
    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return 0;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBlueprint();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
    }
}
