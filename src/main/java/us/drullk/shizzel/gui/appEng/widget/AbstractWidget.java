package us.drullk.shizzel.gui.appEng.widget;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import us.drullk.shizzel.utils.GuiHelper;

public abstract class AbstractWidget extends Gui
{
    public static int WIDGET_SIZE = 18;

    protected int xPosition;

    protected int yPosition;

    protected IWidgetHost hostGUI;

    public AbstractWidget(IWidgetHost hostGUI, int xPos, int yPos)
    {
        this.hostGUI = hostGUI;

        this.xPosition = xPos;

        this.yPosition = yPos;
    }

    public void drawMouseHoverUnderlay()
    {
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        this.drawGradientRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + 17, this.yPosition + 17, 0x80FFFFFF, 0x80FFFFFF);

        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public abstract void drawWidget();

    public abstract void getTooltip(List<String> tooltip);

    public boolean isMouseOverWidget(int mouseX, int mouseY)
    {
        return GuiHelper.INSTANCE.isPointInGuiRegion(this.yPosition, this.xPosition, AbstractWidget.WIDGET_SIZE, AbstractWidget.WIDGET_SIZE, mouseX,
                mouseY, this.hostGUI.guiLeft(), this.hostGUI.guiTop());
    }

    public abstract void mouseClicked();

    public void setPosition(int xPos, int yPos)
    {
        this.xPosition = xPos;
        this.yPosition = yPos;
    }
}
