package codes.sinister.mctier;

import codes.sinister.mctier.commands.TierCommand;
import codes.sinister.mctier.commands.VerCommand;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class MCTier {
    public final @NotNull JavaPlugin plugin;
    private final @NotNull BukkitCommandHandler commandHandler;
    private BukkitAudiences adventure;

    private static MCTier mcTier;
    public static MCTier getInstance() {
        return mcTier;
    }

    public MCTier(@NotNull JavaPlugin plugin) {
        mcTier = this;

        this.plugin = plugin;
        this.commandHandler = BukkitCommandHandler.create(plugin);

        registerCommands();
        /*registerListeners();*/
    }

    private void registerCommands() {
        commandHandler.register(
                new TierCommand(),
                new VerCommand()
        );
    }

/*    private void registerListeners() {
        List.of(
        ).forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
    }*/

    public void disable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
