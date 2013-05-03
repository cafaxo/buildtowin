package buildtowin.tileentity;

import net.minecraftforge.common.ForgeChunkManager.Ticket;

public interface ITicketProvider {
    
    public Ticket getTicket();
    
    void setTicket(Ticket ticket);
}
