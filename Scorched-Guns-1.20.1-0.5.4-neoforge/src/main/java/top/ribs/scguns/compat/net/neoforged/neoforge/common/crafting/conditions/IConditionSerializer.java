package top.ribs.scguns.compat.net.neoforged.neoforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public interface IConditionSerializer<T extends ICondition> {
    void write(JsonObject json, T value);

    T read(JsonObject json);

    ResourceLocation getID();
}
