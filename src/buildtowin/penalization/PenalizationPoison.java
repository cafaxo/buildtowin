package buildtowin.penalization;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import buildtowin.tileentity.TileEntityTeamHub;

public class PenalizationPoison extends Penalization {
    
    public PenalizationPoison(int penalizationId) {
        super(penalizationId);
    }
    
    @Override
    public void penalize(TileEntityTeamHub teamHub) {
        Random rand = new Random();
        
        for (String player : teamHub.getPlayerList().getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = teamHub.worldObj.getPlayerEntityByName(player)) != null) {
                Potion randomPotion = null;
                
                while (randomPotion != null && randomPotion.isBadEffect()) {
                    randomPotion = Potion.potionTypes[rand.nextInt(32)];
                }
                
                entityPlayer.addPotionEffect(new PotionEffect(Potion.poison.getId(), 100, 0));
            }
        }
    }
    
    @Override
    public int getPrice(TileEntityTeamHub teamHub) {
        return teamHub.getPlayerList().getConnectedPlayers().size() * 80;
    }
}
