package top.ribs.scguns.common;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.ribs.scguns.client.util.Easings;

public class JsonDeserializers {
    public static final JsonDeserializer<ItemStack> ITEM_STACK = (json, typeOfT, context) -> {
        JsonObject object = json.getAsJsonObject();
        ResourceLocation itemId = ResourceLocation.parse(object.get("item").getAsString());
        Item item = BuiltInRegistries.ITEM.get(itemId);
        int count = object.has("count") ? object.get("count").getAsInt() : 1;
        return new ItemStack(item, count);
    };
    public static final JsonDeserializer<ResourceLocation> RESOURCE_LOCATION = (json, typeOfT, context) -> ResourceLocation.parse(json.getAsString());
    public static final JsonDeserializer<FireMode> FIRE_MODE = (json, typeOfT, context) -> FireMode.getType(ResourceLocation.tryParse(json.getAsString()));
    public static final JsonDeserializer<ReloadType> RELOAD_TYPE = (json, typeOfT, context) -> ReloadType.getType(ResourceLocation.tryParse(json.getAsString()));
    public static final JsonDeserializer<GripType> GRIP_TYPE = (json, typeOfT, context) -> GripType.getType(ResourceLocation.tryParse(json.getAsString()));
    public static final JsonDeserializer<Easings> EASING = (json, typeOfT, context) -> Easings.byName(json.getAsString());
}
