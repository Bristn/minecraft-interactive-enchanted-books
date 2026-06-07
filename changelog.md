# Changelog for version 1.0.2

- Make particle emission based on the book count of nearby chiseled bookshelves
  - If there are no books in nearby chiseled bookshelves, no particles will be emitted
  - If there is at least one book, particles will be emitted
  - With each additional book, the particles lifetime is increased
- Update comparator signal strengths
  - New enchantment grouping fills the spectrum of signal strengths more thoroughly while also (hopefully) being future proof
- Add unique particle textures for each enchantment
- Add Enchantment Echo item to allow cloning of enchantments
  - Combining an enchanted item with a book creates a Enchantment Echo without destroying the enchanted item
  - Combining the Enchantment Echo with an Echo Shard crafts a Enchanted Book
  - Enchantment Echos can be used in place of Enchanted Books in the other mod functions
- Add info buttons & panel in the book screen
  - These panels show the particle requirements and the cloning of Enchanted Books using Enchantment Echos
- Update tooltip of Enchanted Books to indicate that they are openable

## Changes fo integrating custom enchantments

- To realize the lifetime of particles based on the book count in nearby chiseled bookshelves, this book count is encoded in the z velocity of the particle
  - To decode the book count and get the correct z velocity refer to [EnchantParticle](./src/client/java/net/bristn/interactive_enchanted_books/particle/EnchantParticle.java)
