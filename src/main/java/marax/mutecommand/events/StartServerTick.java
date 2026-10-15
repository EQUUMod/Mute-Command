package marax.mutecommand.events;

import marax.mutecommand.Colors;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static marax.mutecommand.MuteCommandMod.STORE;

public class StartServerTick {
    public static void register () {
        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            List<String> muteList = STORE.muteList();

            for (String playerName : muteList) {
                long playerTimestamp = STORE.muteTime(playerName);

                if (System.currentTimeMillis() > playerTimestamp) {
                    STORE.unmute(playerName);

                    Player player = server.getPlayerList().getPlayer(playerName);

                    if (player != null)
                        player.sendSystemMessage(Component.literal("Your mute has been expired").withColor(Colors.TRUE));
                }
            }
        });
    }
}
