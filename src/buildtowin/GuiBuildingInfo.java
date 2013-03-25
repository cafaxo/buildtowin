package buildtowin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBuildingInfo extends Gui {
    private Minecraft theGame;
    
    private float ySpeed;
    
    private float yPosition;
    
    private int progress;
    
    private String daysLeft;
    
    public GuiBuildingInfo(Minecraft par1Minecraft) {
        this.theGame = par1Minecraft;
        this.yPosition = -32;
        this.ySpeed = 0;
        this.progress = 0;
        this.daysLeft = "";
    }
    
    public void tick() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        
        if (player != null) {
            TileEntityBuildingController buildingController = BuildToWin.getBuildingController().getTileEntity(player);
            if (buildingController != null && buildingController.getDeadline() != 0) {
                if (this.yPosition < 0) {
                    this.yPosition += this.ySpeed;
                    this.ySpeed += 0.1F;
                } else {
                    this.yPosition = 0;
                    this.ySpeed = 0;
                }
                
                this.progress = 100;
                
                if (buildingController.getBlockDataList().size() != 0) {
                    this.progress = 100 * buildingController.getFinishedBlocks() / buildingController.getBlockDataList().size();
                }
                
                this.daysLeft = String.format("%.2f", ((buildingController.getDeadline() - player.worldObj.getTotalWorldTime()) / 24000.F));
            } else {
                if (this.yPosition > -32) {
                    this.yPosition -= this.ySpeed;
                    this.ySpeed += 0.1F;
                    this.progress = 100;
                } else {
                    this.yPosition = -32;
                    this.ySpeed = 0;
                }
            }
            
            if (this.yPosition != -32) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                this.theGame.renderEngine.bindTexture("/achievement/bg.png");
                
                this.drawTexturedModalRect(0, Math.round(yPosition), 96, 202, 160, 32);
                this.drawTexturedModalRect(45, Math.round(yPosition), 96 + 30, 202, 160 - 30, 32);
                this.theGame.fontRenderer.drawStringWithShadow("Days left: " + this.daysLeft, 10, Math.round(this.yPosition) + 12, 0xffffff);
                this.theGame.fontRenderer.drawStringWithShadow("Progress: " + this.progress + "%", 90, Math.round(this.yPosition) + 12, 0xffffff);
            }
        }
        
    }
}
