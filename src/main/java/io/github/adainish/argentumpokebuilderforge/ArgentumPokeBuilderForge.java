package io.github.adainish.argentumpokebuilderforge;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.mojang.logging.LogUtils;
import io.github.adainish.argentumpokebuilderforge.cmd.Command;
import io.github.adainish.argentumpokebuilderforge.config.Config;
import io.github.adainish.argentumpokebuilderforge.config.LanguageConfig;
import io.github.adainish.argentumpokebuilderforge.listener.PlayerListener;
import io.github.adainish.argentumpokebuilderforge.wrapper.DataWrapper;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.cli.Arg;

import java.io.File;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ArgentumPokeBuilderForge.MODID)
public class ArgentumPokeBuilderForge {

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

    public static LanguageConfig languageConfig;

    public static PlayerListener playerListener;


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
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        instance = this;
        log.info("Booting up %n by %authors %v %y"
                .replace("%n", MOD_NAME)
                .replace("%authors", AUTHORS)
                .replace("%v", VERSION)
                .replace("%y", YEAR)
        );
        //do data set up
        CobblemonEvents.SERVER_STARTED.subscribe(Priority.NORMAL, minecraftServer -> {
            setServer(minecraftServer);
            //init subscriptions
            playerListener = new PlayerListener();
            dataWrapper = new DataWrapper();
            reload();
            return Unit.INSTANCE;
        });

        CobblemonEvents.SERVER_STOPPING.subscribe(Priority.NORMAL, minecraftServer -> {
            dataWrapper.playerCache.forEach((uuid, player) -> {
                player.save();
            });
            return Unit.INSTANCE;
        });
    }

    @SubscribeEvent
    public void onCommandRegistration(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Command.getCommand());
    }

    public void initDirs() {
        setConfigDir(new File(FMLPaths.GAMEDIR.get().resolve(FMLConfig.defaultConfigPath()) + "/ArgentumPokeBuilder/"));
        getConfigDir().mkdir();
        setStorage(new File(getConfigDir(), "/storage/"));
        getStorage().mkdirs();
        setPlayerStorageDir(new File(storage, "/playerdata/"));
        getPlayerStorageDir().mkdirs();
    }



    public void initConfigs() {
        log.warn("Loading Config Files");

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
}
