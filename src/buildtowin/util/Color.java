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
    
    public Color() {
        this.r = 1.0F;
        this.g = 1.0F;
        this.b = 1.0F;
        this.id = 0;
    }
    
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
    
    public static Color fromId(int id) {
        Color color = new Color();
        
        if (id < Color.niceColors.length) {
            Color niceColor = Color.niceColors[id];
            
            color.r = niceColor.r;
            color.g = niceColor.g;
            color.b = niceColor.b;
        } else {
            Random rand = new Random(id);
            ArrayList<Float> values = new ArrayList<Float>();
            values.add(rand.nextFloat());
            values.add(rand.nextFloat());
            values.add(1.7F - (values.get(0) + values.get(1)));
            
            Collections.shuffle(values, rand);
            
            color.r = values.get(0);
            color.g = values.get(1);
            color.b = values.get(2);
        }
        
        color.id = id;
        
        return color;
    }
    
    public int toDecimal() {
        return ((int) (this.r * 255.F) << 16) + ((int) (this.g * 255.F) << 8) + (int) (this.b * 255.F);
    }
}
