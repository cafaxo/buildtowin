package buildtowin.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import buildtowin.blueprint.BlueprintList;
import buildtowin.network.PacketIds;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenBlueprintLoad extends GuiScreen {
    
    private TileEntity sender;
    
    private GuiScreen guiScreenParent;
    
    private GuiBlueprintSlot blueprintSlot;
    
    private int selectedBlueprint;
    
    public GuiScreenBlueprintLoad(TileEntity sender, GuiScreen guiScreenParent) {
        this.sender = sender;
        this.guiScreenParent = guiScreenParent;
        this.selectedBlueprint = 0;
    }
    
    @Override
    public void initGui() {
        this.blueprintSlot = new GuiBlueprintSlot(this, this.mc, this.width, this.height, 45, this.height - 45, 32);
        this.blueprintSlot.registerScrollButtons(this.buttonList, 7, 8);
        
        GuiButton load = new GuiButton(1, this.width / 2 - 45 - 50, this.height - 35, 90, 20, "Load");
        load.enabled = BlueprintList.blueprintListClient.getBlueprintList().size() > 0;
        this.buttonList.add(load);
        
        this.buttonList.add(new GuiButton(2, this.width / 2 - 45 + 50, this.height - 35, 90, 20, "Cancel"));
    }
    
    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1) {
            this.sendBlueprintLoadPacket();
            this.mc.displayGuiScreen((GuiScreen) null);
        } else if (par1GuiButton.id == 2) {
            this.mc.displayGuiScreen(this.guiScreenParent);
        } else {
            this.blueprintSlot.actionPerformed(par1GuiButton);
        }
    }
    
    private void sendBlueprintLoadPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.BLUEPRINT_LOAD);
            
            dataoutputstream.writeInt(this.sender.xCoord);
            dataoutputstream.writeInt(this.sender.yCoord);
            dataoutputstream.writeInt(this.sender.zCoord);
            
            dataoutputstream.writeInt(this.selectedBlueprint);
            
            this.mc.getNetHandler().addToSendQueue(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    @Override
    public void drawScreen(int par1, int par2, float par3) {
        this.blueprintSlot.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRenderer, "Select Blueprint", this.width / 2, 30, 16777215);
        super.drawScreen(par1, par2, par3);
    }
    
    public int getSelectedBlueprint() {
        return this.selectedBlueprint;
    }
    
    public void setSelectedBlueprint(int selectedBlueprint) {
        this.selectedBlueprint = selectedBlueprint;
    }
}
