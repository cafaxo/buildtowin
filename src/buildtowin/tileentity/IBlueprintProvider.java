package buildtowin.tileentity;

import buildtowin.blueprint.Blueprint;
import buildtowin.util.Color;

public interface IBlueprintProvider {
    
    public void loadBlueprint(Blueprint blueprint);
    
    public Color getColor();
}
