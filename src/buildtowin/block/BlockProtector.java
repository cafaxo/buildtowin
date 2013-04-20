package buildtowin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.client.gui.GuiScreenProtector;
import buildtowin.tileentity.TileEntityProtector;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockProtector extends BlockContainer {
    
    public BlockProtector(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("protector");
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
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:buildinghub_connected");
    }
}
