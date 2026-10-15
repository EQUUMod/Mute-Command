package marax.mutecommand.events;

import static marax.mutecommand.Constants.*;

import marax.mutecommand.MuteCommandMod;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class AllowChatMessage {
    public static void register() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, player, bound) -> {
            String playerName = player.getName().getString();
            ServerLevel playerDimension = player.level();
            MinecraftServer server = playerDimension.getServer();

            long muteTime = MuteCommandMod.STORE.muteTime(playerName);

            if (muteTime > -1) {
                String remainingTime = formatRemainingTime(muteTime - System.currentTimeMillis());

                try {
                    MuteCommandMod.runVanillaCommand(server, "title " + playerName + " actionbar {text: \"You can't send messages by being muted, remaining time: " + remainingTime + "\", color: red}");
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        });
    }

    private static String formatRemainingTime(long millis) {
        long totalSeconds = millis / 1000;

        if (totalSeconds <= 0)
            return "0 seconds";

        StringBuilder builder = new StringBuilder();

        for (int i = TIMEUNITS.length - 1; i >= 0; i--) {
            long amount = totalSeconds / TIMEUNITS_TOSECONDS[i];

            if (amount <= 0)
                continue;

            totalSeconds -= amount * TIMEUNITS_TOSECONDS[i];

            if (builder.length() > 0)
                builder.append(", ");

            String unit = TIMEUNITS[i];
            builder.append(amount).append(' ').append(amount == 1 ? unit.substring(0, unit.length() - 1) : unit);
        }

        return builder.toString();
    }
}
