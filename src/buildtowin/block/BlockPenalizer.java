package buildtowin.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.client.gui.GuiScreenPenalizer;
import buildtowin.tileentity.TileEntityPenalizer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPenalizer extends BlockContainer {
    
    public BlockPenalizer(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("penalizer");
    }
    
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
    }
}
