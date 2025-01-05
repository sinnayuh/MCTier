package codes.sinister.mctier.commands;

import codes.sinister.mctier.MCTier;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@Command({"tier", "mctier"})
public class TierCommand {
    private static final String API_URL = "https://mctiers.com/api/search_profile/%s";
    private static final Gson GSON = new Gson();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final String[] MODES = {
            "vanilla",
            "sword",
            "uhc",
            "pot",
            "neth_pot",
            "smp",
            "axe"
    };

    @DefaultFor({"tier", "mctier"})
    public void checkTier(Player sender, String target) {
        sender.sendMessage(Component.text("Fetching profile statistics...", NamedTextColor.RED));

        Bukkit.getScheduler().runTaskAsynchronously(MCTier.getInstance().plugin, () -> {
            try {
                String jsonResponse = fetchData(String.format(API_URL, target));
                JsonObject profile = GSON.fromJson(jsonResponse, JsonObject.class);

                sender.sendMessage(Component.text("------------------------------------", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));

                JsonArray badges = profile.getAsJsonArray("badges");
                boolean hasCrownBadge = false;
                String crownDesc = "";
                for (int i = 0; i < badges.size(); i++) {
                    JsonObject badge = badges.get(i).getAsJsonObject();
                    String title = badge.get("title").getAsString();
                    if (title.equals("Holding The Crown")) {
                        hasCrownBadge = true;
                        crownDesc = badge.get("desc").getAsString();
                        break;
                    }
                }

                Component nameComponent = Component.text(profile.get("name").getAsString(), NamedTextColor.GREEN);
                if (hasCrownBadge) {
                    nameComponent = nameComponent.append(Component.text(" 🜲", NamedTextColor.GOLD))
                            .hoverEvent(HoverEvent.showText(Component.text()
                                    .append(Component.text("Holding The Crown\n", NamedTextColor.GOLD))
                                    .append(Component.text(crownDesc, NamedTextColor.GRAY))
                                    .build()));
                }

                sender.sendMessage(Component.text("Tierlist Data for ", NamedTextColor.GRAY)
                        .append(nameComponent));

                sender.sendMessage(Component.text("Region: ", NamedTextColor.GRAY)
                        .append(Component.text(profile.get("region").getAsString(), NamedTextColor.GREEN))
                        .append(Component.text(" | ", NamedTextColor.GRAY))
                        .append(Component.text("Points: ", NamedTextColor.GRAY))
                        .append(Component.text(profile.get("points").getAsString(), NamedTextColor.GREEN))
                        .append(Component.text(" | ", NamedTextColor.GRAY))
                        .append(Component.text("Overall: #", NamedTextColor.GRAY))
                        .append(Component.text(profile.get("overall").getAsString(), NamedTextColor.GREEN)));

                sender.sendMessage(Component.text("------------------------------------", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));

                JsonObject rankings = profile.getAsJsonObject("rankings");

                for (String mode : MODES) {
                    if (rankings.has(mode)) {
                        JsonObject ranking = rankings.getAsJsonObject(mode);
                        TextComponent modeComponent = formatGameMode(mode, ranking, badges);
                        sender.sendMessage(modeComponent);
                    }
                }

                sender.sendMessage(Component.text("------------------------------------", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));

            } catch (Exception e) {
                sender.sendMessage(Component.text("Error fetching " + target + "'s MCTiers data!", NamedTextColor.RED));
            }
        });
    }

    private String getTierColorCode(int tier) {
        return switch (tier) {
            case 1 -> "§c";
            case 2 -> "§d";
            case 3 -> "§9";
            case 4 -> "§a";
            default -> "§7";
        };
    }

    private static final String getGameModeIcon(String mode) {
        return switch (mode) {
            case "neth_pot" -> "🧪 ";
            case "pot" -> "⚗ ";
            case "axe" -> "🪓 ";
            case "smp" -> "🛡 ";
            case "vanilla" -> "🎣 ";
            case "sword" -> "🗡 ";
            case "uhc" -> "🏹 ";
            default -> "";
        };
    }

    private TextComponent formatGameMode(String mode, JsonObject ranking, JsonArray badges) {
        String displayName = formatModeName(mode);

        if (ranking == null) {
            return Component.text(displayName + ": ", NamedTextColor.GRAY)
                    .append(Component.text("Unranked", NamedTextColor.GRAY));
        }

        int tier = ranking.get("tier").getAsInt();
        int peakTier = ranking.has("peak_tier") && !ranking.get("peak_tier").isJsonNull()
                ? ranking.get("peak_tier").getAsInt()
                : tier;
        boolean retired = ranking.get("retired").getAsBoolean();
        long attained = ranking.get("attained").getAsLong();

        JsonObject highestBadge = null;
        int highestTier = 0;

        for (int i = 0; i < badges.size(); i++) {
            JsonObject badge = badges.get(i).getAsJsonObject();
            String title = badge.get("title").getAsString().toLowerCase();
            String desc = badge.get("desc").getAsString();

            String modeSearch = mode.equals("neth_pot") ? "netherite pot" : mode.replace("_", " ");
            boolean isForThisMode = title.contains(modeSearch) &&
                    desc.toLowerCase().contains(modeSearch);

            if (isForThisMode) {
                int badgeTier = title.contains("champion") ? 1 : 2;
                if (highestBadge == null || badgeTier < highestTier) {
                    highestBadge = badge;
                    highestTier = badgeTier;
                }
            }
        }

        StringBuilder badgeText = new StringBuilder();
        if (highestBadge != null) {
            String title = highestBadge.get("title").getAsString();
            String desc = highestBadge.get("desc").getAsString();

            title = capitalizeWords(title);

            title = title.replace("Smp", "SMP").replace("Uhc", "UHC");
            desc = desc.replace("smp", "SMP").replace("uhc", "UHC");

            badgeText.append("§7").append(title).append("\n")
                    .append("§7").append(desc);
        }

        String formattedDate = DATE_FORMAT.format(new Date(attained * 1000L));

        String tierDisplay = getTierDisplay(tier);

        String hoverText = String.format(
                "%s§l%s §8§o(%s)\n" +
                        "%s\n" +
                        "§8Retired: %s",
                getTierColorCode(tier),
                getTierTitle(tier, mode),
                formattedDate,
                badgeText.toString().trim(),
                retired ? "§a✔" : "§c✗"
        );

        if (ranking == null) {
            return Component.text(displayName + ": ", NamedTextColor.GRAY)
                    .append(Component.text("Unranked", NamedTextColor.GRAY));
        }

        return Component.text(getGameModeIcon(mode), NamedTextColor.WHITE)
                .append(Component.text(displayName + ": ", NamedTextColor.GRAY))
                .append(Component.text(tierDisplay, getTierColor(tier))
                        .decoration(TextDecoration.BOLD, true))
                .hoverEvent(HoverEvent.showText(Component.text(hoverText)));
    }

    private NamedTextColor getTierColor(int tier) {
        return switch (tier) {
            case 1 -> NamedTextColor.RED;
            case 2 -> NamedTextColor.LIGHT_PURPLE;
            case 3 -> NamedTextColor.BLUE;
            case 4 -> NamedTextColor.GREEN;
            default -> NamedTextColor.GRAY;
        };
    }

    private String getTierDisplay(int tier) {
        return switch (tier) {
            case 1 -> "HT1";
            case 2 -> "LT2";
            case 3 -> "LT3";
            case 4 -> "LT4";
            default -> "T" + tier;
        };
    }

    private String getTierTitle(int tier, String mode) {
        String modeName = formatModeName(mode);
        return switch (tier) {
            case 1 -> "High Tier 1";
            case 2 -> "High Tier 2";
            case 3 -> "Low Tier 3";
            case 4 -> "Low Tier 4";
            default -> "Tier " + tier;
        };
    }

    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private String formatModeName(String mode) {
        if (mode.equals("uhc")) {
            return "UHC";
        }
        if (mode.equals("smp")) {
            return "SMP";
        }
        if (mode.equals("neth_pot")) {
            return "Netherite Pot";
        }

        String[] words = mode.split("_");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }

        return formatted.toString().trim();
    }

    private String fetchData(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }
}