package buildtowin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.client.gui.GuiScreenPenalizer;
import buildtowin.client.renderer.IColoredBlock;
import buildtowin.tileentity.TileEntityPenalizer;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Color;
import buildtowin.util.PlayerList;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPenalizer extends BlockContainer implements IColoredBlock {
    
    private Icon blockIconStandard;
    
    private Icon blockIconOverlay;
    
    public BlockPenalizer(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setHardness(50.0F);
        this.setResistance(2000.0F);
        this.setUnlocalizedName("penalizer");
    }
    
    @Override
    public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider((EntityPlayer) par5EntityLiving, TileEntityTeamHub.class);
        
        if (teamHub != null) {
            TileEntityPenalizer penalizer = (TileEntityPenalizer) par1World.getBlockTileEntity(x, y, z);
            penalizer.setTeamHub(teamHub);
            teamHub.getExtensionList().add(penalizer);
            
            par1World.markBlockForRenderUpdate(penalizer.xCoord, penalizer.yCoord, penalizer.zCoord);
        }
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        if (par1World.isRemote) {
            TileEntityPenalizer penalizer = (TileEntityPenalizer) par1World.getBlockTileEntity(x, y, z);
            this.displayPenalizer(penalizer);
        }
        
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void displayPenalizer(TileEntityPenalizer penalizer) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.displayGuiScreen(new GuiScreenPenalizer(penalizer));
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityPenalizer();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:penalizer");
        this.blockIconStandard = par1IconRegister.registerIcon("buildtowin:penalizer");
        this.blockIconOverlay = par1IconRegister.registerIcon("buildtowin:penalizer_overlay");
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
        TileEntityPenalizer tileEntity = (TileEntityPenalizer) world.getBlockTileEntity(x, y, z);
        
        if (tileEntity.getTeamHub() != null) {
            return tileEntity.getTeamHub().getColor();
        } else {
            return new Color(1.0F, 1.0F, 1.0F);
        }
    }
}
