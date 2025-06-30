package io.github.adainish.argentumpokebuilderforge.cmd;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.adainish.argentumpokebuilderforge.ArgentumPokeBuilderForge;
import io.github.adainish.argentumpokebuilderforge.obj.Player;
import io.github.adainish.argentumpokebuilderforge.obj.PokeBuilder;
import io.github.adainish.argentumpokebuilderforge.storage.PlayerStorage;
import io.github.adainish.argentumpokebuilderforge.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;


public class Command
{
    public static LiteralArgumentBuilder<CommandSourceStack> getCommand() {
        return Commands.literal("pokebuilder")
                .executes(cc -> {
                    try {
                        Player player = ArgentumPokeBuilderForge.playerStorage.getPlayer(cc.getSource().getPlayerOrException().getUUID());
                        if (player != null) {
                            PokeBuilder pokeBuilder = new PokeBuilder(player);
                            //open pokebuilder GUI
                            pokeBuilder.open(cc.getSource().getPlayerOrException());
                        } else {
                            Util.send(cc.getSource(), "&cUnable to load your pokebuilder data...");
                        }
                    } catch (Exception e) {
                        Util.send(cc.getSource(), "&cAn error occurred while trying to open the Pokebuilder GUI. Please check the console for details.");
                        ArgentumPokeBuilderForge.getLog().error("Error while executing pokebuilder command", e);
                    }
                    return 1;
                })
                .then(Commands.literal("reload")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            ArgentumPokeBuilderForge.instance.reload();
                            Util.send(cc.getSource(), "&eReloaded argentum pokebuilder, please check the console for any errors.");
                            return 1;
                        })
                )
                .then(Commands.literal("give")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&ePlease provide a valid player and amount");
                            return 1;
                        })
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                                .executes(cc -> {
                                    Util.send(cc.getSource(), "&ePlease provide a valid amount");
                                    return 1;
                                }).then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                                        .executes(cc -> {
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            Player player = ArgentumPokeBuilderForge.playerStorage.getPlayer(serverPlayer.getUUID());
                                            int amount = IntegerArgumentType.getInteger(cc, "amount");
                                            if (player != null) {
                                                player.tokenCount += amount;
                                                player.sendMessage("&aYou received %tokens% tokens, you now have %amount% tokens"
                                                        .replace("%tokens%", String.valueOf(amount))
                                                        .replace("%amount%", String.valueOf(player.tokenCount))
                                                );
                                                Util.send(cc.getSource(), "&eAdded %amount% tokens to %player%, their new token balance is %balance%"
                                                        .replace("%amount%", String.valueOf(amount))
                                                        .replace("%player%", serverPlayer.getName().getString())
                                                        .replace("%balance%", String.valueOf(player.tokenCount))
                                                );
                                                player.save();
                                            } else {
                                                Util.send(cc.getSource(), "&cUnable to load provided pokebuilder data...");
                                            }
                                            return 1;
                                        })
                                )
                        )

                )
                .then(Commands.literal("take")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                        .executes(cc -> {
                            Util.send(cc.getSource(), "&ePlease provide a valid player and amount");
                            return 1;
                        })
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                                .executes(cc -> {
                                    Util.send(cc.getSource(), "&ePlease provide a valid amount");
                                    return 1;
                                }).then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                                        .executes(cc -> {
                                            ServerPlayer serverPlayer = EntityArgument.getPlayer(cc, "player");
                                            Player player = ArgentumPokeBuilderForge.playerStorage.getPlayer(serverPlayer.getUUID());
                                            int amount = IntegerArgumentType.getInteger(cc, "amount");
                                            if (player != null) {
                                                player.tokenCount -= amount;
                                                player.sendMessage("&aYou had %tokens% tokens taken by an administrator, you now have %amount% tokens"
                                                        .replace("%tokens%", String.valueOf(amount))
                                                        .replace("%amount%", String.valueOf(player.tokenCount))
                                                );
                                                Util.send(cc.getSource(), "&eRemoved %amount% tokens from %player%, their new token balance is %balance%"
                                                        .replace("%amount%", String.valueOf(amount))
                                                        .replace("%player%", serverPlayer.getName().getString())
                                                        .replace("%balance%", String.valueOf(player.tokenCount))
                                                );
                                                player.save();
                                            } else {
                                                Util.send(cc.getSource(), "&cUnable to load provided pokebuilder data...");
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("tokens")
                        .executes(cc -> {
                            Player player = ArgentumPokeBuilderForge.playerStorage.getPlayer(cc.getSource().getPlayerOrException().getUUID());
                            if (player != null) {
                                player.sendMessage("&7You have %amount% tokens".replace("%amount%", String.valueOf(player.tokenCount)));
                            } else {
                                Util.send(cc.getSource(), "&cUnable to load your pokebuilder data...");
                            }
                            return 1;
                        })
                )
                ;

    }
}
