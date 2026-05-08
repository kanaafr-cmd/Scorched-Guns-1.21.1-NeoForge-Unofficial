package top.ribs.scguns.item;

import top.ribs.scguns.item.attachment.IBarrel;
import top.ribs.scguns.item.attachment.impl.Barrel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * A basic barrel attachment item implementation with color support
 *
 * Author: MrCrayfish
 */
public class BarrelItem extends AttachmentItem implements IBarrel, IColored
{
    private final Barrel barrel;
    private final boolean colored;

    public BarrelItem(Barrel barrel, Item.Properties properties)
    {
        super(properties);
        this.barrel = barrel;
        this.colored = true;
    }

    public BarrelItem(Barrel barrel, Item.Properties properties, boolean colored)
    {
        super(properties);
        this.barrel = barrel;
        this.colored = colored;
    }

    @Override
    public Barrel getProperties()
    {
        return this.barrel;
    }

    @Override
    public boolean canColor(ItemStack stack)
    {
        return this.colored;
    }
}
