package buildtowin.blueprint;

import java.util.Arrays;

public class BlockCoordinates {
    
    public int[] coordinates;
    
    BlockCoordinates(int x, int y, int z) {
        this.coordinates = new int[] { x, y, z };
    }
    
    public BlockCoordinates(BlockCoordinates blockCoordinates) {
        this.coordinates = new int[] { blockCoordinates.coordinates[0], blockCoordinates.coordinates[1], blockCoordinates.coordinates[2] };
    }
    
    @Override
    public boolean equals(Object blockCoordinates) {
        if (blockCoordinates instanceof BlockCoordinates) {
            return Arrays.equals(this.coordinates, ((BlockCoordinates) blockCoordinates).coordinates);
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.coordinates);
    }
}
