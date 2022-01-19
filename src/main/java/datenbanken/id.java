package datenbanken;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class id<setConfig> {
    public id( String normal, String path){
        init( path);
    }

    private static final HashMap<String, String> Configs = new HashMap<>();

    private static String prefix, path;

    public static void init( String ConfigFilePath){
        prefix = null;
        path = ConfigFilePath;
    }

    public static boolean hasmPrefix(String guildID){
        return Configs.containsKey(guildID);
    }

    public static String getid(String setting, Boolean print){
        if(Configs.containsKey(setting)){
            if (print.equals(true)){
            System.out.println("Botchannel:");
            System.out.println(Configs.get(setting));}
            return Configs.get(setting);
        }else{
            if (print.equals(true)){
            System.out.println("default Botchannel");}
            return prefix;
        }
    }

    public static void setId(String GuildId, String botchannelname){
        Configs.put(GuildId, botchannelname);
        saveToFile();
    }

    public static void idreadFromFile(){
        try {

            File file = new File(path);
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException error) {
                System.out.println("An error occurred.");
                error.printStackTrace();
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                Configs.put(data.split(":")[0], data.split(":")[1]);
            }
            System.out.println("Botchannels ausgelesen");


        }catch (FileNotFoundException d){
            d.printStackTrace();
        }
    }

    public static void saveToFile(){
        try {
            FileWriter fileWriter = new FileWriter(path);
            StringBuilder lines = new StringBuilder();
            for (HashMap.Entry<String, String> set : Configs.entrySet()) {
                lines.append(set.getKey()).append(":").append(set.getValue()).append("\n");
            }
            fileWriter.write(lines.toString());
            fileWriter.close();
            System.out.println("Botchannel gespeichert");
        }catch (IOException d){
            d.printStackTrace();
        }
    }
}
