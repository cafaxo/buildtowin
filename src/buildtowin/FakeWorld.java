package buildtowin;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class FakeWorld implements IBlockAccess {
    
    private IBlockAccess world;
    
    private BlockData fakeBlockDataList[];
    
    public FakeWorld(IBlockAccess world) {
        this.world = world;
    }
    
    public void overrideSurroundingBlueprints(int x, int y, int z) {
        fakeBlockDataList = new BlockData[5];
        
        fakeBlockDataList[0] = BuildToWin.getBlueprint().getBlockData(world, x, y, z);
        fakeBlockDataList[1] = BuildToWin.getBlueprint().getBlockData(world, x - 1, y, z);
        fakeBlockDataList[2] = BuildToWin.getBlueprint().getBlockData(world, x, y, z - 1);
        fakeBlockDataList[3] = BuildToWin.getBlueprint().getBlockData(world, x + 1, y, z);
        fakeBlockDataList[4] = BuildToWin.getBlueprint().getBlockData(world, x, y, z + 1);
    }
    
    @Override
    public int getBlockId(int x, int y, int z) {
        if (this.fakeBlockDataList != null) {
            for (BlockData fakeBlockData : this.fakeBlockDataList) {
                if (fakeBlockData != null) {
                    if (fakeBlockData.x == x && fakeBlockData.y == y && fakeBlockData.z == z) {
                        return fakeBlockData.id;
                    }
                }
            }
        }
        
        return world.getBlockId(x, y, z);
    }
    
    @Override
    public TileEntity getBlockTileEntity(int i, int j, int k) {
        return world.getBlockTileEntity(i, j, k);
    }
    
    @Override
    public int getLightBrightnessForSkyBlocks(int i, int j, int k, int l) {
        return world.getLightBrightnessForSkyBlocks(i, j, k, l);
    }
    
    @Override
    public int getBlockMetadata(int x, int y, int z) {
        if (this.fakeBlockDataList != null) {
            for (BlockData fakeBlockData : this.fakeBlockDataList) {
                if (fakeBlockData != null) {
                    if (fakeBlockData.x == x && fakeBlockData.y == y && fakeBlockData.z == z) {
                        return fakeBlockData.metadata;
                    }
                }
            }
        }
        
        return world.getBlockMetadata(x, y, z);
    }
    
    @Override
    public float getBrightness(int i, int j, int k, int l) {
        return world.getBrightness(i, j, k, l);
    }
    
    @Override
    public float getLightBrightness(int i, int j, int k) {
        return world.getLightBrightness(i, j, k);
    }
    
    @Override
    public Material getBlockMaterial(int i, int j, int k) {
        return world.getBlockMaterial(i, j, k);
    }
    
    @Override
    public boolean isBlockOpaqueCube(int i, int j, int k) {
        return world.isBlockOpaqueCube(i, j, k);
    }
    
    @Override
    public boolean isBlockNormalCube(int i, int j, int k) {
        return world.isBlockNormalCube(i, j, k);
    }
    
    @Override
    public boolean isAirBlock(int i, int j, int k) {
        return world.isAirBlock(i, j, k);
    }
    
    @Override
    public BiomeGenBase getBiomeGenForCoords(int i, int j) {
        return world.getBiomeGenForCoords(i, j);
    }
    
    @Override
    public int getHeight() {
        return world.getHeight();
    }
    
    @Override
    public boolean extendedLevelsInChunkCache() {
        return world.extendedLevelsInChunkCache();
    }
    
    @Override
    public boolean doesBlockHaveSolidTopSurface(int i, int j, int k) {
        return world.doesBlockHaveSolidTopSurface(i, j, k);
    }
    
    @Override
    public Vec3Pool getWorldVec3Pool() {
        return world.getWorldVec3Pool();
    }
    
    @Override
    public int isBlockProvidingPowerTo(int i, int j, int k, int l) {
        return world.isBlockProvidingPowerTo(i, j, k, l);
    }
    
}
