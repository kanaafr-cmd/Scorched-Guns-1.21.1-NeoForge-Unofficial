package top.ribs.scguns.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AnthralitePaxelItem extends TieredItem {
    private final Tier tier;

    public AnthralitePaxelItem(Tier tier, Item.Properties properties) {
        super(tier, properties.attributes(DiggerItem.createAttributes(tier, 4.5F, -3.05F)));
        this.tier = tier;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState blockstate) {
        return !blockstate.is(this.tier.getIncorrectBlocksForDrops()) &&
                (blockstate.is(BlockTags.MINEABLE_WITH_AXE) ||
                        blockstate.is(BlockTags.MINEABLE_WITH_HOE) ||
                        blockstate.is(BlockTags.MINEABLE_WITH_PICKAXE) ||
                        blockstate.is(BlockTags.MINEABLE_WITH_SHOVEL));
    }

    @Override
    public float getDestroySpeed(ItemStack itemstack, BlockState blockstate) {
        return this.tier.getSpeed();
    }

    @Override
    public boolean mineBlock(ItemStack itemstack, Level world, BlockState blockstate, BlockPos pos, LivingEntity entity) {
        itemstack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
        itemstack.hurtAndBreak(2, sourceentity, EquipmentSlot.MAINHAND);
    }
}
