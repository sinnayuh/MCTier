# MCTier Plugin

A Minecraft plugin that integrates with MCTiers.com to display player ranking information directly in-game. This plugin provides an elegant way to view player tier data across various game modes with an interactive chat interface.

## Features

- **Real-time Data**: Fetches player data directly from MCTiers.com API
- **Interactive Display**: Shows tier information with hover events for detailed stats
- **Multiple Game Modes**: Supports all MCTiers game modes:
  - Vanilla
  - Sword
  - UHC
  - Pot
  - Netherite Pot
  - SMP
  - Axe

## Commands

- `/tier <player>` or `/mctier <player>` - Displays tier information for the specified player

## Display Information

The plugin displays:
- Player name (with crown icon for players holding tier positions)
- Region
- Points
- Overall ranking
- Individual game mode rankings with:
  - Current tier
  - Badges earned
  - Retirement status
  - Achievement dates

### Tier Display Format
- HT1: High Tier 1 (Red)
- LT2: Low Tier 2 (Light Purple)
- LT3: Low Tier 3 (Blue)
- LT4: Low Tier 4 (Green)

### Visual Indicators
- Game mode icons for easy identification
- Colored tier indicators
- Crown icon (🜲) for players holding tier positions
- Hover text showing detailed information

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server or load the plugin

## Examples

When using `/tier <player>`, the output will look like:
```
------------------------------------
Tierlist Data for PlayerName
Region: NA | Points: 300 | Overall: #1
------------------------------------
🗡 Sword: LT2
🏹 UHC: LT2
⚗ Pot: HT1
🧪 Netherite Pot: HT1
🛡 SMP: HT1
🎣 Vanilla: HT1
🪓 Axe: LT4
------------------------------------
```

## Support

For any issues or feature requests, please open an issue on the repository.