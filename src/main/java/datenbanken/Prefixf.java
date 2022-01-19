package datenbanken;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Prefixf {

    private static final HashMap<String, String> Configs = new HashMap<>();

    private static String prefix, path;

    public static void init(String defaultPrefix, String ConfigFilePath){
        prefix = defaultPrefix;
        path = ConfigFilePath;
    }

    public static boolean hasmPrefix(String guildID){
        return Configs.containsKey(guildID);
    }

    public static String getPrefix(String GuildId){
        if(Configs.containsKey(GuildId)){
            System.out.println("Setting:");
            System.out.println(Configs.get(GuildId));
            return Configs.get(GuildId);
        }else{
            System.out.println("defult Prefix");
            return prefix;
        }
    }

    public static void setPrefix(String GuildId, String botchannelname){
        Configs.put(GuildId, botchannelname);
        saveToFile();
    }

    public static void PrefixreadFromFile(){
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
            System.out.println("Prefixe ausgelesen");


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
            System.out.println("Prefix gespeichert");
        }catch (IOException d){
            d.printStackTrace();
        }
    }
}