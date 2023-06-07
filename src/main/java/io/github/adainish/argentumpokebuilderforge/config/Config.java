package io.github.adainish.argentumpokebuilderforge.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import io.github.adainish.argentumpokebuilderforge.managers.PokeBuilderDataManager;
import io.github.adainish.argentumpokebuilderforge.util.Adapters;

import java.io.*;

public class Config
{
    public PokeBuilderDataManager pokeBuilderDataManager;


    public Config()
    {
        this.pokeBuilderDataManager = new PokeBuilderDataManager();
    }

    public static void writeConfig()
    {
        File dir = ArgentumPokeBuilderForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        Config config = new Config();
        try {
            File file = new File(dir, "pokebuilder.json");
            if (file.exists())
                return;
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            String json = gson.toJson(config);
            writer.write(json);
            writer.close();
        } catch (IOException e)
        {
            ArgentumPokeBuilderForge.getLog().warn(e);
        }
    }

    public static Config getConfig()
    {
        File dir = ArgentumPokeBuilderForge.getConfigDir();
        dir.mkdirs();
        Gson gson  = Adapters.PRETTY_MAIN_GSON;
        File file = new File(dir, "pokebuilder.json");
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            ArgentumPokeBuilderForge.getLog().error("Something went wrong attempting to read the Language Config");
            return null;
        }

        return gson.fromJson(reader, Config.class);
    }
}
