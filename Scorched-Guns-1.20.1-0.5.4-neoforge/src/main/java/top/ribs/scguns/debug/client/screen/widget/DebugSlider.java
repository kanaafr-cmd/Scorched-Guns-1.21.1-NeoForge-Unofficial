package top.ribs.scguns.debug.client.screen.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import top.ribs.scguns.debug.IDebugWidget;

import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class DebugSlider extends AbstractSliderButton implements IDebugWidget {
    private final double minValue;
    private final double maxValue;
    private final Consumer<Double> callback;

    public DebugSlider(double minValue, double maxValue, double currentValue, double stepSize, int precision, Consumer<Double> callback) {
        super(0, 0, 0, 14, Component.empty(), (currentValue - minValue) / (maxValue - minValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.callback = callback;
    }

    @Override
    protected void applyValue() {
        this.callback.accept(this.getValue());
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.empty());
    }

    public double getValue() {
        return this.minValue + (this.maxValue - this.minValue) * this.value;
    }
}
