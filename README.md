> # Structra
> Time-based schematic loader and writer plugin.

> [!IMPORTANT]
> This is not an addon for WorldEdit. It uses entirely different system.

> ## Features
> - ⚡ Fast and lightweight, more importantly **time-based** schematic loading and writing.
> - 🧩 Provides easy to use API for developers.

> ## Installation
> 1. Download the latest release from [Releases](https://github.com/desaxxx/Structra/releases).
> 2. Place the `Structra-x.x.x` into your server's plugins folder.
> 3. Restart your server.

> ## Usage
> - `/structra tool`: Gives you the Structra tool item.
>   - **Left-click** with the tool: Sets **Position 1** at the clicked block.
>   - **Right-click** with the tool: Sets **Position 2** at the clicked block.
>
> - `/structra pos1`: Sets **Position 1** to the **block you are looking at** (within **10 blocks**).
>
> - `/structra pos2`: Sets **Position 2** to the **block you are looking at** (within **10 blocks**).
>
> - `/structra save <fileName> [<batchSize>]`: Saves the selected region as a structure.
>   - `fileName`: The name for your structra file.
>   - `batchSize` (optional): Number of blocks processed per time (default: 50).
>
> - `/structra load <fileName> [<batchSize>]`: Load a structure at your current location.
>   - `fileName`: The name for your structra file to load.
>   - `batchSize` (optional): Number of blocks processed per time (default: 50).

> ## Compatibility
> - Paper 1.17 or newer.
> - Java 16 or higher.
> - No dependencies required.

> ## Support / Donate
> Your support helps keep this project active. You can donate using these platforms:
>
> [![Buy Me a Coffee](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/buymeacoffee-plural_vector.svg)]() <br>
> [![Patreon](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/patreon-plural_vector.svg)]() <br>
> [![Ko-fi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/kofi-plural_vector.svg)]()

> ## Contributing
> Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

> ## Contact
> For support and questions, please join our **Discord servers**.
> 
> |                                                                                                                                                           |                  |
> |:---------------------------------------------------------------------------------------------------------------------------------------------------------:|:-----------------|
> | [![Desa Project](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://discord.gg/dN6RUzZGgJ) | **Desa Project** |
> |    [![Nesoi](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://discord.gg/qcW6YrxwqJ)     | **Nesoi**        |

> ## License
> This plugin is licensed under __[GNU GENERAL PUBLIC LICENSE](LICENSE)__ (GPLv3)
