package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import us.drullk.shizzel.utils.GuiHelper;

public abstract class AbstractGuiButtonBase extends GuiButton
{
    public AbstractGuiButtonBase(int ID, final int xPosition, final int yPosition, final int width, final int height, final String text)
    {
        super(ID, xPosition, yPosition, width, height, text);
    }

    public AbstractGuiButtonBase(int ID, int x, int y, String text)
    {
        super(ID, x, y, text);
    }

    public abstract void getTooltip(List<String> tooltip);

    public boolean isMouseOverButton(int cursorX, int cursorY)
    {
        return GuiHelper.INSTANCE.isPointInRegion(this.yPosition, this.xPosition, this.height, this.width, cursorX, cursorY);
    }
}
