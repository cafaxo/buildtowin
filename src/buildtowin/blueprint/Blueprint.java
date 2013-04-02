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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import buildtowin.BuildToWin;
import buildtowin.tileentity.TileEntityBlueprint;

public class Blueprint {
    
    private String name;
    
    private ArrayList<String> authors;
    
    private HashMap<BlockCoordinates, BlockData> blocks;
    
    private World worldObj;
    
    private int offsetX, offsetY, offsetZ;
    
    private byte color;
    
    private static final Map<Integer, Integer> blockToItemId = new HashMap<Integer, Integer>();
    
    static {
        blockToItemId.put(Block.bed.blockID, Item.bed.itemID);
        blockToItemId.put(Block.doorWood.blockID, Item.doorWood.itemID);
        blockToItemId.put(Block.doorSteel.blockID, Item.doorSteel.itemID);
    }
    
    public Blueprint() {
        this.blocks = new HashMap<BlockCoordinates, BlockData>();
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.color = (byte) 0;
    }
    
    public Blueprint(Blueprint blueprint) {
        this.blocks = new HashMap<BlockCoordinates, BlockData>();
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;
        this.color = (byte) 0;
        
        Iterator iter = this.blocks.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            BlockCoordinates blockCoordinates = (BlockCoordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            this.blocks.put(new BlockCoordinates(blockCoordinates), new BlockData(blockData));
        }
    }
    
    public void setOffset(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }
    
    public void setBlockData(int x, int y, int z, BlockData blockData) {
        this.blocks.put(new BlockCoordinates(x - this.offsetX, y - this.offsetY, z - this.offsetZ), blockData);
    }
    
    public BlockData getBlockData(int x, int y, int z) {
        return this.blocks.get(new BlockCoordinates(x - this.offsetX, y - this.offsetY, z - this.offsetZ));
    }
    
    public void removeBlockData(int x, int y, int z) {
        this.blocks.remove(new BlockCoordinates(x - this.offsetX, y - this.offsetY, z - this.offsetZ));
    }
    
    public void refreshBlueprint(int x, int y, int z, BlockData blockData) {
        this.worldObj.setBlock(x, y, z, BuildToWin.blueprint.blockID, 0, 2);
        this.worldObj.markBlockForUpdate(x, y, z);
        
        TileEntityBlueprint blueprint = (TileEntityBlueprint) this.worldObj.getBlockTileEntity(x, y, z);
        
        blueprint.setBlockData(blockData);
        blueprint.setColor(this.color);
        
        if (this.getBlockData(x, y, z) == null) {
            this.setBlockData(x, y, z, blockData);
        }
    }
    
    public void placeBlueprint(int x, int y, int z, BlockData blockData) {
        if (blockData.id != BuildToWin.buildingHub.blockID && blockData.id != BuildToWin.blueprint.blockID) {
            if (blockData.id == Block.doorWood.blockID || blockData.id == Block.doorSteel.blockID) {
                this.placeBlueprintDoor(x, y, z, blockData);
            } else if (blockData.id == Block.bed.blockID) {
                this.placeBlueprintBed(x, y, z, blockData);
            } else {
                this.worldObj.setBlock(x, y, z, Block.obsidian.blockID, 0, 3);
                this.refreshBlueprint(x, y, z, blockData);
            }
        }
    }
    
    public void loadBlueprint(HashMap<BlockCoordinates, BlockData> blocks) {
        Iterator iter = blocks.entrySet().iterator();
        int finishedBlocks = 0;
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            BlockCoordinates blockCoordinates = (BlockCoordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            this.blocks.put(blockCoordinates, blockData);
        }
    }
    
    private void placeBlueprintDoor(int x, int y, int z, BlockData blockData) {
        if (this.worldObj.getBlockId(x, y + 1, z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(x, y + 1, z);
            BlockData overCurrentData = new BlockData(blockData.id, metadata);
            
            this.refreshBlueprint(x, y + 1, z, overCurrentData);
        } else if (this.worldObj.getBlockId(x, y - 1, z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(x, y - 1, z);
            BlockData underCurrentData = new BlockData(blockData.id, metadata);
            
            this.refreshBlueprint(x, y - 1, z, underCurrentData);
        }
        
        this.refreshBlueprint(x, y, z, blockData);
    }
    
    private void placeBlueprintBed(int x, int y, int z, BlockData blockData) {
        if (this.worldObj.getBlockId(x + 1, y, z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(x + 1, y, z);
            BlockData blockDataSecondPart = new BlockData(blockData.id, metadata);
            
            this.refreshBlueprint(x + 1, y, z, blockDataSecondPart);
        } else if (this.worldObj.getBlockId(x, y, z + 1) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(x, y, z + 1);
            BlockData blockDataSecondPart = new BlockData(blockData.id, metadata);
            
            this.refreshBlueprint(x, y, z + 1, blockDataSecondPart);
        } else if (this.worldObj.getBlockId(x - 1, y, z) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(x - 1, y, z);
            BlockData blockDataSecondPart = new BlockData(blockData.id, metadata);
            
            this.refreshBlueprint(x - 1, y, z, blockDataSecondPart);
        } else if (this.worldObj.getBlockId(x, y, z - 1) == blockData.id) {
            int metadata = this.worldObj.getBlockMetadata(x, y, z - 1);
            BlockData blockDataSecondPart = new BlockData(blockData.id, metadata);
            
            this.refreshBlueprint(x, y, z - 1, blockDataSecondPart);
        }
        
        this.refreshBlueprint(x, y, z, blockData);
    }
    
    public void removeBlueprint(int x, int y, int z, boolean removeFromList) {
        BlockData blockData = this.getBlockData(x, y, z);
        
        if (blockData != null) {
            if (blockData.id == Block.doorWood.blockID || blockData.id == Block.doorSteel.blockID) {
                this.removeBlueprintDoor(x, y, z, blockData, removeFromList);
            } else if (blockData.id == Block.bed.blockID) {
                this.removeBlueprintBed(x, y, z, blockData, removeFromList);
            } else {
                this.removeBlueprintStandard(x, y, z, blockData, removeFromList);
            }
        }
    }
    
    public void removeBlueprintStandard(int x, int y, int z, BlockData blockData, boolean removeFromList) {
        this.worldObj.setBlock(x, y, z, blockData.id, blockData.metadata, 3);
        
        if (removeFromList) {
            this.removeBlockData(x, y, z);
        }
    }
    
    public void removeBlueprintDoor(int x, int y, int z, BlockData firstPart, boolean removeFromList) {
        BlockData secondPart = null;
        
        if ((secondPart = this.getBlockData(x, y + 1, z)) != null && secondPart.id == firstPart.id) {
            this.removeBlueprintStandard(x, y + 1, z, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x, y - 1, z)) != null && secondPart.id == firstPart.id) {
            this.removeBlueprintStandard(x, y - 1, z, secondPart, removeFromList);
        }
        
        this.removeBlueprintStandard(x, y, z, firstPart, removeFromList);
    }
    
    private void removeBlueprintBed(int x, int y, int z, BlockData firstPart, boolean removeFromList) {
        BlockData secondPart = null;
        
        if ((secondPart = this.getBlockData(x + 1, y, z)) != null && secondPart.id == firstPart.id) {
            this.removeBlueprintStandard(x + 1, y, z, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x, y, z + 1)) != null && secondPart.id == firstPart.id) {
            this.removeBlueprintStandard(x, y, z + 1, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x - 1, y, z)) != null && secondPart.id == firstPart.id) {
            this.removeBlueprintStandard(x - 1, y, z, secondPart, removeFromList);
        } else if ((secondPart = this.getBlockData(x, y, z - 1)) != null && secondPart.id == firstPart.id) {
            this.removeBlueprintStandard(x, y, z - 1, secondPart, removeFromList);
        }
        
        this.removeBlueprintStandard(x, y, z, firstPart, removeFromList);
    }
    
    public void clear() {
        Iterator iter = this.blocks.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            BlockCoordinates blockCoordinates = (BlockCoordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            this.removeBlueprint(
                    this.offsetX + blockCoordinates.coordinates[0],
                    this.offsetY + blockCoordinates.coordinates[1],
                    this.offsetZ + blockCoordinates.coordinates[2],
                    false);
        }
        
        this.blocks.clear();
    }
    
    public int refresh() {
        Iterator iter = this.blocks.entrySet().iterator();
        int finishedBlocks = 0;
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            BlockCoordinates blockCoordinates = (BlockCoordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            int realBlockId = this.worldObj.getBlockId(
                    this.offsetX + blockCoordinates.coordinates[0],
                    this.offsetY + blockCoordinates.coordinates[1],
                    this.offsetZ + blockCoordinates.coordinates[2]);
            
            if (realBlockId == blockData.id) {
                ++finishedBlocks;
            } else if (realBlockId != BuildToWin.blueprint.blockID) {
                this.refreshBlueprint(
                        this.offsetX + blockCoordinates.coordinates[0],
                        this.offsetY + blockCoordinates.coordinates[1],
                        this.offsetZ + blockCoordinates.coordinates[2],
                        blockData);
            }
        }
        
        return finishedBlocks;
    }
    
    public void reset() {
        Iterator iter = this.blocks.entrySet().iterator();
        
        while (iter.hasNext()) {
            Map.Entry pairs = (Map.Entry) iter.next();
            BlockCoordinates blockCoordinates = (BlockCoordinates) pairs.getKey();
            BlockData blockData = (BlockData) pairs.getValue();
            
            this.refreshBlueprint(
                    this.offsetX + blockCoordinates.coordinates[0],
                    this.offsetY + blockCoordinates.coordinates[1],
                    this.offsetZ + blockCoordinates.coordinates[2],
                    blockData);
        }
    }
    
    public int[] encode() {
        int data[] = new int[this.blocks.size() * 5];
        
        Iterator iter = this.blocks.entrySet().iterator();
        int finishedBlocks = 0;
        
        for (int i = 0; iter.hasNext(); ++i) {
            Map.Entry pairs = (Map.Entry) iter.next();
            BlockCoordinates blockCoordinates = (BlockCoordinates) pairs.getKey();
            
            data[i * 5] = blockCoordinates.coordinates[0];
            data[i * 5 + 1] = blockCoordinates.coordinates[1];
            data[i * 5 + 2] = blockCoordinates.coordinates[2];
            
            BlockData blockData = (BlockData) pairs.getValue();
            
            data[i * 5 + 3] = blockData.id;
            data[i * 5 + 4] = blockData.metadata;
        }
        
        return data;
    }
    
    public void decode(int data[]) {
        for (int i = 0; i < data.length / 5; ++i) {
            BlockCoordinates blockCoordinates = new BlockCoordinates(data[i * 5], data[i * 5 + 1], data[i * 5 + 2]);
            BlockData blockData = new BlockData(data[i * 5 + 3], data[i * 5 + 4]);
            
            this.blocks.put(blockCoordinates, blockData);
        }
    }
    
    public static Blueprint fromBlueprintFile(File file) {
        Blueprint blueprint = new Blueprint();
        FileInputStream fileInputStream;
        
        try {
            fileInputStream = new FileInputStream(file);
            
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
            
            blueprint.name = dataInputStream.readUTF();
            
            int authorCount = dataInputStream.readInt();
            
            ArrayList<String> authors = new ArrayList<String>();
            
            for (int i = 0; i < authorCount; ++i) {
                authors.add(dataInputStream.readUTF());
            }
            
            blueprint.setAuthors(authors);
            
            int data[] = new int[dataInputStream.readInt()];
            
            for (int i = 0; i < data.length; ++i) {
                data[i] = dataInputStream.readInt();
            }
            
            blueprint.decode(data);
            
            fileInputStream.close();
            
            return blueprint;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static Blueprint fromSchematicFile(File file) {
        Blueprint blueprint = new Blueprint();
        NBTTagCompound nbt;
        
        try {
            nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
            
            blueprint.name = file.getName();
            
            int width = nbt.getShort("Width");
            int height = nbt.getShort("Height");
            int length = nbt.getShort("Length");
            
            byte[] blockIds = nbt.getByteArray("Blocks");
            byte[] blockMetadata = nbt.getByteArray("Data");
            
            int index = 0;
            
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++) {
                        if (blockIds[index] != 0) {
                            BlockCoordinates blockCoordinates = new BlockCoordinates(x, y, z);
                            BlockData blockData = new BlockData(blockIds[index], blockMetadata[index]);
                            
                            blueprint.blocks.put(blockCoordinates, blockData);
                        }
                        
                        ++index;
                    }
                }
            }
            
            return blueprint;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
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
            
            for (int i = 0; i < data.length; ++i) {
                dataOutputStream.writeInt(data[i]);
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
    
    public void setWorldObj(World worldObj) {
        this.worldObj = worldObj;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ArrayList<String> getAuthors() {
        return authors;
    }
    
    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }
    
    public HashMap<BlockCoordinates, BlockData> getBlocks() {
        return blocks;
    }
    
    public byte getColor() {
        return color;
    }
    
    public void setColor(byte color) {
        this.color = color;
    }
    
    public void select(int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxZ = Math.max(z1, z2);
        
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    int blockId = this.worldObj.getBlockId(x, y, z);
                    
                    if (blockId != 0 && blockId != BuildToWin.buildingHub.blockID) {
                        if (blockId == BuildToWin.blueprint.blockID) {
                            this.removeBlueprint(x, y, z, true);
                        } else {
                            this.placeBlueprint(x, y, z, new BlockData(blockId, this.worldObj.getBlockMetadata(x, y, z)));
                        }
                    }
                }
            }
        }
    }
    
    public int getItemId(int blockId) {
        Integer itemId = this.blockToItemId.get(blockId);
        
        if (itemId != null) {
            return itemId;
        }
        
        return blockId;
    }
}
