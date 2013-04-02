package buildtowin.blueprint;

public class BlockData {
    
    public int id, metadata;
    
    public BlockData(int id, int metadata) {
        this.id = id;
        this.metadata = metadata;
    }
    
    public BlockData(BlockData blockData) {
        this.id = blockData.id;
        this.metadata = blockData.metadata;
    }
}
