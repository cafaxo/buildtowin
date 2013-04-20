package buildtowin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityTeamHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTeamHub extends BlockContainer {
    
    private Icon iconDisconnected;
    
    public BlockTeamHub(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("teamHub");
    }
    
    @Override
    public void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) par5EntityPlayer.worldObj.getBlockTileEntity(par2, par3, par4);
        
        if (teamHub.getPlayerList().isPlayerConnected(par5EntityPlayer)) {
            teamHub.disconnectPlayer(par5EntityPlayer);
        } else {
            teamHub.connectPlayer(par5EntityPlayer);
        }
    }
    
    @Override
    public void onBlockHarvested(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) par1World.getBlockTileEntity(par2, par3, par4);
        teamHub.getBlueprint().clear();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) blockAccess.getBlockTileEntity(x, y, z);
        
        if (teamHub.getPlayerList().isPlayerConnected(Minecraft.getMinecraft().thePlayer)) {
            return this.blockIcon;
        } else {
            return this.iconDisconnected;
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityTeamHub();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:teamhub_connected");
        this.iconDisconnected = par1IconRegister.registerIcon("buildtowin:teamhub_disconnected");
    }
}
