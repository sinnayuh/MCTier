package codes.sinister.mctier.commands;

import codes.sinister.mctier.MCTier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"mctv", "tvers", "tver", "mctvers", "mctver"})
@CommandPermission("mctier.version")
public record VerCommand() {
    @DefaultFor({"mctv", "tvers", "tver", "mctvers", "mctver"})
    public void version(Player sender) {
        String version = MCTier.getInstance().plugin.getDescription().getVersion();
        String author = MCTier.getInstance().plugin.getDescription().getAuthors().get(0);
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));
        sender.sendMessage(Component.text("MCTier", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("Version: ", NamedTextColor.GRAY).append(Component.text(version, NamedTextColor.GREEN)));
        sender.sendMessage(Component.text("Author: ", NamedTextColor.GRAY).append(Component.text(author, NamedTextColor.GREEN)));
        sender.sendMessage(Component.text("------------------------------------", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));
    }
}