package dev.codex.architecturyfix;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

@Mod(ArchitecturyDsurroundModListFix.MOD_ID)
public final class ArchitecturyDsurroundModListFix {
    public static final String MOD_ID = "architectury_dsurround_modlist_fix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ArchitecturyDsurroundModListFix() {
        LOGGER.info("Loaded {}.", MOD_ID);
    }
}
