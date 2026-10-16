package marax.mutecommand;

import marax.mutecommand.commands.AdvancedCommands;
import marax.mutecommand.commands.CommandRegistry;
import marax.mutecommand.events.EventRegistries;
import marax.mutecommand.commands.CommandRegistries;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class MuteCommandMod implements ModInitializer {
    public MuteCommandMod() throws IOException {}

    public static final String MOD_ID = "mute-command";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static SqlStore STORE;
    public Connection connection;

    private static void registerCommands() {
        for (CommandRegistry command : CommandRegistries.commands) {
            command.register();
        }
        AdvancedCommands.register();
    }

    public static void runVanillaCommand(MinecraftServer server, String cmd) throws CommandSyntaxException {
        CommandSourceStack source = server.createCommandSourceStack();
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();

        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(cmd, source);
        dispatcher.execute(parseResults);

    }

    @Override
    public void onInitialize() {
        LOGGER.info(
"""
         
         ____  __ _   _ _____ _____  ____ ___  __  __ __  __    _    _   _ ____    ████████████████████████████████████
        / |  \\/  | | | |_   _| ____ / ___/ _ \\|  \\/  |  \\/  |  / \\  | \\ | |  _ \\   ██ Developed by EQUUMod ████████████
       / /| |\\/| | | | | | | |  _| | |  | | | | |\\/| | |\\/| | / _ \\ |  \\| | | | |  ████████████████████████████████████
      / / | |  | | |_| | | | | |___| |__| |_| | |  | | |  | |/ ___ \\| |\\  | |_| |  ██ For the Artisans Minecraft SMP ██
     /_/  |_|  |_|\\___/  |_| |_____ \\____\\___/|_|  |_|_|  |_/_/   \\_|_| \\_|____/   ████████████████████████████████████
""");

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:data.sqlite");
        } catch (Exception e) {
            e.printStackTrace();
        }

        STORE = new SqlStore(connection);
        this.registerCommands();
        EventRegistries.register();
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
