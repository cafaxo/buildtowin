package buildtowin.blueprint;

import java.util.Arrays;

public class BlockCoordinates {
    
    public int[] coordinates;
    
    BlockCoordinates(int x, int y, int z) {
        this.coordinates = new int[] { x, y, z };
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
