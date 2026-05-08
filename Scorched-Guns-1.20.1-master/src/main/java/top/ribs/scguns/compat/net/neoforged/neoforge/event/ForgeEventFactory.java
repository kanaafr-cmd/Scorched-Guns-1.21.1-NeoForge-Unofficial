package top.ribs.scguns.compat.net.neoforged.neoforge.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

import java.util.List;

public final class ForgeEventFactory {
    private ForgeEventFactory() {
    }

    public static boolean onAnimalTame(LivingEntity animal, Player player) {
        return false;
    }

    public static boolean onExplosionStart(Level level, Explosion explosion) {
        return false;
    }

    public static void onExplosionDetonate(Level level, Explosion explosion, List<Entity> entities, double diameter) {
    }
}
