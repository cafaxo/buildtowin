package buildtowin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenBuildingSettings extends GuiScreen {
    private TileEntityBuildingController buildingController;
    
    private long plannedTimespan = 0;
    
    private boolean isPlayerCreative = false;
    
    GuiTextField saveFileName;
    
    public GuiScreenBuildingSettings(TileEntityBuildingController buildingController, boolean isPlayerCreative) {
        this.buildingController = buildingController;
        
        if (buildingController.getDeadline() == 0) {
            this.plannedTimespan = buildingController.getPlannedTimespan();
        } else {
            this.plannedTimespan = buildingController.getDeadline() - buildingController.worldObj.getTotalWorldTime();
        }
        
        this.isPlayerCreative = isPlayerCreative;
    }
    
    @Override
    public void initGui() {
        GuiButton decreaseTimespan = new GuiButton(1, this.width / 2 - 36, this.height / 2 - 27, 20, 20, "-");
        decreaseTimespan.enabled = this.isPlayerCreative;
        this.buttonList.add(decreaseTimespan);
        
        GuiButton increaseTimespan = new GuiButton(2, this.width / 2 + 15, this.height / 2 - 27, 20, 20, "+");
        increaseTimespan.enabled = this.isPlayerCreative;
        this.buttonList.add(increaseTimespan);
        
        if (this.buildingController.getDeadline() == 0) {
            GuiButton start = new GuiButton(3, this.width / 2 - 45, this.height / 2 + 5, 90, 20, "Start");
            this.buttonList.add(start);
        } else {
            GuiButton stop = new GuiButton(4, this.width / 2 - 45, this.height / 2 + 5, 90, 20, "Stop");
            stop.enabled = this.isPlayerCreative;
            this.buttonList.add(stop);
        }
        
        GuiButton load = new GuiButton(5, this.width / 2 - 45, this.height / 2 + 35, 35, 20, "Load");
        this.buttonList.add(load);
        
        GuiButton save = new GuiButton(6, this.width / 2 + 12, this.height / 2 + 35, 35, 20, "Save");
        this.buttonList.add(save);
        
        saveFileName = new GuiTextField(this.fontRenderer, this.width / 2 - 45, this.height / 2 + 65, 90, 15);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            if (this.plannedTimespan > 24000) {
                this.plannedTimespan -= 24000;
                this.sendTimespanPacket();
            }
        } else if (par1GuiButton.id == 2) {
            this.plannedTimespan += 24000;
            this.sendTimespanPacket();
        } else if (par1GuiButton.id == 3) {
            this.sendStartPacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 4) {
            this.sendStopPacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 5) {
            this.mc.displayGuiScreen(new GuiScreenBlueprintLoad(this));
        } else if (par1GuiButton.id == 6) {
            this.sendSavePacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        }
    }
    
    @Override
    public void updateScreen() {
        this.saveFileName.updateCursorCounter();
    }
    
    protected void keyTyped(char par1, int par2) {
        this.saveFileName.textboxKeyTyped(par1, par2);
    }
    
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
        this.saveFileName.mouseClicked(par1, par2, par3);
    }
    
    private void sendSavePacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.buildingController.xCoord);
            dataoutputstream.writeInt(this.buildingController.yCoord);
            dataoutputstream.writeInt(this.buildingController.zCoord);
            dataoutputstream.writeUTF(this.saveFileName.getText());
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btwbpsav", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void sendTimespanPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.buildingController.xCoord);
            dataoutputstream.writeInt(this.buildingController.yCoord);
            dataoutputstream.writeInt(this.buildingController.zCoord);
            dataoutputstream.writeLong(this.plannedTimespan);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btwtimsupdt", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void sendStartPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.buildingController.xCoord);
            dataoutputstream.writeInt(this.buildingController.yCoord);
            dataoutputstream.writeInt(this.buildingController.zCoord);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btwstart", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void sendStopPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(this.buildingController.xCoord);
            dataoutputstream.writeInt(this.buildingController.yCoord);
            dataoutputstream.writeInt(this.buildingController.zCoord);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btwstop", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/btw_settings.png");
        
        int x = (width - 170) / 2;
        int y = (height - 150) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 150);
        
        this.fontRenderer.drawString("Settings", this.width / 2 - 10, height / 2 - 62, 4210752);
        
        this.fontRenderer.drawString("Days left:", (this.width - this.fontRenderer.getStringWidth("Days left:")) / 2, height / 2 - 40, 4210752);
        
        String daysLeft = String.format("%.2f", this.plannedTimespan / 24000D);
        this.fontRenderer.drawString(daysLeft, (this.width - this.fontRenderer.getStringWidth(daysLeft)) / 2, height / 2 - 21, 4210752);
        
        this.saveFileName.drawTextBox();
        
        super.drawScreen(par1, par2, par3);
    }
    
    public TileEntityBuildingController getBuildingController() {
        return buildingController;
    }
}