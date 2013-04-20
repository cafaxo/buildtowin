package buildtowin.util;

import net.minecraft.entity.player.EntityPlayer;

public interface IPlayerListProvider {
    
    public abstract PlayerList getPlayerList();
    
    public abstract boolean isValid();
    
    public abstract void onPlayerConnected(EntityPlayer entityPlayer);
    
    public abstract void onPlayerDisconnect(EntityPlayer entityPlayer);
}
