package dev.codex.creatingspacefix;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

@Mod(CreatingSpaceConfigLoadFix.MOD_ID)
public final class CreatingSpaceConfigLoadFix {
    public static final String MOD_ID = "creating_space_config_load_fix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CreatingSpaceConfigLoadFix() {
        LOGGER.info("Loaded {}.", MOD_ID);
    }
}
