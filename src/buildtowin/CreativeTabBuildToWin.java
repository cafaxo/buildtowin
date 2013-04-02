package buildtowin;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabBuildToWin extends CreativeTabs {
    
    public CreativeTabBuildToWin(String label) {
        super(label);
    }
    
    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(BuildToWin.buildingHub.blockID, 1, 0);
    }
}
