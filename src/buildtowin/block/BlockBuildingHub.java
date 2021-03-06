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
import buildtowin.client.gui.GuiScreenBuildingHub;
import buildtowin.tileentity.TileEntityBuildingHub;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBuildingHub extends BlockContainer {
    
    private Icon iconDisconnected;
    
    public BlockBuildingHub(int blockId) {
        super(blockId, Material.rock);
        
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setUnlocalizedName("buildingHub");
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
        if (par1World.isRemote) {
            TileEntityBuildingHub buildingHub = (TileEntityBuildingHub) par1World.getBlockTileEntity(x, y, z);
            this.displayBuildingHub(buildingHub, entityPlayer);
        }
        
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void displayBuildingHub(TileEntityBuildingHub buildingHub, EntityPlayer player) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        mc.displayGuiScreen(new GuiScreenBuildingHub(buildingHub));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        TileEntityBuildingHub buildingHub = (TileEntityBuildingHub) blockAccess.getBlockTileEntity(x, y, z);
        
        if (buildingHub.getPlayerList().isPlayerConnected(Minecraft.getMinecraft().thePlayer)) {
            return this.blockIcon;
        } else {
            return this.iconDisconnected;
        }
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBuildingHub();
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("buildtowin:buildinghub_connected");
        this.iconDisconnected = par1IconRegister.registerIcon("buildtowin:buildinghub_disconnected");
    }
}
