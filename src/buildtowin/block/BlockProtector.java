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
import buildtowin.client.gui.GuiScreenProtector;
import buildtowin.client.renderer.IColoredBlock;
import buildtowin.tileentity.TileEntityProtector;
import buildtowin.tileentity.TileEntityTeamHub;
import buildtowin.util.Color;
import buildtowin.util.PlayerList;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockProtector extends BlockContainer implements IColoredBlock {
    
    private Icon blockIconStandard;
    
    private Icon blockIconOverlay;
    
    public BlockProtector(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("protector");
    }
    
    public void onBlockPlacedBy(World par1World, int x, int y, int z, EntityLiving par5EntityLiving, ItemStack par6ItemStack) {
        TileEntityTeamHub teamHub = (TileEntityTeamHub) PlayerList.getPlayerListProvider((EntityPlayer) par5EntityLiving, TileEntityTeamHub.class);
        
        if (teamHub != null) {
            TileEntityProtector protector = (TileEntityProtector) par1World.getBlockTileEntity(x, y, z);
            protector.setTeamHub(teamHub);
        }
    }
    
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        if (par1World.isRemote) {
            TileEntityProtector protector = (TileEntityProtector) par1World.getBlockTileEntity(x, y, z);
            this.displayProtector(protector, par5EntityPlayer);
        }
        
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void displayProtector(TileEntityProtector protector, EntityPlayer entityPlayer) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.displayGuiScreen(new GuiScreenProtector(protector, entityPlayer.capabilities.isCreativeMode));
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityProtector();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:protector");
        this.blockIconStandard = par1IconRegister.registerIcon("buildtowin:protector");
        this.blockIconOverlay = par1IconRegister.registerIcon("buildtowin:protector_overlay");
    }
    
    @Override
    public int getRenderType() {
        return BuildToWin.coloredBlockRenderId;
    }
    
    @Override
    public void switchIconToOverlay() {
        this.blockIcon = this.blockIconOverlay;
    }
    
    @Override
    public void switchIconToStandard() {
        this.blockIcon = this.blockIconStandard;
    }
    
    @Override
    public Color getColor(IBlockAccess world, int x, int y, int z) {
        TileEntityProtector tileEntity = (TileEntityProtector) world.getBlockTileEntity(x, y, z);
        
        if (tileEntity.getTeamHub() != null) {
            return tileEntity.getTeamHub().getColor();
        } else {
            return new Color(1.0F, 1.0F, 1.0F);
        }
    }
}
