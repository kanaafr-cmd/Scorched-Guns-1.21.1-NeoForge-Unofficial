package dev.codex.dynamictreesfix.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.codex.dynamictreesfix.DynamicTreesRootsFamilyFix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(targets = "com.dtteam.dynamictrees.block.branch.BasicRootsBlock", remap = false)
public abstract class BasicRootsBlockMixin {
    @Unique
    private static Field dynamic_trees_roots_family_fix$familyField;

    @Unique
    private static Class<?> dynamic_trees_roots_family_fix$rootsFamilyClass;

    @Unique
    private static Object dynamic_trees_roots_family_fix$fallbackRootsFamily;

    @Inject(method = "getFamily()Lcom/dtteam/dynamictrees/tree/family/UndergroundRootsFamily;", at = @At("HEAD"), cancellable = true, remap = false)
    private void dynamic_trees_roots_family_fix$getFamily(CallbackInfoReturnable<Object> cir) {
        Object family = dynamic_trees_roots_family_fix$getRawFamily(this);
        if (family == null || dynamic_trees_roots_family_fix$isUndergroundRootsFamily(family)) {
            return;
        }

        Object fallback = dynamic_trees_roots_family_fix$getFallbackRootsFamily();
        if (fallback != null) {
            DynamicTreesRootsFamilyFix.LOGGER.warn(
                "Dynamic Trees roots block {} has unresolved family {}; using fallback UndergroundRootsFamily to avoid startup crash.",
                this.getClass().getName(),
                family.getClass().getName());
            cir.setReturnValue(fallback);
        }
    }

    @Inject(method = "getRadius(Lnet/minecraft/world/level/block/state/BlockState;)I", at = @At("HEAD"), cancellable = true, remap = false)
    private void dynamic_trees_roots_family_fix$getRadius(BlockState state, CallbackInfoReturnable<Integer> cir) {
        Object family = dynamic_trees_roots_family_fix$getRawFamily(this);
        if (family == null || dynamic_trees_roots_family_fix$isUndergroundRootsFamily(family)) {
            return;
        }

        DynamicTreesRootsFamilyFix.LOGGER.warn(
            "Dynamic Trees roots block {} has unresolved family {} during radius lookup; returning radius 0 to avoid startup crash.",
            this.getClass().getName(),
            family.getClass().getName());
        cir.setReturnValue(0);
    }

    @Unique
    private static Object dynamic_trees_roots_family_fix$getRawFamily(Object rootsBlock) {
        try {
            Field field = dynamic_trees_roots_family_fix$familyField;
            if (field == null) {
                Class<?> branchBlock = Class.forName("com.dtteam.dynamictrees.block.branch.BranchBlock");
                field = branchBlock.getDeclaredField("family");
                field.setAccessible(true);
                dynamic_trees_roots_family_fix$familyField = field;
            }
            return field.get(rootsBlock);
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            DynamicTreesRootsFamilyFix.LOGGER.warn("Could not inspect Dynamic Trees roots block family.", ex);
            return null;
        }
    }

    @Unique
    private static boolean dynamic_trees_roots_family_fix$isUndergroundRootsFamily(Object family) {
        try {
            Class<?> rootsFamilyClass = dynamic_trees_roots_family_fix$rootsFamilyClass;
            if (rootsFamilyClass == null) {
                rootsFamilyClass = Class.forName("com.dtteam.dynamictrees.tree.family.UndergroundRootsFamily");
                dynamic_trees_roots_family_fix$rootsFamilyClass = rootsFamilyClass;
            }
            return rootsFamilyClass.isInstance(family);
        }
        catch (LinkageError | ReflectiveOperationException ex) {
            DynamicTreesRootsFamilyFix.LOGGER.warn("Could not inspect Dynamic Trees UndergroundRootsFamily type.", ex);
            return true;
        }
    }

    @Unique
    private static Object dynamic_trees_roots_family_fix$getFallbackRootsFamily() {
        Object fallback = dynamic_trees_roots_family_fix$fallbackRootsFamily;
        if (fallback != null) {
            return fallback;
        }

        try {
            Class<?> rootsFamilyClass = dynamic_trees_roots_family_fix$rootsFamilyClass;
            if (rootsFamilyClass == null) {
                rootsFamilyClass = Class.forName("com.dtteam.dynamictrees.tree.family.UndergroundRootsFamily");
                dynamic_trees_roots_family_fix$rootsFamilyClass = rootsFamilyClass;
            }
            fallback = rootsFamilyClass
                .getConstructor(ResourceLocation.class)
                .newInstance(ResourceLocation.fromNamespaceAndPath("the_world_compat_patches", "fallback_roots"));
            dynamic_trees_roots_family_fix$fallbackRootsFamily = fallback;
            return fallback;
        }
        catch (ReflectiveOperationException | LinkageError ex) {
            DynamicTreesRootsFamilyFix.LOGGER.warn("Could not create fallback Dynamic Trees UndergroundRootsFamily.", ex);
            return null;
        }
    }
}
