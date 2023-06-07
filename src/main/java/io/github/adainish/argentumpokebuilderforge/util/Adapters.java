package io.github.adainish.argentumpokebuilderforge.util;

import com.cobblemon.mod.common.util.adapters.ItemStackAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Modifier;

public class Adapters
{
    public static Gson PRETTY_MAIN_GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(ItemStack.class, ItemStackAdapter.INSTANCE)
            .excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .create();
}
