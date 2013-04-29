package buildtowin.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import buildtowin.BuildToWin;
import buildtowin.client.renderer.texture.TextureBlueprintDetector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlueprintDetector extends Item {
    
    public ItemBlueprintDetector(int itemId) {
        super(itemId);
        
        this.setMaxStackSize(1);
        this.setCreativeTab(BuildToWin.tabBuildToWin);
        this.setUnlocalizedName("blueprintDetector");
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        TextureBlueprintDetector blueprintDetectorTexture = new TextureBlueprintDetector();
        Minecraft.getMinecraft().renderEngine.textureMapItems.setTextureEntry("buildtowin:detector", blueprintDetectorTexture);
        
        this.itemIcon = blueprintDetectorTexture;
    }
}
