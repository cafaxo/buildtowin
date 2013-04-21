package buildtowin.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import buildtowin.network.PacketIds;

public class PriceList {
    
    public static PriceList serverInstance = new PriceList();
    
    public static PriceList clientInstance = new PriceList();
    
    private HashMap<Short, Short> priceMap;
    
    public static PriceList getInstance(World world) {
        if (world.isRemote) {
            return PriceList.clientInstance;
        } else {
            return PriceList.serverInstance;
        }
    }
    
    public PriceList() {
        this.priceMap = new HashMap<Short, Short>();
    }
    
    public Packet250CustomPayload getDescriptionPacket() {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        
        try {
            dataoutputstream.writeInt(PacketIds.PRICELIST_UPDATE);
            
            dataoutputstream.writeInt(this.priceMap.size() - 1);
            
            for (Map.Entry<Short, Short> entry : this.priceMap.entrySet()) {
                if (entry.getKey() != null) {
                    dataoutputstream.writeShort(Item.itemsList[entry.getKey()].itemID);
                    dataoutputstream.writeShort(entry.getValue());
                }
            }
            
            return new Packet250CustomPayload("btw", bytearrayoutputstream.toByteArray());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public void readDescriptionPacket(DataInputStream dataInputStream) throws IOException {
        this.priceMap.clear();
        
        int priceCount = dataInputStream.readInt();
        
        for (int i = 0; i < priceCount; ++i) {
            Short id = dataInputStream.readShort();
            Short price = dataInputStream.readShort();
            
            this.priceMap.put(id, price);
        }
    }
    
    public void writeFile(File file) {
        PrintWriter out = null;
        
        try {
            out = new PrintWriter(file);
            
            for (int i = 0; i < Item.itemsList.length; ++i) {
                Item item = Item.itemsList[i];
                
                if (item != null) {
                    out.write(((Integer) item.itemID).toString() + ":" + item.getUnlocalizedName() + ":0\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }
    
    public void readFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                this.writeFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return;
        }
        
        BufferedReader bufferedReader = null;
        
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            
            while (line != null) {
                String splitLine[] = line.split(":");
                
                Short id = Short.parseShort(splitLine[0]);
                Short price = Short.parseShort(splitLine[2]);
                
                this.priceMap.put(id, price);
                
                line = bufferedReader.readLine();
            }
            
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public short getPrice(Item item) {
        Short id = (short) item.itemID;
        Short price = this.priceMap.get(id);
        
        if (price != null) {
            return price;
        }
        
        return 0;
    }
}
