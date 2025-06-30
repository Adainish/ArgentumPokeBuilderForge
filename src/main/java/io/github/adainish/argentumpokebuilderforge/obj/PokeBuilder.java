package io.github.adainish.argentumpokebuilderforge.obj;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import ca.landonjw.gooeylibs2.api.button.PlaceholderButton;
import ca.landonjw.gooeylibs2.api.button.linked.LinkType;
import ca.landonjw.gooeylibs2.api.button.linked.LinkedPageButton;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.abilities.AbilityPool;
import com.cobblemon.mod.common.api.abilities.PotentialAbility;
import com.cobblemon.mod.common.api.pokeball.PokeBalls;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.NoPokemonStoreException;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.pokeball.PokeBall;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import io.github.adainish.argentumpokebuilderforge.enumerations.BuilderType;
import io.github.adainish.argentumpokebuilderforge.util.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PokeBuilder
{
    public Player assignedPlayer;
    public BuilderType builderType = BuilderType.UNDECIDED;
    public Pokemon selectedPokemon = null;

    public String selectedAction = "";

    public int amount = 0;
    public Stat selectedStat;

    public SpeciesFeature speciesFeature;

    public PotentialAbility potentialAbility;


    public PokeBuilder(Player player)
    {
        this.assignedPlayer = player;
    }

    public boolean canAfford()
    {
        return assignedPlayer.tokenCount >= getCost(builderType);
    }

    public int getCost(BuilderType builderType)
    {
        int amount = 0;
        if (ArgentumPokeBuilderForge.config.pokeBuilderDataManager.baseAttributeCosts.containsKey(builderType))
            amount = ArgentumPokeBuilderForge.config.pokeBuilderDataManager.baseAttributeCosts.get(builderType);
        if (builderType.equals(BuilderType.EVS) || builderType.equals(BuilderType.IVS) || builderType.equals(BuilderType.FRIENDSHIP))
        {
            if (Math.abs(this.amount) > 0) {
                amount = (amount * Math.abs(this.amount));
            }
        }
        return amount;
    }

    public int getMaxAmount()
    {
        int maxInt = 31;
        switch (builderType)
        {
            case FRIENDSHIP -> {
                maxInt = 255;
                break;
            }
            case EVS -> {
                maxInt = 252;
                break;
            }
        }

        return maxInt;
    }

    public int getLeftoverSubstractableEVSAmount()
    {
        return selectedPokemon.getEvs().getOrDefault(selectedStat);
    }

    public int getLeftOverEVSAmount()
    {
        AtomicInteger amount = new AtomicInteger();

        int max = 510;
        selectedPokemon.getEvs().forEach(entry -> {
                amount.addAndGet(entry.getValue());
        });


        return max - amount.get();
    }

    public int getMaxNegativeAdjustment() {
        int newStatMax = 0;

        switch (builderType) {
            case FRIENDSHIP -> {
                return selectedPokemon.getFriendship();
            }

            case EVS -> {
                return getLeftoverSubstractableEVSAmount();
            }
            case IVS -> {
                return selectedPokemon.getIvs().getOrDefault(selectedStat);
            }
        }

        return newStatMax;
    }



    public int getMaxPositiveAdjustment()
    {
        int maxType = getMaxAmount();
        int newStatMax = 0;

        switch (builderType)
        {
            case FRIENDSHIP -> {
                if (selectedPokemon.getFriendship() >= maxType)
                    return 0;
                newStatMax = (maxType - selectedPokemon.getFriendship());
                break;
            }

            case EVS -> {
                if (selectedPokemon.getEvs().getOrDefault(selectedStat) >= maxType)
                    return 0;
                newStatMax = (maxType - selectedPokemon.getEvs().getOrDefault(selectedStat));
                if (newStatMax > getLeftOverEVSAmount())
                    newStatMax = getLeftOverEVSAmount();
                break;
            }
            case IVS -> {
                if (selectedPokemon.getIvs().getOrDefault(selectedStat) >= maxType)
                    return 0;
                newStatMax = (maxType - selectedPokemon.getIvs().getOrDefault(selectedStat));
                break;
            }
        }

        return newStatMax;
    }

    public void decreaseAmount(int parsed)
    {
        if ( (this.amount - parsed ) > ( -getMaxNegativeAdjustment()))
            this.amount -= parsed;
        else this.amount = -getMaxNegativeAdjustment();
    }

    public void increaseAmount(int parsed)
    {
        if ((amount + parsed ) < getMaxPositiveAdjustment()) {
            this.amount += parsed;
        }
        else this.amount = getMaxPositiveAdjustment();
    }


    public ItemStack getIcon(BuilderType builderType)
    {
        ItemStack stack = new ItemStack(Items.PAPER);
        if (ArgentumPokeBuilderForge.config.pokeBuilderDataManager.attributeIcons.containsKey(builderType))
            stack = ArgentumPokeBuilderForge.config.pokeBuilderDataManager.attributeIcons.get(builderType).copy();
        return stack;
    }

    public String getOrDefaultStringStat(Stat stat)
    {
        if (stat != null)
            return stat.toString();
        else return "None";
    }

    public void open(ServerPlayer serverPlayer)
    {
        UIManager.openUIForcefully(serverPlayer, mainGUI());
    }

    public GooeyButton filler() {
        return GooeyButton.builder()
                .display(new ItemStack(Items.GRAY_STAINED_GLASS_PANE))
                .build();
    }

    public List<Button> editGUIButtons()
    {
        List<Button> buttons = new ArrayList<>();

        switch (this.builderType)
        {
            case EVS, IVS -> {
                for (Stat stat: Stats.values()) {
                    if (stat.equals(Stats.ACCURACY) || stat.equals(Stats.EVASION))
                        continue;
                    GooeyButton gooeyButton = GooeyButton.builder()
                            .display(new ItemStack(CobblemonItems.MUSCLE_BAND.asItem()))
                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&e" + stat.getDisplayName().getString())))
                            .onClick(b -> {
                                this.selectedStat = stat;
                                //go to amount edit menu
                                UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                            })
                            .build();
                    buttons.add(gooeyButton);
                }
                break;
            }
            case ABILITY -> {

                this.selectedPokemon.getForm().getAbilities().forEach(potentialAbility -> {
                    try {
                        var ability = potentialAbility.getTemplate().create(true, Priority.NORMAL).getDescription();
                        GooeyButton button = GooeyButton.builder()
                                .display(new ItemStack(CobblemonItems.BLACK_SLUDGE.asItem()))
                                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&e%ability%".replace("%ability%", potentialAbility.getTemplate().getName()))))
                                .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(Arrays.asList(ability))))
                                .onClick(b -> {
                                    this.potentialAbility = potentialAbility;
                                    UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                                })
                                .build();
                        buttons.add(button);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                });

                break;
            }

//            case FORM -> {
//                selectedPokemon.getSpecies().getForms().forEach(formData -> {
//                    GooeyButton button = GooeyButton.builder()
//            .display(new ItemStack(CobblemonItems.NEST_BALL.get()))
//                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&b" + formData.getName())))
//
//                            .onClick(b -> {
//                                //go to purchase
//                                this.speciesFeature = formData;
//                                UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
//                            })
//                            .build();
//                    if (selectedPokemon.getForm().equals(formData))
//                        button = GooeyButton.builder()
            //           .display(new ItemStack(Items.TNT))
//                                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&cAlready on this form")))
//
//                                .build();
//                    buttons.add(button);
//                });
//                break;
//            }

            case SHINY -> {
                GooeyButton button;
                if (selectedPokemon.getShiny())
                {
                    button = GooeyButton.builder()
                            .display(new ItemStack(Items.ENDER_PEARL))
                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&cUnshiny")))
                            .onClick(b -> {
                                //open purchase
                                this.selectedAction = "false";
                                UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                            })
                            .build();
                } else {
                    button = GooeyButton.builder()
                            .display(new ItemStack(CobblemonItems.SHINY_STONE.asItem()))
                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&aShiny")))
                            .onClick(b -> {
                                //open purchase
                                this.selectedAction = "true";
                                UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                            })
                            .build();
                }
                buttons.add(button);
                break;
            }
            case GENDER -> {
                //do gender check
                if (selectedPokemon.getGender().equals(Gender.GENDERLESS))
                    return buttons;
                for (Gender gender:Gender.values()) {
                    if (gender.equals(Gender.GENDERLESS))
                        continue;
                    GooeyButton button = GooeyButton.builder()
                            .display(new ItemStack(Items.LIME_DYE))
                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&e" + gender.name())))
                            .onClick(b -> {
                                //do purchase
                                this.selectedAction = gender.name();
                                UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                            })
                            .build();

                    if (selectedPokemon.getGender().equals(gender))
                        button = GooeyButton.builder()
                                .display(new ItemStack(Items.TNT))
                                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&cAlready this gender")))
                                .build();
                    if (isPokemonNonGenderExchangeable(this.selectedPokemon))
                    {
                        button = GooeyButton.builder()
                                .display(new ItemStack(Items.TNT))
                                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&cThis Pokemon's Gender can't change!")))
                                .build();
                    }
                    buttons.add(button);
                }
                break;
            }
            case NATURE -> {
                Natures.INSTANCE.all().forEach(nature -> {
                    GooeyButton button = GooeyButton.builder()
                            .display(new ItemStack(CobblemonItems.MIRACLE_SEED.asItem()))
                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("") + nature.getDisplayName().replace("cobblemon", "").replaceAll("\\.", "").replace("nature", "")))
                            .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(Arrays.asList("&a+" + getOrDefaultStringStat(nature.getIncreasedStat()), "&c-" + getOrDefaultStringStat(nature.getDecreasedStat())))))
                            .onClick(b -> {
                                //open purchase
                                this.selectedAction = nature.getName().toString();
                                UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                            })
                            .build();
                    if (selectedPokemon.getNature().equals(nature))
                        button = GooeyButton.builder()
                                .display(new ItemStack(Items.TNT))
                                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&cAlready has this nature")))
                                .build();
                    buttons.add(button);
                });
                break;
            }
            case POKEBALL -> {
                PokeBalls.INSTANCE.all().forEach(pokeBall -> {
                    GooeyButton button = GooeyButton.builder()
                            .display(new ItemStack(pokeBall.item()))
                            .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("") + pokeBall.getName().getPath().replace("_", " ")))
                            .onClick(b -> {
                                //open purchase
                                selectedAction = pokeBall.getName().toString();
                                UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                            })
                            .build();
                    if (selectedPokemon.getCaughtBall().equals(pokeBall))
                        button = GooeyButton.builder()
                                .display(new ItemStack(Items.TNT))
                                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&cAlready has this poke ball")))
                                .build();
                    buttons.add(button);
                });
                break;
            }
        }

        return buttons;
    }

    public boolean isPokemonNonGenderExchangeable(Pokemon pokemon)
    {
        return ArgentumPokeBuilderForge.config.pokeBuilderDataManager.genderPreventionIDList.contains(pokemon.getSpecies().resourceIdentifier.toString());
    }
    public List<Button> builderTypeButtonList()
    {
        List<Button> buttons = new ArrayList<>();
        for (BuilderType builderType:BuilderType.values()) {
            if (builderType.equals(BuilderType.UNDECIDED))
                continue;
            if (!ArgentumPokeBuilderForge.config.pokeBuilderDataManager.attributeEnabledStatus.getOrDefault(builderType, false))
                continue;
            GooeyButton button = GooeyButton.builder()
                    .display(getIcon(builderType))
                    .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&b" + builderType.name().toLowerCase())))
                    .onClick(b -> {
                        this.builderType = builderType;
                        this.amount = 0;
                        //open adaptable edit menu
                        UIManager.openUIForcefully(b.getPlayer(), this.builderType.equals(BuilderType.FRIENDSHIP) ? statAmountGUI() : editGUI());
                    })
                    .build();
            buttons.add(button);
        }
        return buttons;
    }

    public boolean isBlackListed(Pokemon pokemon)
    {
        return ArgentumPokeBuilderForge.config.pokeBuilderDataManager.blacklistedSpecies.contains(pokemon.getSpecies().resourceIdentifier.toString());
    }

    public List<Button> partyMemberButtonList()
    {
        List<Button> buttons = new ArrayList<>();

        PartyStore partyStore = Cobblemon.INSTANCE.getStorage().getParty(assignedPlayer.uuid, ArgentumPokeBuilderForge.getServer().registryAccess());
        partyStore.forEach(pokemon -> {
            if (pokemon == null)
                return;
            GooeyButton button;
            if (!isBlackListed(pokemon)) {
                button = GooeyButton.builder()
                        .display(Util.returnIcon(pokemon))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString(pokemon.getSpecies().getName())))
                        .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(Util.formattedArrayList(Util.pokemonLore(pokemon)))))
                        .onClick(b -> {
                            this.selectedPokemon = pokemon;
                            //open builder type selection GUI
                            UIManager.openUIForcefully(b.getPlayer(), builderTypeGUI());
                        })
                        .build();
            } else {
                button = GooeyButton.builder()
                        .display(new ItemStack(Items.BARRIER))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&4&lBlacklisted")))
                        .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(Util.formattedArrayList(Arrays.asList("&c&l(&4&l!&c&l) &4You may not modify this Pokemon!")))))
                        .build();
            }
            buttons.add(button);
        });
        return buttons;
    }

    public void transform()
    {
        String message = "&aModified your Pokemon!";
        switch (builderType)
        {
            case FRIENDSHIP -> {
                int amount = selectedPokemon.getFriendship();
                if (this.amount < 0) {
                    amount -= Math.abs(this.amount);
                } else {
                    amount += this.amount;
                }
                selectedPokemon.setFriendship(amount, true);
                message = "&aAdjusted your Pokemon's friendship to %amount%".replace("%amount%", String.valueOf(this.amount));
                break;
            }
            case EVS -> {
                Stat stat = selectedStat;

                if (stat != null)
                {
                    int amount = selectedPokemon.getEvs().getOrDefault(stat);
                    if (this.amount < 0) {
                        amount -= Math.abs(this.amount);
                    } else {
                        amount += this.amount;
                    }
                    selectedPokemon.getEvs().set(stat, amount);
                }
                message = "&aAdjusted your Pokemon's %stat% EV by %amount%".replace("%stat%", selectedStat.getDisplayName().getString()).replace("%amount%", String.valueOf(this.amount));
                break;
            }
            case IVS -> {
                Stat stat = selectedStat;

                if (stat != null)
                {
                    int amount = selectedPokemon.getIvs().getOrDefault(stat);
                    if (this.amount < 0) {
                        amount -= Math.abs(this.amount);
                    } else {
                        amount += this.amount;
                    }
                    selectedPokemon.getIvs().set(stat, amount);
                    message = "&aAdjusted your Pokemon's %stat% IV by %amount%".replace("%stat%", selectedStat.getDisplayName().getString()).replace("%amount%", String.valueOf(this.amount));
                }
                break;
            }
            case ABILITY -> {
                Ability ability = this.potentialAbility.getTemplate().create(true, Priority.NORMAL);

                selectedPokemon.setAbility$common(ability);
                message = "&aAdjusted your Pokemon's ability to %ability%".replace("%ability%", ability.getName());
                break;
            }
            case POKEBALL -> {
                PokeBall pokeBall = PokeBalls.INSTANCE.getPokeBall(ResourceLocation.tryParse(this.selectedAction));
                if (pokeBall != null) {
                    selectedPokemon.setCaughtBall(pokeBall);
                    message = "&aAdjusted your Pokemon's pokeball to %pokeball%".replace("%pokeball%", pokeBall.getName().getPath().toString().replaceAll("_", " "));
                }
                break;
            }
            case NATURE -> {
                Nature nature = Natures.INSTANCE.getNature(ResourceLocation.tryParse(this.selectedAction));
                if (nature != null) {
                    selectedPokemon.setNature(nature);
                    message = "&aAdjusted your Pokemon's nature to %nature%".replace("%nature%", nature.getDisplayName().replace("cobblemon", "").replaceAll("\\.", "").replace("nature", ""));
                }
                break;
            }
            case GENDER -> {
                Gender gender = Gender.valueOf(this.selectedAction);
                if (gender != null)
                    selectedPokemon.setGender(gender);
                message = "&aAdjusted your Pokemon's gender to %gender%".replace("%gender%", gender.name().toLowerCase());
                break;
            }
            case SHINY -> {
                selectedPokemon.setShiny(this.selectedAction.equals("true"));
                message = "&aAdjusted your Pokemon's shiny status!";
            }
//            case FORM -> {
//
//                selectedPokemon.setFeatures(new ArrayList<>(Arrays.asList(speciesFeature)));
//                selectedPokemon.updateAspects();
//                break;
//            }

        }
        assignedPlayer.sendMessage(message);
    }

    public GooeyPage purchaseGUI()
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        GooeyButton back = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&eGo Back")))
                .onClick(b -> {
                    if (builderType.equals(BuilderType.IVS) || builderType.equals(BuilderType.EVS) || builderType.equals(BuilderType.FRIENDSHIP))
                    {
                        UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                    } else UIManager.openUIForcefully(b.getPlayer(), editGUI());
                })
                .build();

        GooeyButton confirm = GooeyButton.builder()
                .display(new ItemStack(Items.GREEN_DYE))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&aConfirm Modification")))
                .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(Arrays.asList("&7Current cost: &b%amount%".replace("%amount%", String.valueOf(getCost(builderType)))))))
                .onClick(b -> {
                    if (canAfford())
                    {
                        //convert selected action to convertable and applicable data then transform the pokemon info
                        assignedPlayer.tokenCount -= getCost(builderType);
                        assignedPlayer.save();
                        transform();
                        UIManager.closeUI(b.getPlayer());
                    } else {
                        assignedPlayer.sendMessage("&eYou can't afford this modification!");
                    }
                })
                .build();

        builder.set(2, 3, back);
        builder.set(2, 5, confirm);


        return GooeyPage.builder().template(builder.build()).build();
    }

    public GooeyPage statAmountGUI()
    {
        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        GooeyButton back = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&eGo Back")))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), builderTypeGUI());
                })
                .build();

        GooeyButton confirm = GooeyButton.builder()
                .display(new ItemStack(Items.GREEN_DYE))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&aConfirm Amount")))
                .with(DataComponents.LORE, new ItemLore(Util.formattedComponentList(Arrays.asList("&7Current amount: &b%amount%".replace("%amount%", String.valueOf(amount))))))
                .onClick(b -> {
                    if (this.amount == 0)
                    {
                        assignedPlayer.sendMessage("&eYou can't edit a Pokemon's stats with a total of 0!");
                        return;
                    }
                    UIManager.openUIForcefully(b.getPlayer(), purchaseGUI());
                })
                .build();

        builder.set(2, 3, back);
        builder.set(2, 5, confirm);

        switch (builderType)
        {
            case EVS, FRIENDSHIP -> {
                GooeyButton increaseOne = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+1")))
                        .onClick(b -> {
                            increaseAmount(1);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseTen = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+10")))
                        .onClick(b -> {
                            increaseAmount(10);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseTwentyFive = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+25")))
                        .onClick(b -> {
                            increaseAmount(25);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseFifty = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+50")))
                        .onClick(b -> {
                            increaseAmount(50);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseHundred = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+100")))
                        .onClick(b -> {
                            increaseAmount(100);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseMax = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+Max Amount")))
                        .onClick(b -> {
                            increaseAmount(getMaxAmount());
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                builder.set(1, 1, increaseOne);
                builder.set(1, 2, increaseTen);
                builder.set(1, 3, increaseTwentyFive);
                builder.set(1, 4, increaseFifty);
                builder.set(1, 5, increaseHundred);
                builder.set(1, 6, increaseMax);

                GooeyButton decreaseOne = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-1")))
                        .onClick(b -> {
                            decreaseAmount(1);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseTen = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-10")))
                        .onClick(b -> {
                            decreaseAmount(10);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseTwentyFive = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-25")))
                        .onClick(b -> {
                            decreaseAmount(25);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseFifty = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-50")))
                        .onClick(b -> {
                            decreaseAmount(50);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseHundred = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-100")))
                        .onClick(b -> {
                            decreaseAmount(100);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseMax = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-Max Amount")))
                        .onClick(b -> {
                            decreaseAmount(getMaxAmount());
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                builder.set(3, 1, decreaseOne);
                builder.set(3, 2, decreaseTen);
                builder.set(3, 3, decreaseTwentyFive);
                builder.set(3, 4, decreaseFifty);
                builder.set(3, 5, decreaseHundred);
                builder.set(3, 6, decreaseMax);
                break;
            }
            case IVS -> {
                GooeyButton increaseOne = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+1")))
                        .onClick(b -> {
                            increaseAmount(1);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseTen = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+10")))
                        .onClick(b -> {
                            increaseAmount(10);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton increaseTwentyFive = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+25")))
                        .onClick(b -> {
                            increaseAmount(25);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();


                GooeyButton increaseMax = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.ELECTIRIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&a+Max Amount")))
                        .onClick(b -> {
                            increaseAmount(getMaxAmount());
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                builder.set(1, 1, increaseOne);
                builder.set(1, 3, increaseTen);
                builder.set(1, 5, increaseTwentyFive);
                builder.set(1, 7, increaseMax);

                GooeyButton decreaseOne = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-1")))
                        .onClick(b -> {
                            decreaseAmount(1);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseTen = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-10")))
                        .onClick(b -> {
                            decreaseAmount(10);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseTwentyFive = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-25")))
                        .onClick(b -> {
                            decreaseAmount(25);
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                GooeyButton decreaseMax = GooeyButton.builder()
                        .display(new ItemStack(CobblemonItems.MAGMARIZER.asItem()))
                        .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&c-Max Amount")))
                        .onClick(b -> {
                            decreaseAmount(getMaxAmount());
                            UIManager.openUIForcefully(b.getPlayer(), statAmountGUI());
                        })
                        .build();

                builder.set(3, 1, decreaseOne);
                builder.set(3, 3, decreaseTen);
                builder.set(3, 5, decreaseTwentyFive);
                builder.set(3, 7, decreaseMax);
                break;
            }
        }

        return GooeyPage.builder().template(builder.build()).build();
    }

    public LinkedPage editGUI()
    {

        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("Previous Page")))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("Next Page")))
                .linkType(LinkType.Next)
                .build();

        GooeyButton back = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&eGo Back")))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), builderTypeGUI());
                })
                .build();

        builder.set(0, 3, previous)
                .set(0, 5, next)
                .set(0, 0, back)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), editGUIButtons(), LinkedPage.builder().template(builder.build()));
    }

    public LinkedPage builderTypeGUI()
    {

        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("Previous Page")))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("Next Page")))
                .linkType(LinkType.Next)
                .build();


        GooeyButton back = GooeyButton.builder()
                .display(new ItemStack(Items.ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("&eGo Back")))
                .onClick(b -> {
                    UIManager.openUIForcefully(b.getPlayer(), mainGUI());
                })
                .build();

        builder.set(0, 3, previous)
                .set(0, 5, next)
                .set(0, 0, back)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), builderTypeButtonList(), LinkedPage.builder().template(builder.build()));
    }


    public LinkedPage mainGUI()
    {

        ChestTemplate.Builder builder = ChestTemplate.builder(5);
        builder.fill(filler());

        PlaceholderButton placeHolderButton = new PlaceholderButton();
        LinkedPageButton previous = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("Previous Page")))
                .linkType(LinkType.Previous)
                .build();

        LinkedPageButton next = LinkedPageButton.builder()
                .display(new ItemStack(Items.SPECTRAL_ARROW))
                .with(DataComponents.CUSTOM_NAME, Component.literal(Util.formattedString("Next Page")))
                .linkType(LinkType.Next)
                .build();


        builder.set(0, 3, previous)
                .set(0, 5, next)
                .rectangle(1, 1, 3, 7, placeHolderButton);

        return PaginationHelper.createPagesFromPlaceholders(builder.build(), partyMemberButtonList(), LinkedPage.builder().template(builder.build()));
    }
}
