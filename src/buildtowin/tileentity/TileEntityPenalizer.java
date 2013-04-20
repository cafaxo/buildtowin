package buildtowin.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.SpawnerAnimals;
import buildtowin.network.PacketIds;
import buildtowin.util.Coordinates;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TileEntityPenalizer extends TileEntity {
    
    private TileEntityTeamHub teamHub;
    
    private int type;
    
    private int strength;
    
    private int timer;
    
    public TileEntityPenalizer() {
        this.type = 0;
        this.strength = 0;
        this.timer = 0;
    }
    
    public void sendPenalizePacket(int type, int strength) {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PENALIZER_PENALIZE);
            
            dataoutputstream.writeInt(this.xCoord);
            dataoutputstream.writeInt(this.yCoord);
            dataoutputstream.writeInt(this.zCoord);
            
            dataoutputstream.writeInt(type);
            dataoutputstream.writeInt(strength);
            
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    public void onPenalizePacket(DataInputStream dataInputStream) throws IOException {
        int type = dataInputStream.readInt();
        int strength = dataInputStream.readInt();
        
        if (this.teamHub.getEnergy() < 20) {
            return;
        }
        
        this.teamHub.setEnergy(this.teamHub.getEnergy() - 20);
        
        ArrayList<TileEntityTeamHub> teamHubs = this.teamHub.getGameHub().getConnectedTeamHubs();
        
        for (TileEntityTeamHub teamHub : teamHubs) {
            if (teamHub != this.teamHub) {
                TileEntityPenalizer penalizer = teamHub.getPenalizer();
                
                penalizer.type = type;
                penalizer.strength = strength;
                penalizer.penalize();
            }
        }
    }
    
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.timer > 0) {
                if (this.timer % (50 - this.strength * 10) == 0) {
                    this.penalize();
                }
                
                --this.timer;
            }
        }
    }
    
    private void penalize() {
        switch (this.type) {
        case 0:
            this.penalizeWithLightning();
            break;
        case 1:
            this.penalizeWithMonsters();
            break;
        case 2:
            this.penalizeWithPoison();
            break;
        }
    }
    
    private void penalizeWithPoison() {
        Random rand = new Random();
        
        for (String player : this.teamHub.getPlayerList().getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = this.worldObj.getPlayerEntityByName(player)) != null) {
                entityPlayer.addPotionEffect(new PotionEffect(Potion.poison.getId(), 100, 0));
            }
        }
    }
    
    private void penalizeWithLightning() {
        if (this.timer == 0) {
            this.timer = this.strength * 400;
        }
        
        Coordinates randCoords = this.teamHub.getBlueprint().getRandomBlueprint();
        
        this.worldObj.addWeatherEffect(new EntityLightningBolt(
                this.worldObj,
                (double) randCoords.x,
                (double) randCoords.y,
                (double) randCoords.z));
        
        for (int x = 0; x < 4; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = 0; z < 4; ++z) {
                    if (this.teamHub.getBlueprint().getBlockData(randCoords.x + x - 2, randCoords.y + y - 2, randCoords.z + z - 2) != null) {
                        this.worldObj.destroyBlock(randCoords.x + x - 2, randCoords.y + y - 2, randCoords.z + z - 2, false);
                    }
                }
            }
        }
    }
    
    private void penalizeWithMonsters() {
        Random rand = new Random();
        
        for (String player : this.teamHub.getPlayerList().getConnectedPlayers()) {
            EntityPlayer entityPlayer = null;
            
            if ((entityPlayer = this.worldObj.getPlayerEntityByName(player)) != null) {
                
                int spawnX = (int) (entityPlayer.posX + rand.nextInt(12) - 6);
                int spawnY = (int) Math.round(entityPlayer.posY);
                int spawnZ = (int) (entityPlayer.posZ + rand.nextInt(12) - 6);
                
                if (SpawnerAnimals.canCreatureTypeSpawnAtLocation(
                        EnumCreatureType.monster,
                        this.worldObj,
                        spawnX,
                        spawnY,
                        spawnZ)) {
                    
                    EntityMob randomMob = null;
                    
                    switch (rand.nextInt(3)) {
                    case 0:
                        EntityZombie zombie = new EntityZombie(this.worldObj);
                        zombie.initCreature();
                        zombie.setVillager(false);
                        randomMob = zombie;
                        break;
                    case 1:
                        EntityCreeper creeper = new EntityCreeper(this.worldObj);
                        creeper.initCreature();
                        randomMob = creeper;
                        break;
                    case 2:
                        EntitySkeleton skeleton = new EntitySkeleton(this.worldObj);
                        skeleton.initCreature();
                        randomMob = skeleton;
                        break;
                    }
                    
                    randomMob.setLocationAndAngles(spawnX, spawnY, spawnZ, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
                    this.worldObj.spawnEntityInWorld(randomMob);
                }
            }
        }
    }
    
    public TileEntityTeamHub getTeamHub() {
        return teamHub;
    }
    
    public void setTeamHub(TileEntityTeamHub teamHub) {
        this.teamHub = teamHub;
    }
}
