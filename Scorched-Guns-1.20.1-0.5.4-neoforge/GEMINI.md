# Scorched Guns NeoForge 1.21.1 Port

This exact folder is the project. Do not inspect parent folders.

Rules:
- Use PowerShell commands only.
- Do not use grep/tail/Linux shell commands.
- Do not broadly scan the repository.
- Focus on one bug at a time.
- Build after every patch using:
  ./gradlew build -x test
- Stop after successful build and summarize changed files only.

Project state:
- NeoForge 1.21.1 unofficial Scorched Guns port
- Reload/rendering fixes partially attempted
- Recipe/tag migration partially attempted
- Third-person pose fixes partially attempted

Known compatibility:
- Fresh Moves breaks first-person arms
- Fresh Animations intermittently breaks first-person arms
- Resource pack issue, not general renderer conflict

Current priorities:
1. Loot modifier migration bug
2. Remaining reload edge cases
3. ExoSuit HUD/night vision detection
4. Remaining recipe/tag fixes
5. Third-person animation polish
6. Better Combat compatibility later
## Porting Rule

This NeoForge 1.21.1 port should preserve behavior from the original Scorched Guns 0.5.4 Forge 1.20.1 project whenever possible.

When investigating bugs:
1. compare against original Forge implementation
2. identify what changed during NeoForge migration
3. avoid inventing replacement behavior unless necessary
4. prefer restoring original intended logic

The original Forge project is the behavioral reference.
## Patch Mod Rule

Do not put modpack-specific compatibility hacks into the Scorched Guns port unless the bug exists in Scorched Guns by itself.

The Fresh Moves/Fresh Animations detached first-person arm issue is modpack/resource-pack compatibility.

Fix location:
- the-world-compat-patches

Not:
- Scorched Guns port

Goal:
- keep Scorched Guns as clean upstream port
- keep modpack-specific crash/glitch fixes inside the patch mod