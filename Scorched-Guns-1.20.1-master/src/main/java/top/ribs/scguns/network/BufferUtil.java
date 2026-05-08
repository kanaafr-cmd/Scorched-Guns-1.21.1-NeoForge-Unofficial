package top.ribs.scguns.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/**
 * Author: MrCrayfish
 */
public class BufferUtil
{
    public static void writeVec3(FriendlyByteBuf buffer, Vec3 vec)
    {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }

    public static Vec3 readVec3(FriendlyByteBuf buffer)
    {
        return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public static void writeItemStackToBufIgnoreTag(FriendlyByteBuf buffer, ItemStack stack) {
        if (stack.isEmpty()) {
            buffer.writeShort(-1);
            return;
        }
        buffer.writeShort(Item.getId(stack.getItem()));
        buffer.writeByte(stack.getCount());
    }

    public static ItemStack readItemStackFromBufIgnoreTag(FriendlyByteBuf buffer) {
        int id = buffer.readShort();
        if (id < 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Item.byId(id), buffer.readByte());
    }
}
