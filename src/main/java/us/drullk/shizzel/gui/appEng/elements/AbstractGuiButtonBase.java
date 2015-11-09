package us.drullk.shizzel.gui.appEng.elements;

import net.minecraft.client.gui.GuiButton;
import us.drullk.shizzel.utils.Helper;

import java.util.List;

public abstract class AbstractGuiButtonBase extends GuiButton
{
    public AbstractGuiButtonBase(int ID, int x, int y, String text) {
        super(ID, x, y, text);
    }

    public abstract void getTooltip(List<String> tooltip);

    public boolean isMouseOverButton(int cursorX, int cursorY)
    {
        return Helper.isPointInRegion(this.yPosition, this.xPosition, this.height, this.width, cursorX, cursorY);
    }
}
