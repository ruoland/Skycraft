package org.land.skycraftclient.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SkyButton extends ButtonWidget {
    protected SkyButton(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y, width, height, message, onPress, narrationSupplier);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if(isFocused()){

        }
    }


}
