package buildtowin;

import java.io.File;

import buildtowin.blueprint.BlueprintList;
import buildtowin.util.PriceList;

public class CommonProxy {
    
    public void init() {
        BlueprintList.serverInstance.init(new File(".", "blueprints"));
        
        PriceList.serverInstance.readFile(new File("btw_pricelist.txt"));
    }
}
