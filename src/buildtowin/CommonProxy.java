package buildtowin;

import buildtowin.blueprint.BlueprintList;

public class CommonProxy {
    
    public void init() {
        if (BlueprintList.blueprintListServer == null) {
            BlueprintList.blueprintListServer = new BlueprintList(".");
            BlueprintList.blueprintListServer.read();
        }
    }
}
