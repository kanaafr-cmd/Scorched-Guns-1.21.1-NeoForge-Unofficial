package top.ribs.scguns.enchantment;

public final class CorrodedEnchantment {
    private CorrodedEnchantment() {
    }

    public static float getBotDamageBonus(int level) {
        return level * 2.5F;
    }
}
