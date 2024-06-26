package io.github.adainish.argentumpokebuilderforge.util;

import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil
{
    private static final Pattern HEXPATTERN = Pattern.compile("\\{(#[a-fA-F0-9]{6})}");
    private static final String SPLITPATTERN = "((?=\\{#[a-fA-F0-9]{6}}))";

    public static Component parseHexCodes(String text, boolean removeItalics) {
        if(text == null)
            return null;
        MutableComponent comp = Component.literal(text);

        String[] temp = text.split(SPLITPATTERN);
        Arrays.stream(temp).forEach(s -> {
            Matcher m = HEXPATTERN.matcher(s);
            if(m.find()) {
                TextColor color = TextColor.parseColor(m.group(1));
                s = m.replaceAll("");
                if(removeItalics)
                    comp.append(Component.literal(s).setStyle(Style.EMPTY.withColor(color).withItalic(false)));
                else
                    comp.append(Component.literal(s).setStyle(Style.EMPTY.withColor(color)));
            } else {
                comp.append(Component.literal(s));
            }
        });

        return comp;
    }

    public static final TextColor BLUE = TextColor.parseColor("#00AFFC");
    public static final TextColor ORANGE = TextColor.parseColor("#FF6700");
    private static final MutableComponent PLUGIN_PREFIX = Component.literal(Util.formattedString(ArgentumPokeBuilderForge.languageConfig.prefix)).setStyle(Style.EMPTY.withColor(BLUE));

    private static final MutableComponent MESSAGE_PREFIX = getPluginPrefix().append(Component.literal(ArgentumPokeBuilderForge.languageConfig.splitter).setStyle(Style.EMPTY.withColor(ORANGE)));

    /**
     * @return a copy of the coloured MostWanted TextComponent
     */
    public static MutableComponent getPluginPrefix() {
        return PLUGIN_PREFIX.copy();
    }

    /**
     * @return a copy of the coloured MostWanted prefix
     */
    public static MutableComponent getMessagePrefix() {
        return MESSAGE_PREFIX.copy();
    }
}
