package buildtowin.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Color {
    
    private static final Color niceColors[] = {
            new Color(1.0F, 1.0F, 1.0F),
            new Color(0.3F, 0.3F, 1.0F),
            new Color(1.0F, 0.3F, 0.3F),
            new Color(0.3F, 1.0F, 0.3F)
    };
    
    public int id;
    
    public float r, g, b;
    
    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.id = -1;
    }
    
    public Color(Color color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.id = -1;
    }
    
    public void setFromId(int id) {
        if (id < this.niceColors.length) {
            Color niceColor = Color.niceColors[id];
            
            this.r = niceColor.r;
            this.g = niceColor.g;
            this.b = niceColor.b;
        } else {
            Random rand = new Random(id);
            ArrayList<Float> values = new ArrayList<Float>();
            values.add(rand.nextFloat());
            values.add(rand.nextFloat());
            values.add(1.7F - (values.get(0) + values.get(1)));
            
            Collections.shuffle(values, rand);
            
            this.r = values.get(0);
            this.g = values.get(1);
            this.b = values.get(2);
        }
        
        this.id = id;
    }
}
