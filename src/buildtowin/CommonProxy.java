package buildtowin;

import java.io.File;

import buildtowin.blueprint.BlueprintList;

public class CommonProxy {
    
    public void init() {
        BlueprintList.serverInstance.init(new File(".", "blueprints"));
    }
}
