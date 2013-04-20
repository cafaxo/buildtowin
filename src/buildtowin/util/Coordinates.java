package buildtowin.util;

import java.util.Arrays;
import java.util.Random;

public class Coordinates {
    
    public int x;
    
    public int y;
    
    public int z;
    
    public Coordinates(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Coordinates(Coordinates coordinates) {
        this.x = coordinates.x;
        this.y = coordinates.y;
        this.z = coordinates.z;
    }
    
    public static Coordinates getRandomCoordinatesInBounds(Coordinates boundMin, Coordinates boundMax) {
        Random rand = new Random();
        
        int randX = boundMin.x + (int) (rand.nextFloat() * (float) (boundMax.x - boundMin.x));
        int randY = boundMin.y + (int) (rand.nextFloat() * (float) (boundMax.y - boundMin.y));
        int randZ = boundMin.z + (int) (rand.nextFloat() * (float) (boundMax.z - boundMin.z));
        
        return new Coordinates(randX, randY, randZ);
    }
    
    public int[] getCoordinates() {
        return new int[] { this.x, this.y, this.z };
    }
    
    @Override
    public boolean equals(Object coordinates) {
        if (coordinates instanceof Coordinates) {
            return Arrays.equals(this.getCoordinates(), ((Coordinates) coordinates).getCoordinates());
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getCoordinates());
    }
}
