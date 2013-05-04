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
import buildtowin.client.renderer.IColoredBlock;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Color;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTeamHub extends BlockContainer implements IColoredBlock {
    
    private Icon blockIconStandard;
    
    private Icon blockIconOverlay;
    
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
            teamHub.getPlayerList().disconnectPlayer(par5EntityPlayer);
        } else {
            teamHub.getPlayerList().connectPlayer(par5EntityPlayer);
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityTeamHub();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:teamhub");
        this.blockIconStandard = par1IconRegister.registerIcon("buildtowin:teamhub");
        this.blockIconOverlay = par1IconRegister.registerIcon("buildtowin:teamhub_overlay");
    }
    
    @Override
    public int getRenderType() {
        return BuildToWin.coloredBlockRenderId;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void switchIconToOverlay() {
        this.blockIcon = this.blockIconOverlay;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void switchIconToStandard() {
        this.blockIcon = this.blockIconStandard;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Color getColor(IBlockAccess world, int x, int y, int z) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) world.getBlockTileEntity(x, y, z);
        
        if (teamHub.getPlayerList().isPlayerConnected(Minecraft.getMinecraft().thePlayer)) {
            return teamHub.getColor();
        } else {
            return new Color(teamHub.getColor().r * 0.5F, teamHub.getColor().g * 0.5F, teamHub.getColor().b * 0.5F);
        }
    }
}
