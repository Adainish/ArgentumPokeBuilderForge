package io.github.adainish.argentumpokebuilderforge.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Modifier;

public class Adapters
{
    public static Gson PRETTY_MAIN_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ItemStack.class, io.github.adainish.islandgyms.util.ItemStackAdapter.class)
            .disableHtmlEscaping()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();
}
