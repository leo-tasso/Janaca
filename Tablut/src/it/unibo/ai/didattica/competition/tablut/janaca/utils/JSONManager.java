package it.unibo.ai.didattica.competition.tablut.janaca.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONManager {

    private JSONManager() {
        super();
    }

    static public Map<String, Double> byFileToProgram(String nameFile){
        Map<String, Double> map = new HashMap<String, Double>();
        return map;
    }

    static public boolean byProgramToFile(Map<String, Double> toDump, String nameFile){
        // Create a Gson instance with pretty printing enabled
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert the map to a JSON string
        String json = gson.toJson(toDump);

        boolean result = true;
        // Write the JSON string to a file
        try (FileWriter writer = new FileWriter(nameFile)) {
            writer.write(json);
            System.out.println("JSON file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
