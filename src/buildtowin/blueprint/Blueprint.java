package buildtowin.blueprint;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityBlueprint;
import buildtowin.util.Coordinates;

public class Blueprint {
    
    private String name;
    
    private ArrayList<String> authors = new ArrayList<String>();
    
    private HashMap<Coordinates, BlockData> blocks = new HashMap<Coordinates, BlockData>();
    
    private TileEntity blueprintProvider;
    
    public Blueprint(TileEntity blueprintProvider) {
        this.blueprintProvider = blueprintProvider;
    }
    
    public Blueprint(TileEntity blueprintProvider, Blueprint blueprint) {
        this.blueprintProvider = blueprintProvider;
        this.loadBlockData(blueprint.getBlocks());
    }
    
    public void setBlockData(int x, int y, int z, BlockData blockData) {
        this.blueprintProvider.getWorldObj().setBlock(x, y, z, BuildToWin.blueprint.blockID, 0, 2);
        this.blueprintProvider.getWorldObj().markBlockForUpdate(x, y, z);
        
        TileEntityBlueprint blueprint = (TileEntityBlueprint) this.blueprintProvider.getWorldObj().getBlockTileEntity(x, y, z);
        
        blueprint.setBlockData(blockData);
        blueprint.setColor(((IBlueprintProvider) this.blueprintProvider).getColor());
        
        this.blocks.put(new Coordinates(x - this.blueprintProvider.xCoord, y - this.blueprintProvider.yCoord, z - this.blueprintProvider.zCoord), blockData);
    }
    
    public BlockData getBlockData(int x, int y, int z) {
        return this.blocks.get(new Coordinates(x - this.blueprintProvider.xCoord, y - this.blueprintProvider.yCoord, z - this.blueprintProvider.zCoord));
    }
    
    public void removeBlockData(int x, int y, int z) {
        this.blocks.remove(new Coordinates(x - this.blueprintProvider.xCoord, y - this.blueprintProvider.yCoord, z - this.blueprintProvider.zCoord));
    }
    
    public void placeBlueprint(int x, int y, int z, BlockData blockData) {
        if (blockData.savedId != BuildToWin.buildingHub.blockID && blockData.savedId != BuildToWin.blueprint.blockID) {
            if (blockData.savedId == Block.doorWood.blockID || blockData.savedId == Block.doorIron.blockID) {
                this.placeBlueprintDoor(x, y, z, blockData);
            } else if (blockData.savedId == Block.bed.blockID) {
                this.placeBlueprintBed(x, y, z, blockData);
            } else {
                this.setBlockData(x, y, z, blockData);
            }
        }
    }
    
    private void placeBlueprintDoor(int x, int y, int z, BlockData blockData) {
        if (this.blueprintProvider.getWorldObj().getBlockId(x, y + 1, z) == blockData.savedId) {
            int metadata = this.blueprintProvider.getWorldObj().getBlockMetadata(x, y + 1, z);
            BlockData overCurrentData = new BlockData(blockData.savedId, metadata);
            
            this.setBlockData(x, y + 1, z, overCurrentData);
        } else if (this.blueprintProvider.getWorldObj().getBlockId(x, y - 1, z) == blockData.savedId) {
            int metadata = this.blueprintProvider.getWorldObj().getBlockMetadata(x, y - 1, z);
            BlockData underCurrentData = new BlockData(blockData.savedId, metadata);
            
            this.setBlockData(x, y - 1, z, underCurrentData);
        }
        
        this.setBlockData(x, y, z, blockData);
    }
    
    private void placeBlueprintBed(int x, int y, int z, BlockData blockData) {
        if (this.blueprintProvider.getWorldObj().getBlockId(x + 1, y, z) == blockData.savedId) {
            int metadata = this.blueprintProvider.getWorldObj().getBlockMetadata(x + 1, y, z);
            BlockData blockDataSecondPart = new BlockData(blockData.savedId, metadata);
            
            this.setBlockData(x + 1, y, z, blockDataSecondPart);
        } else if (this.blueprintProvider.getWorldObj().getBlockId(x, y, z + 1) == blockData.savedId) {
            int metadata = this.blueprintProvider.getWorldObj().getBlockMetadata(x, y, z + 1);
            BlockData blockDataSecondPart = new BlockData(blockData.savedId, metadata);
            
            this.setBlockData(x, y, z + 1, blockDataSecondPart);
        } else if (this.blueprintProvider.getWorldObj().getBlockId(x - 1, y, z) == blockData.savedId) {
            int metadata = this.blueprintProvider.getWorldObj().getBlockMetadata(x - 1, y, z);
            BlockData blockDataSecondPart = new BlockData(blockData.savedId, metadata);
            
            this.setBlockData(x - 1, y, z, blockDataSecondPart);
        } else if (this.blueprintProvider.getWorldObj().getBlockId(x, y, z - 1) == blockData.savedId) {
            int metadata = this.blueprintProvider.getWorldObj().getBlockMetadata(x, y, z - 1);
            BlockData blockDataSecondPart = new BlockData(blockData.savedId, metadata);
            
            this.setBlockData(x, y, z - 1, blockDataSecondPart);
        }
        
        this.setBlockData(x, y, z, blockData);
    }
    
    public void removeBlueprint(int x, int y, int z, boolean removeFromList) {
        BlockData blockData = this.getBlockData(x, y, z);
        
        if (blockData != null) {
            if (blockData.savedId == Block.doorWood.blockID || blockData.savedId == Block.doorIron.blockID) {
                this.removeBlueprintDoor(x, y, z, blockData, removeFromList);
            } else if (blockData.savedId == Block.bed.blockID) {
                this.removeBlueprintBed(x, y, z, blockData, removeFromList);
            } else {
                this.removeBlueprintStandard(x, y, z, blockData, removeFromList);
            }
        }
    }
    
    public void removeBlueprintStandard(int x, int y, int z, BlockData blockData, boolean removeFromList) {
        this.blueprintProvider.getWorldObj().setBlock(x, y, z, blockData.savedId, blockData.savedMetadata, 3);
        
        if (removeFromList) {
            this.removeBlockData(x, y, z);
        }
    }
    
    public void removeBlueprintDoor(int x, int y, int z, BlockData firstPart, boolean removeFromList) {
        BlockData secondPart = null;
        
        if ((secondPart = this.getBlockData(x, y + 1, z)) != null && secondPart.savedId == firstPart.savedId) {
            this.removeBlueprintStandard(x, y + 1, z, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x, y - 1, z)) != null && secondPart.savedId == firstPart.savedId) {
            this.removeBlueprintStandard(x, y - 1, z, secondPart, removeFromList);
        }
        
        this.removeBlueprintStandard(x, y, z, firstPart, removeFromList);
    }
    
    private void removeBlueprintBed(int x, int y, int z, BlockData firstPart, boolean removeFromList) {
        BlockData secondPart = null;
        
        if ((secondPart = this.getBlockData(x + 1, y, z)) != null && secondPart.savedId == firstPart.savedId) {
            this.removeBlueprintStandard(x + 1, y, z, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x, y, z + 1)) != null && secondPart.savedId == firstPart.savedId) {
            this.removeBlueprintStandard(x, y, z + 1, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x - 1, y, z)) != null && secondPart.savedId == firstPart.savedId) {
            this.removeBlueprintStandard(x - 1, y, z, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x, y, z - 1)) != null && secondPart.savedId == firstPart.savedId) {
            this.removeBlueprintStandard(x, y, z - 1, secondPart, removeFromList);
        }
        
        this.removeBlueprintStandard(x, y, z, firstPart, removeFromList);
    }
    
    public void select(int x1, int y1, int z1, int x2, int y2, int z2, boolean mode) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    int blockId = this.blueprintProvider.getWorldObj().getBlockId(x, y, z);
                    
                    if (blockId != 0 && blockId != BuildToWin.buildingHub.blockID) {
                        if (mode) {
                            if (blockId != BuildToWin.blueprint.blockID) {
                                this.placeBlueprint(x, y, z, new BlockData(blockId, this.blueprintProvider.getWorldObj().getBlockMetadata(x, y, z)));
                            }
                        } else {
                            if (blockId == BuildToWin.blueprint.blockID) {
                                this.removeBlueprint(x, y, z, true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void clear() {
        Iterator iter = this.blocks.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            Coordinates blockCoordinates = (Coordinates) pairs.getKey();
            
            this.blueprintProvider.getWorldObj().setBlockToAir(
                    this.blueprintProvider.xCoord + blockCoordinates.x,
                    this.blueprintProvider.yCoord + blockCoordinates.y,
                    this.blueprintProvider.zCoord + blockCoordinates.z);
        }
    }
    
    public int refresh() {
        Iterator iter = this.blocks.entrySet().iterator();
        int finishedBlocks = 0;
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            Coordinates blockCoordinates = (Coordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            int realBlockId = this.blueprintProvider.getWorldObj().getBlockId(
                    this.blueprintProvider.xCoord + blockCoordinates.x,
                    this.blueprintProvider.yCoord + blockCoordinates.y,
                    this.blueprintProvider.zCoord + blockCoordinates.z);
            
            if (realBlockId == blockData.savedId
                    || Block.blocksList[realBlockId] != null && Block.blocksList[realBlockId].idDropped(0, new Random(), 0) == blockData.savedId) {
                ++finishedBlocks;
            } else if (realBlockId != BuildToWin.blueprint.blockID) {
                this.setBlockData(
                        this.blueprintProvider.xCoord + blockCoordinates.x,
                        this.blueprintProvider.yCoord + blockCoordinates.y,
                        this.blueprintProvider.zCoord + blockCoordinates.z,
                        blockData);
            }
        }
        
        return finishedBlocks;
    }
    
    public void reset() {
        Iterator iter = this.blocks.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            Coordinates blockCoordinates = (Coordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            blockData.metadata = 0;
            
            this.setBlockData(
                    this.blueprintProvider.xCoord + blockCoordinates.x,
                    this.blueprintProvider.yCoord + blockCoordinates.y,
                    this.blueprintProvider.zCoord + blockCoordinates.z,
                    blockData);
        }
    }
    
    public int[] encode() {
        int data[] = new int[this.blocks.size() * 6];
        
        Iterator iter = this.blocks.entrySet().iterator();
        for (int i = 0; iter.hasNext(); ++i) {
            Map.Entry pairs = (Map.Entry) iter.next();
            Coordinates blockCoordinates = (Coordinates) pairs.getKey();
            
            data[i * 6] = blockCoordinates.x;
            data[i * 6 + 1] = blockCoordinates.y;
            data[i * 6 + 2] = blockCoordinates.z;
            
            BlockData blockData = (BlockData) pairs.getValue();
            
            data[i * 6 + 3] = blockData.metadata;
            data[i * 6 + 4] = blockData.savedId;
            data[i * 6 + 5] = blockData.savedMetadata;
        }
        
        return data;
    }
    
    public void decode(int data[]) {
        for (int i = 0; i < data.length / 6; ++i) {
            Coordinates blockCoordinates = new Coordinates(data[i * 6], data[i * 6 + 1], data[i * 6 + 2]);
            BlockData blockData = new BlockData(data[i * 6 + 3], data[i * 6 + 4], data[i * 6 + 5]);
            
            this.blocks.put(blockCoordinates, blockData);
        }
    }
    
    public boolean loadBlueprintFile(File file) {
        FileInputStream fileInputStream;
        
        try {
            fileInputStream = new FileInputStream(file);
            
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            
            this.name = dataInputStream.readUTF();
            
            int authorCount = dataInputStream.readInt();
            
            for (int i = 0; i < authorCount; ++i) {
                this.authors.add(dataInputStream.readUTF());
            }
            
            int data[] = new int[dataInputStream.readInt()];
            
            for (int i = 0; i < data.length; ++i) {
                data[i] = dataInputStream.readInt();
            }
            
            this.decode(data);
            
            fileInputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean loadSchematicFile(File file) {
        NBTTagCompound nbt;
        
        try {
            nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
            
            this.name = file.getName();
            
            int width = nbt.getShort("Width");
            int height = nbt.getShort("Height");
            int length = nbt.getShort("Length");
            
            byte[] blockIds = nbt.getByteArray("Blocks");
            byte[] blockMetadata = nbt.getByteArray("Data");
            
            int index = 0;
            
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        if (blockIds[index] > 0) {
                            Coordinates blockCoordinates = new Coordinates(x, y, z);
                            BlockData blockData = new BlockData(blockIds[index], blockMetadata[index]);
                            
                            this.blocks.put(blockCoordinates, blockData);
                        }
                        
                        ++index;
                    }
                }
            }
            
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public void loadBlockData(HashMap<Coordinates, BlockData> blocks) {
        this.blocks.clear();
        
        Iterator iter = blocks.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            Coordinates blockCoordinates = (Coordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            this.blocks.put(new Coordinates(blockCoordinates), new BlockData(blockData));
        }
    }
    
    public boolean writeBlueprintFile(File file) {
        FileOutputStream fileOutputStream = null;
        
        try {
            fileOutputStream = new FileOutputStream(file);
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            
            dataOutputStream.writeUTF(this.name);
            
            dataOutputStream.writeInt(this.authors.size());
            
            for (String author : this.authors) {
                dataOutputStream.writeUTF(author);
            }
            
            int data[] = this.encode();
            
            dataOutputStream.writeInt(data.length);
            
            for (int element : data) {
                dataOutputStream.writeInt(element);
            }
            
            dataOutputStream.close();
            
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<String> getAuthors() {
        return this.authors;
    }
    
    public HashMap<Coordinates, BlockData> getBlocks() {
        return this.blocks;
    }
    
    public Coordinates getRandomCoordinatesOutside() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(this.blocks.keySet().size());
        int i = 0;
        
        for (Coordinates coords : this.blocks.keySet()) {
            if (i >= randomIndex) {
                Coordinates absoluteCoords = new Coordinates(coords.x + this.blueprintProvider.xCoord, coords.y + this.blueprintProvider.yCoord, coords.z + this.blueprintProvider.zCoord);
                
                if (this.blueprintProvider.getWorldObj().canBlockSeeTheSky(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z)) {
                    return absoluteCoords;
                }
            }
            
            ++i;
        }
        
        return this.getRandomCoordinatesOutside();
    }
    
    public Coordinates getNextUnfinishedBlueprint() {
        for (Coordinates coords : this.blocks.keySet()) {
            Coordinates absoluteCoords = new Coordinates(coords.x + this.blueprintProvider.xCoord, coords.y + this.blueprintProvider.yCoord, coords.z + this.blueprintProvider.zCoord);
            
            if (this.blueprintProvider.worldObj.getBlockId(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z) == BuildToWin.blueprint.blockID) {
                return absoluteCoords;
            }
        }
        
        return null;
    }
}
