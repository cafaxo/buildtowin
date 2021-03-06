package buildtowin.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import buildtowin.blueprint.BlueprintList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBlueprintSlot extends GuiSlot {
    
    private GuiScreenBlueprintLoad blueprintLoadGui;
    
    public GuiBlueprintSlot(GuiScreenBlueprintLoad guiScreenBlueprintLoad, Minecraft par1Minecraft, int par2, int par3, int par4, int par5, int par6) {
        super(par1Minecraft, par2, par3, par4, par5, par6);
        
        this.blueprintLoadGui = guiScreenBlueprintLoad;
    }
    
    @Override
    protected int getSize() {
        return BlueprintList.clientInstance.getBlueprintList().size();
    }
    
    @Override
    protected void elementClicked(int i, boolean flag) {
        this.blueprintLoadGui.setSelectedBlueprint(i);
        
    }
    
    @Override
    protected boolean isSelected(int i) {
        return this.blueprintLoadGui.getSelectedBlueprint() == i;
    }
    
    @Override
    protected void drawBackground() {
        this.blueprintLoadGui.drawDefaultBackground();
    }
    
    @Override
    protected void drawSlot(int i, int j, int k, int l, Tessellator tessellator) {
        this.blueprintLoadGui.drawString(Minecraft.getMinecraft().fontRenderer, BlueprintList.clientInstance.getBlueprintList().get(i).getName(), j + 2, k + 1, 16777215);
        this.blueprintLoadGui.drawString(Minecraft.getMinecraft().fontRenderer, BlueprintList.clientInstance.getBlueprintList().get(i).getAuthors().toString(), j + 2, k + 1 + 14, 8421504);
    }
}
