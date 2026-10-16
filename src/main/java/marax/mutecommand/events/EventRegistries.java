package marax.mutecommand.events;

public interface EventRegistries {
    static void register() {
        AllowChatMessage.register();
        StartServerTick.register();
    }
}
