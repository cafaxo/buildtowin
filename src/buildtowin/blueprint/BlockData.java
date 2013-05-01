package buildtowin.blueprint;

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
}
