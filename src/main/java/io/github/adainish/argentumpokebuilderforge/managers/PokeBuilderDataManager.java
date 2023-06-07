package io.github.adainish.argentumpokebuilderforge.managers;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import io.github.adainish.argentumpokebuilderforge.enumerations.BuilderType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PokeBuilderDataManager
{
    public HashMap<BuilderType, Integer> baseAttributeCosts = new HashMap<>();
    public HashMap<BuilderType, ItemStack> attributeIcons = new HashMap<>();

    public HashMap<BuilderType, Boolean> attributeEnabledStatus = new HashMap<>();

    public List<String> blacklistedSpecies = new ArrayList<>();

    public PokeBuilderDataManager()
    {
        init();
    }

    public void init()
    {
        if (baseAttributeCosts.isEmpty()) {
            for (BuilderType builderType : BuilderType.values()) {
                if (builderType.equals(BuilderType.UNDECIDED))
                    continue;
                baseAttributeCosts.put(builderType, 100);
            }
        }
        if (attributeEnabledStatus.isEmpty())
        {
            for (BuilderType builderType : BuilderType.values()) {
                if (builderType.equals(BuilderType.UNDECIDED))
                    continue;
                attributeEnabledStatus.put(builderType, true);
            }
        }
        if (attributeIcons.isEmpty())
        {
            for (BuilderType builderType : BuilderType.values()) {
                if (builderType.equals(BuilderType.UNDECIDED))
                    continue;
                attributeIcons.put(builderType, new ItemStack(CobblemonItems.POKE_BALL.get()));
            }
        }
        if (this.blacklistedSpecies.isEmpty())
        {
            for (Species species: PokemonSpecies.INSTANCE.getImplemented()) {
                if (species.create(1).isLegendary())
                    blacklistedSpecies.add(species.getResourceIdentifier().toString());
            }
        }
    }
}
