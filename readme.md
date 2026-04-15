![](./doc/client_header.png)

# Interactive enchanted books

Using an enchanted book opens a screen showing information about the enchantments. This includes a description for each enchantment, the list of mutually exclusive enchantments, the comparator signal and the list of items the enchantment can be applied to.

![Preview when right-clicking with enchanted book](./doc/item_preview.gif)

![](./doc/server_header.png)

# Enchanted books in lecterns

If the mod is installed on the server, enchanted books may be placed in lecterns. Right-clicking the lectern will open the same interface as using the enchanted book itself.

![Preview when placing the enchanted book in a lectern](./doc/lectern_preview.gif)

## Comparator signal

The enchantments are separated into different groups, each emitting a different comparator signal strength. This signal strength is also shown when opening the books gui.

## Lectern block particles

If a lectern contains an enchanted book, the book will emit particles based on the enchantment level of the current page. Lower enchantment levels emit fewer particles compared to higher levels. Curses emit a different particle. When viewing the cover page, the different effects are mixed accordingly.

|                                                                                                                |                                                                                                                                            |
| -------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| ![Particle preview with unbreaking 1](./doc/particle_low.gif) <p style="text-align: center;">Unbreaking I</p>  | ![Particle preview with unbreaking 3](./doc/particle_high.gif) <p style="text-align: center;">Unbreaking III</p>                           |
| ![Particle preview with a curse](./doc/particle_curse.gif) <p style="text-align: center;">Curse of Binding</p> | ![Particle preview a curse and unbreaking 3](./doc/particle_both.gif) <p style="text-align: center;">Unbreaking III + Curse of Binding</p> |

# Hopper functionality for lecterns

Hoppers are able to input written or enchanted books into lecterns. Additionally they are able to remove the active book of a lectern.

![Preview of the hopper interactions](./doc/hopper_preview.gif)

![](./doc/integration_header.png)

See the [example repository](https://github.com/Bristn/minecraft-interactive-enchanted-books-example) for more details on how to integrate custom enchantments.
