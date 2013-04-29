package buildtowin.blueprint;

import net.minecraft.block.Block;

public class BlockData {
    
    public int metadata, savedId, savedMetadata;
    
    public BlockData(int savedId, int savedMetadata) {
        this.savedId = savedId;
        this.savedMetadata = savedMetadata;
    }
    
    public BlockData(int metadata, int savedId, int savedMetadata) {
        this.metadata = metadata;
        this.savedId = savedId;
        this.savedMetadata = savedMetadata;
    }
    
    public BlockData(BlockData blockData) {
        this.metadata = blockData.metadata;
        this.savedId = blockData.savedId;
        this.savedMetadata = blockData.savedMetadata;
    }
    
    public Block getSavedBlock() {
        if (this.savedId > 0 && this.savedId < Block.blocksList.length) {
            if (Block.blocksList[this.savedId] != null) {
                return Block.blocksList[this.savedId];
            }
        }
        
        return null;
    }
}
