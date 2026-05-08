package top.ribs.scguns.common;


import top.ribs.scguns.util.ItemStackNbtHelper;
import net.neoforged.fml.common.EventBusSubscriber;

import com.mrcrayfish.framework.api.network.LevelLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.ribs.scguns.compat.net.neoforged.neoforge.event.TickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import top.ribs.scguns.Config;
import top.ribs.scguns.Reference;
import top.ribs.scguns.attributes.SCAttributes;
import top.ribs.scguns.client.handler.ReloadHandler;
import top.ribs.scguns.common.exosuit.ExoSuitAmmoHelper;
import top.ribs.scguns.event.GunEventBus;
import top.ribs.scguns.init.ModSyncedDataKeys;
import top.ribs.scguns.item.AmmoBoxItem;
import top.ribs.scguns.item.GunItem;
import top.ribs.scguns.item.ammo_boxes.CreativeAmmoBoxItem;
import top.ribs.scguns.item.animated.AnimatedGunItem;
import top.ribs.scguns.network.PacketHandler;
import top.ribs.scguns.network.message.S2CMessageGunSound;
import top.ribs.scguns.network.message.S2CMessageStopReload;
import top.ribs.scguns.util.GunEnchantmentHelper;
import top.ribs.scguns.util.GunModifierHelper;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import static top.ribs.scguns.common.network.ServerPlayHandler.hasCreativeAmmoBoxInCurios;

/**
 * Author: MrCrayfish
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = Reference.MOD_ID)
public class ReloadTracker {
    private static final Map<Player, ReloadTracker> RELOAD_TRACKER_MAP = new WeakHashMap<>();

    private final int startTick;
    private final int slot;
    private final ItemStack stack;
    private final Gun gun;
    private final CompoundTag tag;

    public ReloadTracker(Player player) {
        this(player, ItemStackNbtHelper.getOrCreateTag(player.getMainHandItem()));
    }

    public ReloadTracker(Player player, CompoundTag tag) {
        this.startTick = player.tickCount;
        this.slot = player.getInventory().selected;
        this.stack = player.getInventory().getSelected();
        this.gun = ((GunItem) stack.getItem()).getModifiedGun(stack);
        this.tag = tag;
    }


    private void handleReloadByproduct(Player player) {
        if (this.gun.getReloads().getReloadType() != ReloadType.SINGLE_ITEM) {
            return;
        }

        if (this.gun.getReloads().shouldGiveByproduct(player.level().getRandom(), this.stack)) {
            Item byproduct = this.gun.getReloads().getReloadByproduct();
            if (byproduct != null) {
                ItemStack byproductStack = new ItemStack(byproduct);

                if (GunEventBus.addCasingDirectly(player, byproductStack)) {
                    return;
                }

                boolean added = player.getInventory().add(byproductStack);
                if (!added) {
                    Level level = player.level();
                    double x = player.getX();
                    double y = player.getY();
                    double z = player.getZ();
                    ItemEntity itemEntity = new ItemEntity(level, x, y, z, byproductStack);
                    itemEntity.setDeltaMovement(
                            level.random.nextDouble() * 0.2 - 0.1,
                            0.2,
                            level.random.nextDouble() * 0.2 - 0.1
                    );
                    level.addFreshEntity(itemEntity);
                }
            }
        }
    }

    public boolean isWeaponFull(Player player) {
        int currentAmmo = this.tag.getInt("AmmoCount");
        int maxAmmo = GunModifierHelper.getModifiedAmmoCapacity(this.stack, this.gun);
        return currentAmmo >= maxAmmo;
    }

    private boolean isWeaponEmpty() {
        return this.tag.getInt("AmmoCount") == 0;
    }

    public boolean hasNoAmmo(Player player) {
        boolean result;
        if (gun.getReloads().getReloadType() == ReloadType.SINGLE_ITEM) {
            result = Gun.findAmmo(player, this.gun.getReloads().getReloadItem()).stack().isEmpty();
        } else {
            result = Gun.findAmmo(player, this.gun.getProjectile().getItem()).stack().isEmpty();
        }
        return result;
    }

    private boolean canReload(Player player) {
        if (gun.getReloads().getReloadType() == ReloadType.MANUAL) {
            return true;
        }

        int deltaTicks = player.tickCount - this.startTick;
        double reloadSpeed = Objects.requireNonNull(player.getAttribute(SCAttributes.holder(SCAttributes.RELOAD_SPEED))).getValue();
        int interval = (gun.getReloads().getReloadType() == ReloadType.SINGLE_ITEM) ?
                (int) Math.ceil((double) GunEnchantmentHelper.getMagReloadSpeed(this.stack) / reloadSpeed) :
                (int) Math.ceil((double) GunEnchantmentHelper.getReloadInterval(this.stack) / reloadSpeed);
        return deltaTicks >= interval;
    }

    public static int ammoInInventory(ItemStack[] ammoStack) {
        int result = 0;
        for (ItemStack x : ammoStack) {
            result += x.getCount();
        }
        return result;
    }

    private void shrinkFromAmmoPool(ItemStack[] ammoStack, Player player, int shrinkAmount) {
        final int[] shrinkAmt = {shrinkAmount};

        int exoSuitShrinkAmount = Math.min(shrinkAmt[0], ExoSuitAmmoHelper.getAmmoCountInExoSuit(player, gun.getProjectile().getItem()));
        if (exoSuitShrinkAmount > 0) {
            ExoSuitAmmoHelper.shrinkAmmoInExoSuit(player, gun.getProjectile().getItem(), exoSuitShrinkAmount);
            shrinkAmt[0] -= exoSuitShrinkAmount;

            if (shrinkAmt[0] == 0) {
                return;
            }
        }

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if (stack.getItem() instanceof AmmoBoxItem) {
                    List<ItemStack> contents = AmmoBoxItem.getContents(stack).collect(Collectors.toList());
                    for (ItemStack pouchAmmoStack : contents) {
                        if (!pouchAmmoStack.isEmpty() && pouchAmmoStack.getItem() == gun.getProjectile().getItem()) {
                            int max = Math.min(shrinkAmt[0], pouchAmmoStack.getCount());
                            pouchAmmoStack.shrink(max);
                            shrinkAmt[0] -= max;
                            if (shrinkAmt[0] == 0) {
                                updateAmmoPouchContents(stack, contents);
                                return;
                            }
                        }
                    }
                    updateAmmoPouchContents(stack, contents);
                }
            }
        });

        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() instanceof AmmoBoxItem) {
                List<ItemStack> contents = AmmoBoxItem.getContents(itemStack).collect(Collectors.toList());
                for (ItemStack pouchAmmoStack : contents) {
                    if (!pouchAmmoStack.isEmpty() && pouchAmmoStack.getItem() == gun.getProjectile().getItem()) {
                        int max = Math.min(shrinkAmt[0], pouchAmmoStack.getCount());
                        pouchAmmoStack.shrink(max);
                        shrinkAmt[0] -= max;
                        if (shrinkAmt[0] == 0) {
                            updateAmmoPouchContents(itemStack, contents);
                            return;
                        }
                    }
                }
                updateAmmoPouchContents(itemStack, contents);
            }
        }

        for (ItemStack itemStack : ammoStack) {
            if (shrinkAmt[0] > 0 && !itemStack.isEmpty() && itemStack.getItem() == gun.getProjectile().getItem()) {
                int max = Math.min(shrinkAmt[0], itemStack.getCount());
                itemStack.shrink(max);
                shrinkAmt[0] -= max;
            }
        }
    }

    private void shrinkFromAmmoPool(ItemStack ammoStack, Player player, int shrinkAmount) {
        final int[] shrinkAmt = {shrinkAmount};

        int exoSuitShrinkAmount = Math.min(shrinkAmt[0], ExoSuitAmmoHelper.getAmmoCountInExoSuit(player, gun.getProjectile().getItem()));
        if (exoSuitShrinkAmount > 0) {
            ExoSuitAmmoHelper.shrinkAmmoInExoSuit(player, gun.getProjectile().getItem(), exoSuitShrinkAmount);
            shrinkAmt[0] -= exoSuitShrinkAmount;

            if (shrinkAmt[0] == 0) {
                return;
            }
        }

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            for (int i = 0; i < curios.getSlots(); i++) {
                ItemStack stack = curios.getStackInSlot(i);
                if (stack.getItem() instanceof AmmoBoxItem) {
                    List<ItemStack> contents = AmmoBoxItem.getContents(stack).collect(Collectors.toList());
                    for (ItemStack pouchAmmoStack : contents) {
                        if (!pouchAmmoStack.isEmpty() && pouchAmmoStack.getItem() == gun.getProjectile().getItem()) {
                            int max = Math.min(shrinkAmt[0], pouchAmmoStack.getCount());
                            pouchAmmoStack.shrink(max);
                            shrinkAmt[0] -= max;
                            if (shrinkAmt[0] == 0) {
                                updateAmmoPouchContents(stack, contents);
                                return;
                            }
                        }
                    }
                    updateAmmoPouchContents(stack, contents);
                }
            }
        });
        for (ItemStack itemStack : player.getInventory().items) {
            if (itemStack.getItem() instanceof AmmoBoxItem) {
                List<ItemStack> contents = AmmoBoxItem.getContents(itemStack).collect(Collectors.toList());
                for (ItemStack pouchAmmoStack : contents) {
                    if (!pouchAmmoStack.isEmpty() && pouchAmmoStack.getItem() == gun.getProjectile().getItem()) {
                        int max = Math.min(shrinkAmt[0], pouchAmmoStack.getCount());
                        pouchAmmoStack.shrink(max);
                        shrinkAmt[0] -= max;
                        if (shrinkAmt[0] == 0) {
                            updateAmmoPouchContents(itemStack, contents);
                            return;
                        }
                    }
                }
                updateAmmoPouchContents(itemStack, contents);
            }
        }
        if (shrinkAmt[0] > 0 && !ammoStack.isEmpty() && ammoStack.getItem() == gun.getProjectile().getItem()) {
            int max = Math.min(shrinkAmt[0], ammoStack.getCount());
            ammoStack.shrink(max);
            shrinkAmt[0] -= max;
        }
    }

    private void updateAmmoPouchContents(ItemStack ammoPouch, List<ItemStack> contents) {
        ListTag listTag = new ListTag();
        for (ItemStack stack : contents) {
            CompoundTag itemTag = new CompoundTag();
            stack.save(ItemStackNbtHelper.EMPTY_REGISTRIES, itemTag);
            listTag.add(itemTag);
        }
        ItemStackNbtHelper.getOrCreateTag(ammoPouch).put(AmmoBoxItem.TAG_ITEMS, listTag);
    }

    public void increaseMagAmmo(Player player) {
        ItemStack[] ammoStack = Gun.findAmmoStack(player, this.gun.getProjectile().getItem());
        if (ammoStack.length > 0) {
            int maxAmmo = GunModifierHelper.getModifiedAmmoCapacity(this.stack, this.gun);
            boolean hasCreativeBox = player.getInventory().items.stream()
                    .anyMatch(i -> i.getItem() instanceof CreativeAmmoBoxItem) ||
                    hasCreativeAmmoBoxInCurios((ServerPlayer) player);
            if (hasCreativeBox) {
                this.tag.putInt("AmmoCount", maxAmmo);
                return;
            }
            int currentAmmo = this.tag.getInt("AmmoCount");
            if (currentAmmo < 0 || currentAmmo > maxAmmo) {
                currentAmmo = 0;
            }
            int amount = Math.min(ammoInInventory(ammoStack), maxAmmo - currentAmmo);
            if (amount > 0) {
                this.tag.putInt("AmmoCount", currentAmmo + amount);
                this.shrinkFromAmmoPool(ammoStack, player, amount);
            }
        }
        playReloadSound(player);
    }


    public void reloadItem(Player player) {
        Item reloadItem = this.gun.getReloads().getReloadItem();
        ItemStack[] ammoStacks = Gun.findAmmoStack(player, reloadItem);

        if (ammoStacks.length > 0) {
            int maxAmmo = GunModifierHelper.getModifiedAmmoCapacity(this.stack, this.gun);
            int currentAmmo = this.tag.getInt("AmmoCount");

            if (currentAmmo < maxAmmo) {
                this.tag.putInt("AmmoCount", maxAmmo);
                this.shrinkFromAmmoPool(ammoStacks, player, 1);
                handleReloadByproduct(player);
            }
            playReloadSound(player);
        }
    }

    public void increaseAmmo(Player player) {
        AmmoContext context = Gun.findAmmo(player, this.gun.getProjectile().getItem());
        ItemStack ammo = context.stack();

        if (!ammo.isEmpty()) {
            int amount = Math.min(ammo.getCount(), this.gun.getReloads().getReloadAmount());
            int maxAmmo = GunModifierHelper.getModifiedAmmoCapacity(this.stack, this.gun);
            int currentAmmo = this.tag.getInt("AmmoCount");
            amount = Math.min(amount, maxAmmo - currentAmmo);
            if (amount > 0) {
                this.tag.putInt("AmmoCount", currentAmmo + amount);
                shrinkFromAmmoPool(Gun.findAmmoStack(player, this.gun.getProjectile().getItem()), player, amount);
            }
        }

        playReloadSound(player);
    }

    public CompoundTag getTag() {
        return this.tag;
    }

    private void playReloadSound(Player player) {
        if (stack.getItem() instanceof AnimatedGunItem) {
            return;
        }
        ResourceLocation reloadSound = this.gun.getSounds().getReload();
        if (reloadSound != null) {
            double radius = Config.SERVER.reloadMaxDistance.get();
            double soundX = player.getX();
            double soundY = player.getY() + 1.0;
            double soundZ = player.getZ();
            S2CMessageGunSound message = new S2CMessageGunSound(reloadSound, SoundSource.PLAYERS, (float) soundX, (float) soundY, (float) soundZ, 1.0F, 1.0F, player.getId(), false, true);
            PacketHandler.getPlayChannel().sendToNearbyPlayers(() -> LevelLocation.create((ServerLevel) player.level(), soundX, soundY, soundZ, radius), message);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        Player player = event.player;
        if (player.level().isClientSide) {
            return;
        }

        ReloadTracker tracker = RELOAD_TRACKER_MAP.get(player);
        boolean isActivelyReloading = ModSyncedDataKeys.RELOADING.getValue(player);

        if (isActivelyReloading) {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof GunItem gunItem) {
                if (!(heldItem.getItem().getClass().getPackageName().startsWith("top.ribs.scguns"))) {
                    return;
                }

                boolean needsNewTracker = false;
                if (tracker == null) {
                    needsNewTracker = true;
                } else {
                    int currentSlot = player.getInventory().selected;
                    if (tracker.slot != currentSlot ||
                            heldItem.isEmpty() ||
                            !heldItem.getItem().getClass().equals(tracker.stack.getItem().getClass())) {
                        needsNewTracker = true;
                        RELOAD_TRACKER_MAP.remove(player);
                    }
                }

                if (needsNewTracker) {
                    tracker = new ReloadTracker(player);
                    RELOAD_TRACKER_MAP.put(player, tracker);
                }

                CompoundTag tag = tracker.getTag();
                boolean weaponFull = tracker.isWeaponFull(player);
                boolean hasNoAmmo = tracker.hasNoAmmo(player);

                if (weaponFull || hasNoAmmo) {
                    if (heldItem.getItem() instanceof AnimatedGunItem &&
                            tracker.gun.getReloads().getReloadType() == ReloadType.MANUAL) {

                        if (tag.getBoolean("scguns:ShouldStopAfterLoop")) {
                            long stopTime = tag.getLong("scguns:StopAfterLoopTime");
                            if (stopTime == 0) {
                                tag.putLong("scguns:StopAfterLoopTime", System.currentTimeMillis());
                                return;
                            }
                            if (System.currentTimeMillis() - stopTime < 100) {
                                return;
                            }
                            tag.remove("scguns:ShouldStopAfterLoop");
                            tag.remove("scguns:StopAfterLoopTime");
                        } else {
                            tag.putBoolean("scguns:ShouldStopAfterLoop", true);
                            return;
                        }
                    }
                    RELOAD_TRACKER_MAP.remove(player);
                    ModSyncedDataKeys.RELOADING.setValue(player, false);
                    tag.remove("IsReloading");
                    tag.remove("scguns:IsReloading");
                    if (heldItem.getItem() instanceof AnimatedGunItem) {
                        tag.putString("scguns:ReloadState", "STOPPING");
                        tag.putBoolean("scguns:IsPlayingReloadStop", true);
                        PacketHandler.getPlayChannel().sendToPlayer(() -> (ServerPlayer) player, new S2CMessageStopReload());
                    }
                }
            }
        } else if (tracker != null) {
            RELOAD_TRACKER_MAP.remove(player);
            tracker.getTag().remove("IsReloading");
        }
    }


    public static void loaded(Player player) {
        if (!ModSyncedDataKeys.RELOADING.getValue(player)) {
            ReloadTracker oldTracker = RELOAD_TRACKER_MAP.remove(player);
            if (oldTracker != null) {
                oldTracker.getTag().remove("IsReloading");
            }
            return;
        }

        if (!RELOAD_TRACKER_MAP.containsKey(player)) {
            if (!(player.getInventory().getSelected().getItem() instanceof GunItem)) {
                ModSyncedDataKeys.RELOADING.setValue(player, false);
                return;
            }
            RELOAD_TRACKER_MAP.put(player, new ReloadTracker(player));
        }

        ReloadTracker tracker = RELOAD_TRACKER_MAP.get(player);
        CompoundTag tag = tracker.getTag();

        boolean weaponChanged = false;
        ItemStack currentWeapon = player.getInventory().getSelected();
        int currentSlot = player.getInventory().selected;
        if (tracker.slot != currentSlot ||
                !currentWeapon.getItem().equals(tracker.stack.getItem()) ||
                currentWeapon.isEmpty()) {
            weaponChanged = true;
        }

        if (weaponChanged ||
                tracker.hasNoAmmo(player) ||
                (tracker.isWeaponFull(player) && tracker.gun.getReloads().getReloadType() != ReloadType.MANUAL)) {
            RELOAD_TRACKER_MAP.remove(player);
            ModSyncedDataKeys.RELOADING.setValue(player, false);
            tag.remove("IsReloading");
            return;
        }

        Item item = currentWeapon.getItem();
        if (item instanceof GunItem) {
            Gun gun = tracker.gun;
            ReloadType reloadType = gun.getReloads().getReloadType();
            if (!(item instanceof AnimatedGunItem)) {
                if (reloadType == ReloadType.MAG_FED) {
                    tracker.increaseMagAmmo(player);
                } else if (reloadType == ReloadType.SINGLE_ITEM) {
                    tracker.reloadItem(player);
                } else if (reloadType == ReloadType.MANUAL) {
                    tracker.increaseAmmo(player);
                }

                RELOAD_TRACKER_MAP.remove(player);
                ModSyncedDataKeys.RELOADING.setValue(player, false);
                tag.remove("IsReloading");
                return;
            }
            if (item instanceof AnimatedGunItem gunItem &&
                    item.getClass().getPackageName().startsWith("top.ribs.scguns")) {
                if (reloadType == ReloadType.MANUAL) {
                    tracker.increaseAmmo(player);
                }

                if (tracker.isWeaponFull(player) || tracker.hasNoAmmo(player)) {
                    if (reloadType == ReloadType.MANUAL) {
                        tag.putBoolean("scguns:ShouldStopAfterLoop", true);
                        return;
                    }

                    long id = GeoItem.getId(currentWeapon);
                    AnimationController<GeoAnimatable> animationController = gunItem.getAnimatableInstanceCache()
                            .getManagerForId(id)
                            .getAnimationControllers()
                            .get("controller");

                    if (animationController != null) {
                        animationController.setAnimationSpeed(1.0);
                        animationController.forceAnimationReset();
                    }

                    tracker.handleReloadByproduct(player);
                    RELOAD_TRACKER_MAP.remove(player);
                    ModSyncedDataKeys.RELOADING.setValue(player, false);
                    tag.remove("IsReloading");
                }
            }
        }
    }
}
