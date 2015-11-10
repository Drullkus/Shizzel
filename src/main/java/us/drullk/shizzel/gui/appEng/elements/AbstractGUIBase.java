package us.drullk.shizzel.gui.appEng.elements;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import us.drullk.shizzel.utils.Helper;

public abstract class AbstractGUIBase extends GuiContainer
{
    private class Bounds
    {
        /**
         * Top Y position.
         */
        public int T;

        /**
         * Left X position.
         */
        public int L;

        /**
         * Bottom Y position.
         */
        public int B;

        /**
         * Right X position.
         */
        public int R;

        /**
         * Creates the boundary
         *
         * @param t
         * Top Y position.
         * @param l
         * Left X position.
         * @param b
         * Bottom Y position.
         * @param r
         * Right X position.
         */
        public Bounds(final int t, final int l, final int b, final int r)
        {
            this.T = t;
            this.L = l;
            this.B = b;
            this.R = r;
        }
    }

    private static final int TOOLTIP_OFFSET = 12;

    private static final int TOOLTIP_EMPTY_HEIGHT = 8;

    private static final int TOOLTIP_LINE_HEIGHT = 10;

    private static final int TOOLTIP_HEIGHT_MARGIN = 2;

    private static final int TOOLTIP_BORDER_SIZE = 3;

    private static final int TOOLTIP_COLOR_BACKGROUND = 0xF0100010;

    private static final int TOOLTIP_COLOR_OUTER = 0xFF000000;

    private static final int TOOLTIP_COLOR_INNER_BEGIN = 0xC05000FF;

    private static final int TOOLTIP_COLOR_INNER_END = 0xC05000FF;

    protected final List<String> tooltip = new ArrayList<String>();

    public AbstractGUIBase(Container container)
    {
        super(container);
    }

    protected final boolean addTooltipFromButtons(int cursorX, int cursorY)
    {
        for (Object obj : this.buttonList)
        {
            if (obj instanceof AbstractGuiButtonBase)
            {
                AbstractGuiButtonBase currentButton = (AbstractGuiButtonBase) obj;

                if (currentButton.isMouseOverButton(cursorX, cursorY))
                {
                    currentButton.getTooltip(this.tooltip);

                    return true;
                }
            }
        }

        return false;
    }

    protected final void drawTooltip(int posX, int posY, final boolean clearTooltipAfterDraw)
    {
        if (!this.tooltip.isEmpty())
        {
            // Disable rescaling
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            // Disable lighting
            GL11.glDisable(GL11.GL_LIGHTING);

            // Disable depth testing
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            // Bounds check the position
            if (posY < 0)
            {
                posY = 0;
            }

            // Assume string length is zero
            int maxStringLength_px = 0;

            // Get max string length from lines in the list
            for (String string : this.tooltip)
            {
                // Get the length of the string
                int stringLen = this.mc.fontRenderer.getStringWidth(string);

                // Is it larger than the previous length?
                if (stringLen > maxStringLength_px)
                {
                    // Set it to maximum
                    maxStringLength_px = stringLen;
                }
            }

            // Offset the tooltip slightly
            posX = posX + AbstractGUIBase.TOOLTIP_OFFSET;
            posY = posY - AbstractGUIBase.TOOLTIP_OFFSET;

            // Base height of 8
            int tooltipHeight = AbstractGUIBase.TOOLTIP_EMPTY_HEIGHT;

            // Adjust height based on the number of lines
            if (this.tooltip.size() > 1)
            {
                // Calculate the line height
                int lineHeight = (this.tooltip.size() - 1) * AbstractGUIBase.TOOLTIP_LINE_HEIGHT;

                // Adjust the height
                tooltipHeight += (AbstractGUIBase.TOOLTIP_HEIGHT_MARGIN + lineHeight);
            }

            // Get the current z level
            float prevZlevel = this.zLevel;

            // Set the new level to some high number
            this.zLevel = 300;

            // Tooltip boundary
            Bounds bounds = new Bounds(posY - AbstractGUIBase.TOOLTIP_BORDER_SIZE, posX - AbstractGUIBase.TOOLTIP_BORDER_SIZE, posY + tooltipHeight +
                    AbstractGUIBase.TOOLTIP_BORDER_SIZE, posX + maxStringLength_px + AbstractGUIBase.TOOLTIP_BORDER_SIZE);

            // Draw the background and borders
            this.drawTooltipBackground(bounds);

            // Draw each line
            for (int index = 0; index < this.tooltip.size(); index++)
            {
                // Get the line
                String line = this.tooltip.get(index);

                // Draw the line
                this.mc.fontRenderer.drawStringWithShadow(line, posX, posY, -1);

                // Is this the first line?
                if (index == 0)
                {
                    // Add the margin
                    posY += AbstractGUIBase.TOOLTIP_HEIGHT_MARGIN;
                }

                // Add the line height
                posY += AbstractGUIBase.TOOLTIP_LINE_HEIGHT;
            }

            // Return the z level to what it was before
            this.zLevel = prevZlevel;

            // Reenable lighting
            GL11.glEnable(GL11.GL_LIGHTING);

            // Reenable depth testing
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            // Reenable scaling
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            // Clear the tooltip
            if (clearTooltipAfterDraw)
            {
                this.tooltip.clear();
            }
        }
    }

    private final void drawTooltipBackground(final Bounds bounds)
    {
        // Background
        this.drawGradientRect(bounds.L, bounds.T, bounds.R, bounds.B, AbstractGUIBase.TOOLTIP_COLOR_BACKGROUND,
                AbstractGUIBase.TOOLTIP_COLOR_BACKGROUND);

        // Draw outer borders
        this.drawTooltipBorders(bounds, AbstractGUIBase.TOOLTIP_COLOR_OUTER, AbstractGUIBase.TOOLTIP_COLOR_OUTER, 0);

        // Adjust bounds for inner borders
        bounds.T++;
        bounds.L++;
        bounds.B--;
        bounds.R--;

        // Draw inner borders
        this.drawTooltipBorders(bounds, AbstractGUIBase.TOOLTIP_COLOR_INNER_BEGIN, AbstractGUIBase.TOOLTIP_COLOR_INNER_END, 1);
    }

    private final void drawTooltipBorders(final Bounds bounds, final int colorStart, final int colorEnd, final int cornerExpansion)
    {
        // Left
        this.drawGradientRect(bounds.L - 1, bounds.T - cornerExpansion, bounds.L, bounds.B + cornerExpansion, colorStart, colorEnd);

        // Top
        this.drawGradientRect(bounds.L, bounds.T - 1, bounds.R, bounds.T, colorStart, colorEnd);

        // Right
        this.drawGradientRect(bounds.R, bounds.T - cornerExpansion, bounds.R + 1, bounds.B + cornerExpansion, colorStart, colorEnd);

        // Bottom
        this.drawGradientRect(bounds.L, bounds.B, bounds.R, bounds.B + 1, colorStart, colorEnd);
    }

    private final boolean isPointWithinSlot(final Slot slot, final int x, final int y)
    {
        return Helper.isPointInGuiRegion(slot.yDisplayPosition, slot.xDisplayPosition, 16, 16, x, y, this.guiLeft, this.guiTop);
    }

    protected final Slot getSlotAtPosition(final int x, final int y)
    {
        // Loop over all slots
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++)
        {
            // Get the slot
            Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);

            // Is the point within the slot?
            if (this.isPointWithinSlot(slot, x, y))
            {
                // Return the slot
                return slot;
            }
        }

        // Point was not within any slot
        return null;
    }
}
