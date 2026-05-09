# Known Issues

## Loot Modifier Bug
Mobs, explosions, and block/fluid updates drop patterned random Scorched Guns items.

Likely cause:
- broken NeoForge 1.21.1 GlobalLootModifier migration
- old Forge loot condition IDs
- bad loot modifier JSONs

Focus:
- data/neoforge/loot_modifiers
- old forge:loot_table_id usage
- latest.log loot errors



Important systems:
- ReloadTracker
- ItemStackNbtHelper
- C2SMessageReload
- C2SMessageGunLoaded
- S2CMessageUpdateAmmo

## Resource Pack Compatibility
Fresh Moves:
- breaks first-person arms consistently

Fresh Animations:
- intermittent arm issues

This is resource-pack compatibility, not normal mod conflict.

## ExoSuit
Night vision/HUD modules sometimes say unavailable even when installed.

Investigate:
- ExoSuitUpgradeManager
- ExoSuitData
- ExoSuitPowerManager
- module detection logic
## Fresh Moves / Fresh Animations Compatibility

Fresh Moves breaks Scorched Guns first-person arms constantly.
Fresh Animations breaks them intermittently.

This should be fixed in:
- the-world-compat-patches

Do not patch this inside the Scorched Guns port unless absolutely unavoidable.

Likely areas:
- resource pack model/animation compatibility
- EMF/Fresh Animations player model handling
- first-person arm transform override
- Scorched Guns held item/arm render compatibility layer

Task:
- inspect existing patch mod mixins
- add client-only compatibility patch
- target Fresh Moves/Fresh Animations behavior only
- do not alter normal Scorched Guns behavior