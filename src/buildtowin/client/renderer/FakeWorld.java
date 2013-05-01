package buildtowin.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import buildtowin.tileentity.TileEntityBlueprint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FakeWorld implements IBlockAccess {
    
    public IBlockAccess realWorld;
    
    @Override
    public int getBlockId(int x, int y, int z) {
        TileEntity tileEntity = this.getBlockTileEntity(x, y, z);
        
        if (tileEntity instanceof TileEntityBlueprint) {
            return ((TileEntityBlueprint) tileEntity).getSavedId();
        }
        
        return this.realWorld.getBlockId(x, y, z);
    }
    
    @Override
    public TileEntity getBlockTileEntity(int i, int j, int k) {
        return this.realWorld.getBlockTileEntity(i, j, k);
    }
    
    @Override
    public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l) {
        return this.realWorld.getLightBrightnessForSkyBlocks(i, j, k, l);
    }
    
    @Override
    public int getBlockMetadata(int x, int y, int z) {
        TileEntity tileEntity = this.getBlockTileEntity(x, y, z);
        
        if (tileEntity instanceof TileEntityBlueprint) {
            return ((TileEntityBlueprint) tileEntity).getSavedMetadata();
        }
        
        return this.realWorld.getBlockMetadata(x, y, z);
    }
    
    @Override
    public float getBrightness(int i, int j, int k, int l) {
        return this.realWorld.getBrightness(i, j, k, l);
    }
    
    @Override
    public float getLightBrightness(int i, int j, int k) {
        return this.realWorld.getLightBrightness(i, j, k);
    }
    
    @Override
    public Material getBlockMaterial(int i, int j, int k) {
        return this.realWorld.getBlockMaterial(i, j, k);
    }
    
    @Override
    public boolean isBlockOpaqueCube(int i, int j, int k) {
        Block block = Block.blocksList[this.getBlockId(i, j, k)];
        return block == null ? false : block.isOpaqueCube();
    }
    
    @Override
    public boolean isBlockNormalCube(int i, int j, int k) {
        return this.realWorld.isBlockNormalCube(i, j, k);
    }
    
    @Override
    public boolean isAirBlock(int i, int j, int k) {
        return this.realWorld.isAirBlock(i, j, k);
    }
    
    @Override
    public BiomeGenBase getBiomeGenForCoords(int i, int j) {
        return this.realWorld.getBiomeGenForCoords(i, j);
    }
    
    @Override
    public int getHeight() {
        return this.realWorld.getHeight();
    }
    
    @Override
    public boolean extendedLevelsInChunkCache() {
        return this.realWorld.extendedLevelsInChunkCache();
    }
    
    @Override
    public boolean doesBlockHaveSolidTopSurface(int i, int j, int k) {
        return this.realWorld.doesBlockHaveSolidTopSurface(i, j, k);
    }
    
    @Override
    public Vec3Pool getWorldVec3Pool() {
        return this.realWorld.getWorldVec3Pool();
    }
    
    @Override
    public int isBlockProvidingPowerTo(int i, int j, int k, int l) {
        return this.realWorld.isBlockProvidingPowerTo(i, j, k, l);
    }
}
