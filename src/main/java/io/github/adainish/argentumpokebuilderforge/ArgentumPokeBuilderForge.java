package io.github.adainish.argentumpokebuilderforge;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.platform.events.PlatformEvents;
import io.github.adainish.argentumpokebuilderforge.cmd.Command;
import io.github.adainish.argentumpokebuilderforge.config.Config;
import io.github.adainish.argentumpokebuilderforge.config.DBConfig;
import io.github.adainish.argentumpokebuilderforge.config.LanguageConfig;
import io.github.adainish.argentumpokebuilderforge.listener.PlayerListener;
import io.github.adainish.argentumpokebuilderforge.storage.Database;
import io.github.adainish.argentumpokebuilderforge.storage.PlayerStorage;
import io.github.adainish.argentumpokebuilderforge.wrapper.DataWrapper;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

// The value here should match an entry in the META-INF/mods.toml file
public class ArgentumPokeBuilderForge implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "argentumpokebuilderforge";
    // Directly reference a slf4j logger

    public static ArgentumPokeBuilderForge instance;
    public static final String MOD_NAME = "ArgentumPokeBuilder";
    public static final String VERSION = "1.0.0-Beta";
    public static final String AUTHORS = "Winglet";
    public static final String YEAR = "2023";
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(MOD_NAME);
    private static File configDir;
    private static File storage;
    private static File playerStorageDir;
    private static MinecraftServer server;

    public static DataWrapper dataWrapper;

    public static Config config;
    public static DBConfig dbConfig;

    public static LanguageConfig languageConfig;

    public static PlayerListener playerListener;

    public static PlayerStorage playerStorage;

    public static Logger getLog() {
        return log;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void setServer(MinecraftServer server) {
        ArgentumPokeBuilderForge.server = server;
    }

    public static File getConfigDir() {
        return configDir;
    }

    public static void setConfigDir(File configDir) {
        ArgentumPokeBuilderForge.configDir = configDir;
    }

    public static File getStorage() {
        return storage;
    }

    public static void setStorage(File storage) {
        ArgentumPokeBuilderForge.storage = storage;
    }

    public static File getPlayerStorageDir() {
        return playerStorageDir;
    }

    public static void setPlayerStorageDir(File playerStorageDir) {
        ArgentumPokeBuilderForge.playerStorageDir = playerStorageDir;
    }

    public ArgentumPokeBuilderForge() {

    }
    @Override
    public void onInitialize() {
        this.commonSetup();
    }
    private void commonSetup() {
        // Some common setup code
        instance = this;
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        //do data set up
        PlatformEvents.SERVER_STARTED.subscribe(Priority.NORMAL, t -> {
            setServer(t.getServer());
            playerStorage = new PlayerStorage();
            //init subscriptions
            playerListener = new PlayerListener();
            dataWrapper = new DataWrapper();
            reload();
            return Unit.INSTANCE;
        });

        PlatformEvents.SERVER_STOPPING.subscribe(Priority.NORMAL, t -> {
            this.handleShutDown();
            return Unit.INSTANCE;
        });


        CommandRegistrationCallback.EVENT.register((dispatcher, registryaccess, environment) -> {
            dispatcher.register(Command.getCommand());
        });
    }

    public void initDirs() {
        setConfigDir(new File(FabricLoader.getInstance().getConfigDir()  + "/ArgentumPokeBuilder/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
        setPlayerStorageDir(new File(storage, "/playerdata/"));
        getPlayerStorageDir().mkdirs();
    }



    public void initConfigs() {
        log.warn("Loading Config Files");
        DBConfig.writeConfig();
        dbConfig = DBConfig.getConfig();
        if (dbConfig != null)
        {
            if (dbConfig.enabled)
            {
                playerStorage.database = new Database();
            }
        }
        //write language files then assign them
        LanguageConfig.writeConfig();
        languageConfig = LanguageConfig.getConfig();

        Config.writeConfig();
        config = Config.getConfig();
    }

    public void reload() {
        initDirs();
        initConfigs();
    }

    public void handleShutDown()
    {
        playerStorage.saveAll();
        if (playerStorage.database != null)
            playerStorage.database.shutdown();
    }

}
