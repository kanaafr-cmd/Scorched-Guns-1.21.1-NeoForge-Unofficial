package top.ribs.scguns.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import top.ribs.scguns.Reference;
import top.ribs.scguns.interfaces.IGunModifier;
import top.ribs.scguns.item.attachment.impl.UnderBarrel;

public class BayonetItem extends UnderBarrelItem {
    private final float attackDamage;
    private final float attackSpeed;
    private final IGunModifier modifier;

    public BayonetItem(UnderBarrel underBarrel, Properties properties, float attackDamage, float attackSpeed) {
        super(underBarrel, withAttributes(properties, attackDamage, attackSpeed));
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.modifier = underBarrel.getModifier();
    }

    public BayonetItem(UnderBarrel underBarrel, Properties properties, boolean colored, float attackDamage, float attackSpeed) {
        super(underBarrel, withAttributes(properties, attackDamage, attackSpeed), colored);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.modifier = underBarrel.getModifier();
    }

    public float getAdditionalDamage() {
        return this.modifier.additionalDamage();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        if (state.getDestroySpeed(world, pos) != 0.0F) {
            stack.hurtAndBreak(2, entityLiving, EquipmentSlot.MAINHAND);
        }
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public float getDamage() {
        return this.attackDamage;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return state.is(Blocks.COBWEB) ? 15.0F : 1.0F;
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState block) {
        return block.is(Blocks.COBWEB);
    }

    private static Properties withAttributes(Properties properties, float attackDamage, float attackSpeed) {
        return properties.attributes(ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "bayonet_attack_damage"), attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "bayonet_attack_speed"), attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build());
    }
}
