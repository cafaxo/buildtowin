package buildtowin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityShop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockShop extends BlockContainer {
    
    public BlockShop(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("shop");
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        par5EntityPlayer.openGui(BuildToWin.instance, 0, par1World, x, y, z);
        return true;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityShop();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:shop");
    }
}
