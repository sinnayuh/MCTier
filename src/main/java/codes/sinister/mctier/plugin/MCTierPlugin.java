package codes.sinister.mctier.plugin;

import codes.sinister.mctier.MCTier;
import org.jetbrains.annotations.Nullable;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class MCTierPlugin extends JavaPlugin implements Listener {
    private @Nullable MCTier mctier;

    @Override
    public void onEnable() {
        mctier = new MCTier(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Optional.ofNullable(mctier).ifPresent(MCTier::disable);
    }
}
