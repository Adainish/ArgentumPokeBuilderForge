package io.github.adainish.argentumpokebuilderforge.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Util
{
    public static ServerPlayer getPlayer(UUID uuid) {
        return ArgentumPokeBuilderForge.getServer().getPlayerList().getPlayer(uuid);
    }

    public static boolean isOnline(UUID uuid)
    {
        return ArgentumPokeBuilderForge.getServer().getPlayerList().getPlayer(uuid) != null;
    }
    public static ItemStack returnIcon(Pokemon pokemon) {
        return PokemonItem.from(pokemon, 1);
    }

    public static double getIVSPercentage(int decimalPlaces, Pokemon p) {
        int total = 0;

        for (Stats st : Stats.values()) {
            if (st.equals(Stats.ACCURACY) || st.equals(Stats.EVASION))
                continue;
            if (p.getIvs().get(st) != null)
                total += p.getIvs().getOrDefault(st);
        }

        double percentage = (double) total / 186.0D * 100.0D;
        return Math.floor(percentage * Math.pow(10.0D, decimalPlaces)) / Math.pow(10.0D, decimalPlaces);
    }


    public static double getEVSPercentage(int decimalPlaces, Pokemon p) {
        int total = 0;

        for (Stats st : Stats.values()) {
            if (st.equals(Stats.ACCURACY) || st.equals(Stats.EVASION))
                continue;
            if (p.getEvs().get(st) != null)
                total += p.getEvs().getOrDefault(st);
        }

        double percentage = (double) total / 510.0D * 100.0D;
        return Math.floor(percentage * Math.pow(10.0D, decimalPlaces)) / Math.pow(10.0D, decimalPlaces);
    }

    public static int getIntFromStat(Stats stat, Pokemon pokemon, boolean ivs)
    {
        return ivs ? pokemon.getIvs().getOrDefault(stat) : pokemon.getEvs().getOrDefault(stat);
    }


    public static ArrayList<String> pokemonLore(Pokemon p) {
        ArrayList<String> list = new ArrayList<>();
        list.add("&7Friendship: &e" + p.getFriendship());
        if (p.getShiny())
            list.add("&6&lShiny");
        list.addAll(Arrays.asList("&7Ball:&e " + p.getCaughtBall().getName().getPath().replace("_", " "), "&7Ability:&e " + p.getAbility().getName().toLowerCase(), "&7Nature:&e " + p.getNature().getDisplayName().replace("cobblemon", "").replaceAll("\\.", "").replace("nature", ""), "&7Gender:&e " + p.getGender().name().toLowerCase(), "&7IVS: (&f%ivs%%&7)".replace("%ivs%", String.valueOf(getIVSPercentage(1, p))), "&cHP: %hp% &7/ &6Atk: %atk% &7/ &eDef: %def%"
                .replace("%hp%", String.valueOf(getIntFromStat(Stats.HP, p, true)))
                .replace("%atk%", String.valueOf(getIntFromStat(Stats.ATTACK, p, true)))
                .replace("%def%", String.valueOf(getIntFromStat(Stats.DEFENCE, p, true))), "&9SpA: %spa% &7/ &aSpD: %spd% &7/ &dSpe: %spe%"
                .replace("%spa%", String.valueOf(getIntFromStat(Stats.SPECIAL_ATTACK, p, true)))
                .replace("%spd%", String.valueOf(getIntFromStat(Stats.SPECIAL_DEFENCE, p, true)))
                .replace("%spe%", String.valueOf(getIntFromStat(Stats.SPEED, p, true))), "&7EVS: (&f%evs%%&7)".replace("%evs%", String.valueOf(getEVSPercentage(1, p))), "&cHP: %hp% &7/ &6Atk: %atk% &7/ &eDef: %def%"
                .replace("%hp%", String.valueOf(getIntFromStat(Stats.HP, p, false)))
                .replace("%atk%", String.valueOf(getIntFromStat(Stats.ATTACK, p, false)))
                .replace("%def%", String.valueOf(getIntFromStat(Stats.DEFENCE, p, false))), "&9SpA: %spa% &7/ &aSpD: %spd% &7/ &dSpe: %spe%"
                .replace("%spa%", String.valueOf(getIntFromStat(Stats.SPECIAL_ATTACK, p, false)))
                .replace("%spd%", String.valueOf(getIntFromStat(Stats.SPECIAL_DEFENCE, p, false)))
                .replace("%spe%", String.valueOf(getIntFromStat(Stats.SPEED, p, false)))));


        return list;
    }

    public static void runCommand(String cmd)
    {
        try {
            ArgentumPokeBuilderForge.getServer().getCommands().getDispatcher().execute(cmd, ArgentumPokeBuilderForge.getServer().createCommandSourceStack());
        } catch (CommandSyntaxException e) {
            ArgentumPokeBuilderForge.getLog().error(e);
        }
    }

    public static void send(ServerPlayer sender, String message) {
        sender.sendSystemMessage(Component.literal(((TextUtil.getMessagePrefix()).getString() + message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }
    public static void send(CommandSourceStack sender, String message) {
        sender.sendSystemMessage(Component.literal(((TextUtil.getMessagePrefix()).getString() + message).replaceAll("&([0-9a-fk-or])", "\u00a7$1")));
    }
    public static String formattedString(String s) {
        return s.replaceAll("&", "§");
    }

    public static List<String> formattedArrayList(List<String> list) {

        List<String> formattedList = new ArrayList<>();
        for (String s:list) formattedList.add(formattedString(s));

        return formattedList;
    }

    public static List<Component> formattedComponentList(List<String> s) {
        return s.stream().map(str -> Component.literal(formattedString(str))).collect(Collectors.toList());
    }
}
