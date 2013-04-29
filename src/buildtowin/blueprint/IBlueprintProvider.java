package buildtowin.blueprint;

import buildtowin.util.Color;

public interface IBlueprintProvider {
    
    public abstract void loadBlueprint(Blueprint blueprint);
    
    public abstract Color getColor();
}
