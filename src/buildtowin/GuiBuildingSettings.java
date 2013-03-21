package buildtowin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.StatCollector;

public class GuiBuildingSettings extends GuiScreen {
    private TileEntityBuildingController buildingController;
    
    private int deadline = 0;
    
    public GuiBuildingSettings(TileEntityBuildingController buildingController) {
        this.buildingController = buildingController;
        this.deadline = buildingController.getDeadline();
    }
    
    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 31, this.height / 2 - 13, 20, 20, "-"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 10, this.height / 2 - 13, 20, 20, "+"));
    }
    
    @Override
    public void onGuiClosed() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.buildingController.xCoord);
            dataoutputstream.writeInt(this.buildingController.yCoord);
            dataoutputstream.writeInt(this.buildingController.zCoord);
            dataoutputstream.writeInt(this.deadline);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btwdeadlnupdt", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            if (this.deadline > 0) {
                --this.deadline;
            }
        } else if (par1GuiButton.id == 2) {
            ++this.deadline;
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/buildinfo.png");
        
        int x = (width - 138) / 2;
        int y = (height - 64) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 138, 100);
        
        this.fontRenderer.drawString("Building Settings", (this.width - this.fontRenderer.getStringWidth("Building Settings")) / 2, height / 2 - 45, 4210752);
        
        this.fontRenderer.drawString("Days left:", (this.width - this.fontRenderer.getStringWidth("Days left:")) / 2, height / 2 - 27, 4210752);
        
        this.fontRenderer.drawString(((Integer) this.deadline).toString(), (this.width - this.fontRenderer.getStringWidth(((Integer) this.deadline).toString())) / 2, height / 2 - 7, 4210752);
        
        super.drawScreen(par1, par2, par3);
    }
}