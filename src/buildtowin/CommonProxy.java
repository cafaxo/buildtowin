package buildtowin;

public class CommonProxy {
    
    public void init() {
        BuildToWin.blueprintListServer = new BlueprintList(".");
        BuildToWin.blueprintListServer.read();
    }
}
