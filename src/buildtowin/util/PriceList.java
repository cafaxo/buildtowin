package buildtowin.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
    
    private HashMap<Short, Short> priceMap = new HashMap<Short, Short>();
    
    public void init(File modConfigurationDirectory) {
        File file = new File(modConfigurationDirectory, "buildtowin_pricelist.conf");
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            InputStream inputStream = PriceList.class.getResourceAsStream("/mods/buildtowin/pricelist.conf");
            FileOutputStream fileOutputStream;
            
            try {
                fileOutputStream = new FileOutputStream(file);
                
                int numRead;
                byte buf[] = new byte[1024];
                
                while ((numRead = inputStream.read(buf)) >= 0) {
                    fileOutputStream.write(buf, 0, numRead);
                }
                
                inputStream.close();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        this.read(file);
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
            
            for (Item item : Item.itemsList) {
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
    
    public void read(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            
            while (line != null) {
                String splitLine[] = line.split(":");
                
                if (splitLine.length == 3) {
                    try {
                        Short id = Short.parseShort(splitLine[0]);
                        Short price = Short.parseShort(splitLine[2]);
                        
                        this.priceMap.put(id, price);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                
                line = bufferedReader.readLine();
            }
            
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
    
    public static PriceList getInstance(World world) {
        if (world.isRemote) {
            return PriceList.clientInstance;
        } else {
            return PriceList.serverInstance;
        }
    }
}
