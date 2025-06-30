package io.github.adainish.argentumpokebuilderforge.managers;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.argentumpokebuilderforge.enumerations.BuilderType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PokeBuilderDataManager
{
    public HashMap<BuilderType, Integer> baseAttributeCosts = new HashMap<>();
    public HashMap<BuilderType, ItemStack> attributeIcons = new HashMap<>();
    public HashMap<BuilderType, Boolean> attributeEnabledStatus = new HashMap<>();
    public List<String> blacklistedSpecies = new ArrayList<>();
    public List<String> genderPreventionIDList = new ArrayList<>(Arrays.asList("cobblemon:latias", "cobblemon:latios", "cobblemon:volbeat", "cobblemon:illumise", "cobblemon:jynx", "cobblemon:kangaskhan", "cobblemon:blissey", "cobblemon:chansey", "cobblemon:happiny", "cobblemon:smoochum", "cobblemon:hitmonlee", "cobblemon:hitmonchan", "cobblemon:hitmontop", "cobblemon:nidoqueen", "cobblemon:nidoking", "cobblemon:nidoran", "cobblemon:nidorina", "cobblemon:nidorino", "cobblemon:tauros", "cobblemon:miltank", "cobblemon:wormadam", "cobblemon:mothim", "cobblemon:vespiquen", "cobblemon:gallade", "cobblemon:froslass", "cobblemon:cresselia", "cobblemon:throh", "cobblemon:sawk", "cobblemon:petilil", "cobblemon:lilligant", "cobblemon:braviary", "cobblemon:mandibuzz", "cobblemon:tornadus", "cobblemon:landorus", "cobblemon:thundurus", "cobblemon:enamorus", "cobblemon:florges", "cobblemon:flabebe", "cobblemon:floette", "cobblemon:salazzle", "cobblemon:steenee", "cobblemon:bounsweet", "cobblemon:tsareena", "cobblemon:alcremie", "cobblemon:tinkatink", "cobblemon:tinkatuff", "cobblemon:tinkaton", "cobblemon:grimmsnarl", "cobblemon:hatterene"));
    public PokeBuilderDataManager()
    {
        init();
    }

    public void init()
    {
        if (baseAttributeCosts.isEmpty())
            Arrays.stream(BuilderType.values()).filter(builderType -> !builderType.equals(BuilderType.UNDECIDED)).forEachOrdered(builderType -> baseAttributeCosts.put(builderType, 100));
        if (attributeEnabledStatus.isEmpty())
            Arrays.stream(BuilderType.values()).filter(builderType -> !builderType.equals(BuilderType.UNDECIDED)).forEachOrdered(builderType -> attributeEnabledStatus.put(builderType, true));
        if (attributeIcons.isEmpty())
            Arrays.stream(BuilderType.values()).filter(builderType -> !builderType.equals(BuilderType.UNDECIDED)).forEachOrdered(builderType -> attributeIcons.put(builderType, new ItemStack(CobblemonItems.POKE_BALL.asItem())));
        if (this.blacklistedSpecies.isEmpty())
            PokemonSpecies.INSTANCE.getImplemented().stream().filter(species -> species.create(1).isLegendary()).forEachOrdered(species -> blacklistedSpecies.add(species.getResourceIdentifier().toString()));
    }
}
