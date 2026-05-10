# Changelog for version 1.0.1

- Disable the hopper interactions with lecterns by default to match vanilla more closely
  - Can be enabled with `/gamerule interactive_enchanted_books:hopper_interacts_with_lectern true`
- Implement changing the currently active lectern page using a redstone pulse
  - Can be enabled with `/gamerule interactive_enchanted_books:signal_changes_lectern_page true`
  - Enabling this game rule will disable lecterns emitting a redstone pulse when changing pages. Observers will still trigger
    - This prevents endless loops where the lectern page would cycle through all pages indefinitely
- Fix the particles of the lectern not respecting the currently active page
  - Reason being, that the current page was not updated on the ClientLevel but just the ServerLevel
- Update mod icon
