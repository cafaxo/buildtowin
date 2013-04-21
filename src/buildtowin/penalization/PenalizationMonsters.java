package buildtowin.penalization;

import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.SpawnerAnimals;
import buildtowin.tileentity.TileEntityTeamHub;

public class PenalizationMonsters extends Penalization {
    
    public PenalizationMonsters(int penalizationId) {
        super(penalizationId);
    }
    
    @Override
    public void penalize(TileEntityTeamHub teamHub, int strength) {
        Random rand = new Random();
        
        for (String player : teamHub.getPlayerList().getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = teamHub.worldObj.getPlayerEntityByName(player)) != null) {
                
                int spawnX = (int) (entityPlayer.posX + rand.nextInt(12) - 6);
                int spawnY = (int) Math.round(entityPlayer.posY);
                int spawnZ = (int) (entityPlayer.posZ + rand.nextInt(12) - 6);
                
                if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(
                        EnumCreatureType.monster,
                        teamHub.worldObj,
                        spawnX,
                        spawnY,
                        spawnZ)) {
                    
                    EntityMob randomMob = null;
                    
                    switch (rand.nextInt(3)) {
                    case 0:
                        EntityZombie zombie = new EntityZombie(teamHub.worldObj);
                        zombie.initCreature();
                        zombie.setVillager(false);
                        randomMob = zombie;
                        break;
                    case 1:
                        EntityCreeper creeper = new EntityCreeper(teamHub.worldObj);
                        creeper.initCreature();
                        randomMob = creeper;
                        break;
                    case 2:
                        EntitySkeleton skeleton = new EntitySkeleton(teamHub.worldObj);
                        skeleton.initCreature();
                        randomMob = skeleton;
                        break;
                    }
                    
                    randomMob.setLocationAndAngles(spawnX, spawnY, spawnZ, teamHub.worldObj.rand.nextFloat() * 360.0F, 0.0F);
                    teamHub.worldObj.spawnEntityInWorld(randomMob);
                }
            }
        }
    }
    
    @Override
    public int getPrice(TileEntityTeamHub teamHub, int strength) {
        return teamHub.getPlayerList().getConnectedPlayers().size() * 20;
    }
    
    @Override
    public int getRepetitions(TileEntityTeamHub teamHub, int strength) {
        return 3;
    }
}
