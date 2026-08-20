# Structra

Async, time-based schematic loader and saver for Paper.

[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg)](https://modrinth.com/plugin/structra)
[![discord](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://discord.gg/dN6RUzZGgJ)

> [!NOTE]
> Structra is not a WorldEdit addon. It uses a standalone, asynchronous, time-based block queue to prevent server lag.

### Commands

| Command | Description |
| :--- | :--- |
| `/structra tool` | Wand item for pos1 (L-click) and pos2 (R-click) |
| `/structra pos1 [x y z world]` | Sets selection position 1 |
| `/structra pos2 [x y z world]` | Sets selection position 2 |
| `/structra write <name> [batchSize] [x y z world]` | Saves the selected region |
| `/structra paste <name> [batchSize] [x y z world]` | Pastes the structure |
| `/structra pasteHistory <name> [batchSize]` | Pastes from history |
| `/structra delete <name>` | Deletes saved structure |

$\color{lightgreen}{\textsf{ batchSize (optional): Number of blocks processed per period (default: 50). }}$

### Media
<p align="center">
  <img src="https://i.imgur.com/dXgtOyu.png" width="48%" />
  <img src="https://i.imgur.com/rhnjH0B.png" width="48%" />
  <img src="https://i.imgur.com/veMBV2e.png" width="48%" />
  <img src="https://i.imgur.com/JhknzdU.png" width="48%" />
</p>

### License
[GPL-3.0](LICENSE)