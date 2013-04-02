package buildtowin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.client.gui.GuiScreenGameHub;
import buildtowin.tileentity.TileEntityGameHub;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGameHub extends BlockContainer {
    
    public BlockGameHub(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("Game Hub");
    }
    
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        if (par1World.isRemote) {
            TileEntityGameHub gameHub = (TileEntityGameHub) par1World.getBlockTileEntity(x, y, z);
            this.displayGameHub(gameHub, par5EntityPlayer);
        }
        
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void displayGameHub(TileEntityGameHub gameHub, EntityPlayer player) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.displayGuiScreen(new GuiScreenGameHub(gameHub, player.capabilities.isCreativeMode));
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityGameHub();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:gamehub");
    }
}
