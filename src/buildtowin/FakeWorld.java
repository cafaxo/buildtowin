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
        this.fakeBlockDataList = new BlockData[7];
        
        this.fakeBlockDataList[0] = BuildToWin.getBlueprint().getBlockData(this.world, x, y, z);
        this.fakeBlockDataList[1] = BuildToWin.getBlueprint().getBlockData(this.world, x - 1, y, z);
        this.fakeBlockDataList[2] = BuildToWin.getBlueprint().getBlockData(this.world, x, y, z - 1);
        this.fakeBlockDataList[3] = BuildToWin.getBlueprint().getBlockData(this.world, x + 1, y, z);
        this.fakeBlockDataList[4] = BuildToWin.getBlueprint().getBlockData(this.world, x, y, z + 1);
        this.fakeBlockDataList[5] = BuildToWin.getBlueprint().getBlockData(this.world, x, y + 1, z);
        this.fakeBlockDataList[6] = BuildToWin.getBlueprint().getBlockData(this.world, x, y - 1, z);
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
        if (this.fakeBlockDataList != null) {
            for (BlockData fakeBlockData : this.fakeBlockDataList) {
                if (fakeBlockData != null) {
                    if (fakeBlockData.x == x && fakeBlockData.y == y && fakeBlockData.z == z) {
                        return fakeBlockData.metadata;
                    }
                }
            }
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
