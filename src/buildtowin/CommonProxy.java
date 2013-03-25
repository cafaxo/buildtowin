package buildtowin;

public class CommonProxy {
    public void init() {
        BuildToWin.serverBlueprintList = new BlueprintList(".");
        BuildToWin.serverBlueprintList.read();
    }
}
