package buildtowin.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.lwjgl.opengl.GL11;

import buildtowin.network.PacketIds;
import buildtowin.tileentity.TileEntityGameHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenGameHub extends GuiScreen {
    
    private TileEntityGameHub gameHub;
    
    private long plannedTimespan = 0;
    
    private boolean isPlayerCreative = false;
    
    public GuiScreenGameHub(TileEntityGameHub gameHub, boolean isPlayerCreative) {
        this.gameHub = gameHub;
        
        if (gameHub.getDeadline() == 0) {
            this.plannedTimespan = gameHub.getPlannedTimespan();
        } else {
            this.plannedTimespan = gameHub.getDeadline() - gameHub.getRealWorldTime();
        }
        
        this.isPlayerCreative = isPlayerCreative;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        
        GuiButton decreaseTimespan = new GuiButton(1, this.width / 2 - 36, this.height / 2 - 27, 20, 20, "-");
        decreaseTimespan.enabled = this.isPlayerCreative && this.plannedTimespan >= 24000;
        this.buttonList.add(decreaseTimespan);
        
        GuiButton increaseTimespan = new GuiButton(2, this.width / 2 + 15, this.height / 2 - 27, 20, 20, "+");
        increaseTimespan.enabled = this.isPlayerCreative;
        this.buttonList.add(increaseTimespan);
        
        if (this.gameHub.getDeadline() == 0) {
            GuiButton start = new GuiButton(3, this.width / 2 - 45, this.height / 2 + 30, 90, 20, "Start");
            start.enabled = this.plannedTimespan >= 24000;
            this.buttonList.add(start);
        } else {
            GuiButton stop = new GuiButton(4, this.width / 2 - 45, this.height / 2 + 30, 90, 20, "Stop");
            this.buttonList.add(stop);
        }
        
        GuiButton load = new GuiButton(5, this.width / 2 - 45, this.height / 2 + 5, 90, 20, "Load");
        load.enabled = this.isPlayerCreative && this.gameHub.getDeadline() == 0;
        this.buttonList.add(load);
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.plannedTimespan -= 24000;
            this.initGui();
            this.sendTimespanPacket();
        } else if (par1GuiButton.id == 2) {
            this.plannedTimespan += 24000;
            this.initGui();
            this.sendTimespanPacket();
        } else if (par1GuiButton.id == 3) {
            this.sendStartPacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 4) {
            this.sendStopPacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 5) {
            this.mc.displayGuiScreen(new GuiScreenBlueprintLoad(this.gameHub, this));
        }
    }
    
    private void sendTimespanPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.GAMEHUB_TIMESPAN_UPDATE);
            
            dataoutputstream.writeInt(this.gameHub.xCoord);
            dataoutputstream.writeInt(this.gameHub.yCoord);
            dataoutputstream.writeInt(this.gameHub.zCoord);
            
            dataoutputstream.writeLong(this.plannedTimespan);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void sendStartPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.GAMEHUB_START);
            
            dataoutputstream.writeInt(this.gameHub.xCoord);
            dataoutputstream.writeInt(this.gameHub.yCoord);
            dataoutputstream.writeInt(this.gameHub.zCoord);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void sendStopPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.GAMEHUB_STOP);
            
            dataoutputstream.writeInt(this.gameHub.xCoord);
            dataoutputstream.writeInt(this.gameHub.yCoord);
            dataoutputstream.writeInt(this.gameHub.zCoord);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/mods/buildtowin/textures/gui/gamehub.png");
        
        int x = (this.width - 170) / 2;
        int y = (this.height - 150) / 2 - 32;
        this.drawTexturedModalRect(x, y, 0, 0, 170, 200);
        
        this.fontRenderer.drawString("Game Hub", this.width / 2 - 10, this.height / 2 - 62, 4210752);
        
        this.fontRenderer.drawString("Days left:", (this.width - this.fontRenderer.getStringWidth("Days left:")) / 2, this.height / 2 - 40, 4210752);
        
        String daysLeft = String.format("%.2f", this.plannedTimespan / 24000D);
        this.fontRenderer.drawString(daysLeft, (this.width - this.fontRenderer.getStringWidth(daysLeft)) / 2, this.height / 2 - 21, 4210752);
        
        super.drawScreen(par1, par2, par3);
    }
}
