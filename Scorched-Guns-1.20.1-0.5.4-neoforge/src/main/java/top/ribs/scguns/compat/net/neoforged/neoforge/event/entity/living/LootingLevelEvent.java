package top.ribs.scguns.compat.net.neoforged.neoforge.event.entity.living;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.Event;

public class LootingLevelEvent extends Event {
    private final LivingEntity entity;
    private final DamageSource damageSource;
    private int lootingLevel;

    public LootingLevelEvent(LivingEntity entity, DamageSource damageSource, int lootingLevel) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.lootingLevel = lootingLevel;
    }

    public LivingEntity getEntity() {
        return this.entity;
    }

    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public int getLootingLevel() {
        return this.lootingLevel;
    }

    public void setLootingLevel(int lootingLevel) {
        this.lootingLevel = lootingLevel;
    }
}
