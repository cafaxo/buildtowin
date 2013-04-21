package buildtowin.client.renderer;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import buildtowin.blueprint.BlockData;
import buildtowin.tileentity.TileEntityBlueprint;

public class FakeWorld implements IBlockAccess {
    
    private IBlockAccess world;
    
    public FakeWorld(IBlockAccess world) {
        this.world = world;
    }
    
    @Override
    public int getBlockId(int x, int y, int z) {
        BlockData blockData = TileEntityBlueprint.getBlockData(x, y, z);
        
        if (blockData != null) {
            return blockData.savedId;
        }
        
        return this.world.getBlockId(x, y, z);
    }
    
    @Override
    public TileEntity getBlockTileEntity(int i, int j, int k) {
        return this.world.getBlockTileEntity(i, j, k);
    }
    
    @Override
    public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l) {
        return this.world.getLightBrightnessForSkyBlocks(i, j, k, l);
    }
    
    @Override
    public int getBlockMetadata(int x, int y, int z) {
        BlockData blockData = TileEntityBlueprint.getBlockData(x, y, z);
        
        if (blockData != null) {
            return blockData.savedMetadata;
        }
        
        return this.world.getBlockMetadata(x, y, z);
    }
    
    @Override
    public float getBrightness(int i, int j, int k, int l) {
        return this.world.getBrightness(i, j, k, l);
    }
    
    @Override
    public float getLightBrightness(int i, int j, int k) {
        return this.world.getLightBrightness(i, j, k);
    }
    
    @Override
    public Material getBlockMaterial(int i, int j, int k) {
        return this.world.getBlockMaterial(i, j, k);
    }
    
    @Override
    public boolean isBlockOpaqueCube(int i, int j, int k) {
        return this.world.isBlockOpaqueCube(i, j, k);
    }
    
    @Override
    public boolean isBlockNormalCube(int i, int j, int k) {
        return this.world.isBlockNormalCube(i, j, k);
    }
    
    @Override
    public boolean isAirBlock(int i, int j, int k) {
        return this.world.isAirBlock(i, j, k);
    }
    
    @Override
    public BiomeGenBase getBiomeGenForCoords(int i, int j) {
        return this.world.getBiomeGenForCoords(i, j);
    }
    
    @Override
    public int getHeight() {
        return this.world.getHeight();
    }
    
    @Override
    public boolean extendedLevelsInChunkCache() {
        return this.world.extendedLevelsInChunkCache();
    }
    
    @Override
    public boolean doesBlockHaveSolidTopSurface(int i, int j, int k) {
        return this.world.doesBlockHaveSolidTopSurface(i, j, k);
    }
    
    @Override
    public Vec3Pool getWorldVec3Pool() {
        return this.world.getWorldVec3Pool();
    }
    
    @Override
    public int isBlockProvidingPowerTo(int i, int j, int k, int l) {
        return this.world.isBlockProvidingPowerTo(i, j, k, l);
    }
}
