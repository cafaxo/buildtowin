package buildtowin;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

public class FakeWorld implements IBlockAccess {
    
    private IBlockAccess world;
    
    private ArrayList<BlockData> fakeBlockDataList = new ArrayList<BlockData>();
    
    public FakeWorld(IBlockAccess world) {
        this.world = world;
    }
    
    public void overrideBlockIdAndMetadata(int x, int y, int z, int id, int metadata) {
        this.fakeBlockDataList.add(new BlockData(x, y, z, id, metadata));
    }
    
    public void overrideBlueprint(int x, int y, int z) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        
        if (te != null) {
            if (te instanceof TileEntityBlueprint) {
                TileEntityBlueprint blueprint = (TileEntityBlueprint) te;
                this.fakeBlockDataList.add(new BlockData(x, y, z, blueprint.getBlockId(), blueprint.getBlockMetadata()));
            }
        }
    }
    
    public void resetOverriddenData() {
        this.fakeBlockDataList.clear();
    }
    
    @Override
    public int getBlockId(int x, int y, int z) {
        for (BlockData fakeBlockData : this.fakeBlockDataList) {
            if (fakeBlockData.x == x && fakeBlockData.y == y && fakeBlockData.z == z) {
                return fakeBlockData.id;
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
        for (BlockData fakeBlockData : this.fakeBlockDataList) {
            if (fakeBlockData.x == x && fakeBlockData.y == y && fakeBlockData.z == z) {
                return fakeBlockData.metadata;
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
