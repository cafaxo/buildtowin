package buildtowin.client.gui;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.opengl.GL11;

import buildtowin.ContainerShop;
import buildtowin.tileentity.TileEntityShop;
import buildtowin.util.PriceList;

public class GuiContainerShop extends GuiContainer {
    
    public GuiContainerShop(InventoryPlayer inventory, TileEntityShop shop) {
        super(new ContainerShop(inventory, shop));
        this.ySize = 114 + 3 * 18;
    }
    
    @Override
    protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3) {
        List list = par1ItemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);
        
        for (int k = 0; k < list.size(); ++k) {
            if (k == 0) {
                list.set(k, "\u00a7" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String) list.get(k));
            } else {
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
            }
        }
        
        Integer price = PriceList.clientInstance.getPrice(par1ItemStack.getItem()) * par1ItemStack.stackSize;
        
        if (price != 0) {
            list.add(EnumChatFormatting.AQUA + price.toString() + " coins");
        }
        
        this.func_102021_a(list, par2, par3);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/shop.png");
        
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        
        this.drawTexturedModalRect(x, y, 0, 0, this.xSize, 3 * 18 + 17);
        this.drawTexturedModalRect(x, y + 3 * 18 + 17, 0, 126, this.xSize, 96);
    }
}
