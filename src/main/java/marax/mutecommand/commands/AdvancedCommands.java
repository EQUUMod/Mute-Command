package marax.mutecommand.commands;

import static marax.mutecommand.Constants.*;

import marax.mutecommand.MuteCommandMod;
import marax.mutecommand.Colors;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.network.chat.Component;

import net.minecraft.world.entity.player.Player;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;

import org.apache.commons.lang3.ArrayUtils;

public class AdvancedCommands {
    private static void mute () {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                Commands.literal("mute")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("time", IntegerArgumentType.integer())
                            .suggests(((context, builder) -> (builder.suggest(1)).buildFuture() ))
                            .then(Commands.argument("timeunit", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (String suggestion : TIMEUNITS) {
                                        builder.suggest(suggestion);
                                    }

                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("description", StringArgumentType.string())
                                    .suggests((context, builder) -> (builder.suggest("\"\"")).buildFuture() )
                                    .executes((context -> {
                                        Player player = EntityArgument.getPlayer(context, "player");
                                        String playerName = player.getName().getString();
                                        String muterName = context.getSource().getTextName();
                                        int time = IntegerArgumentType.getInteger(context, "time");
                                        String timeUnit = StringArgumentType.getString(context, "timeunit");
                                        String description = StringArgumentType.getString(context, "description");

                                        if (time < 1) {
                                            context.getSource().sendFailure(Component.literal("Time argument must be an integer higher than 0"));
                                            return 1;
                                        }

                                        int timeUnitIndex = ArrayUtils.indexOf(TIMEUNITS, timeUnit);

                                        if (timeUnitIndex == -1) {
                                            context.getSource().sendFailure(Component.literal("Time unit has not been recognized. Use one in the following list: " + String.join(", ", TIMEUNITS)));
                                            return 1;
                                        }

                                        if (MuteCommandMod.STORE.muteTime(playerName) > -1)
                                            context.getSource().sendSuccess(() -> Component.literal(playerName + " is already muted!").withColor(Colors.FALSE), false);
                                        else {
                                            long timestamp = System.currentTimeMillis() + ((long) time * TIMEUNITS_TOSECONDS[timeUnitIndex] * 1000);

                                            MuteCommandMod.STORE.addMute(playerName, timestamp);

                                            player.sendSystemMessage(Component.literal("You have been muted by " + muterName + " for " + time + " " + timeUnit + (description.isEmpty() ? "" : (" for the following reason: " + description))).withColor(Colors.FALSE));
                                            context.getSource().sendSuccess(() -> Component.literal("Muted " + playerName + " for " + time + " "+ timeUnit).withColor(Colors.TRUE), false);
                                        }

                                        return 1;
                                    })
                                )
                            )
                        )
                    )
                )
            );
        });
    }

    private static void unmute () {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                Commands.literal("unmute")
                    .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes((context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String playerName = player.getName().getString();
                            String unmuterName = context.getSource().getTextName();

                            if (MuteCommandMod.STORE.muteTime(playerName) > -1) {
                                MuteCommandMod.STORE.unmute(playerName);

                                player.sendSystemMessage(Component.literal("You have been unmuted by " + unmuterName).withColor(Colors.TRUE));
                                context.getSource().sendSuccess(() -> Component.literal("Unmuted " + playerName).withColor(Colors.TRUE), false);
                            } else {
                                context.getSource().sendSuccess(() -> Component.literal(playerName + " is not muted").withColor(Colors.FALSE), false);
                            }
                            return 1;
                        }))
                    )
            );
        });
    }

    private static void ismute () {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                Commands.literal("ismute")
                    .executes((context -> {
                        String playerName = context.getSource().getTextName();

                        return sendIsmute(context, playerName);
                    }))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes((context -> {
                            Player player = EntityArgument.getPlayer(context, "player");
                            String playerName = player.getName().getString();

                            return sendIsmute(context, playerName);
                        }))
                    )
            );
        });
    }

    private static int sendIsmute (CommandContext<CommandSourceStack> context, String playerName) {
        boolean isMute = MuteCommandMod.STORE.muteTime(playerName) > -1;

        String text = playerName + " is " + (!isMute ? "not " : "") + "muted";
        context.getSource().sendSuccess(() -> Component.literal(text).withColor(isMute ? Colors.FALSE : Colors.TRUE), false);
        return 1;
    }

    public static void register () {
        mute();
        unmute();
        ismute();
    }
}

