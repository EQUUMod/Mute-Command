package marax.mutecommand.commands;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.function.Consumer;

public class CommandRegistry {
    private final String name;
    private final Consumer<CommandContext<CommandSourceStack>> execute;

    public CommandRegistry(String name, Consumer<CommandContext<CommandSourceStack>> execute) {
        this.name = name;
        this.execute = execute;
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                Commands
                    .literal(this.name)
                    .executes((context) -> {
                        this.execute.accept(context);
                        return 1;
                    })
            );
        });
    }
}