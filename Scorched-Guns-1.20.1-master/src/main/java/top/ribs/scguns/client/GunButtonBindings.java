package top.ribs.scguns.client;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.binding.BindingRegistry;
import com.mrcrayfish.controllable.client.binding.ButtonBinding;
import com.mrcrayfish.controllable.client.binding.context.NeoForgeKeyContext;
import com.mrcrayfish.controllable.client.binding.handlers.ButtonHandler;
import com.mrcrayfish.controllable.client.input.Buttons;

/**
 * Author: MrCrayfish
 */
public class GunButtonBindings
{
    private static final NeoForgeKeyContext GUN_CONTEXT = new NeoForgeKeyContext(GunConflictContext.IN_GAME_HOLDING_WEAPON);
    private static final ButtonHandler BUTTON_HANDLER = new ButtonHandler();
    public static final ButtonBinding SHOOT = new ButtonBinding(Buttons.RIGHT_TRIGGER, "scguns.button.shoot", "button.categories.scguns", GUN_CONTEXT, BUTTON_HANDLER);
    public static final ButtonBinding AIM = new ButtonBinding(Buttons.LEFT_TRIGGER, "scguns.button.aim", "button.categories.scguns", GUN_CONTEXT, BUTTON_HANDLER);
    public static final ButtonBinding RELOAD = new ButtonBinding(Buttons.X, "scguns.button.reload", "button.categories.scguns", GUN_CONTEXT, BUTTON_HANDLER);
    public static final ButtonBinding OPEN_ATTACHMENTS = new ButtonBinding(Buttons.B, "scguns.button.attachments", "button.categories.scguns", GUN_CONTEXT, BUTTON_HANDLER);
    public static final ButtonBinding STEADY_AIM = new ButtonBinding(Buttons.RIGHT_THUMB_STICK, "scguns.button.steadyAim", "button.categories.scguns", GUN_CONTEXT, BUTTON_HANDLER);

    public static void register()
    {
        BindingRegistry registry = Controllable.getBindingRegistry();
        registry.register(SHOOT);
        registry.register(AIM);
        registry.register(RELOAD);
        registry.register(OPEN_ATTACHMENTS);
        registry.register(STEADY_AIM);

    }
}
