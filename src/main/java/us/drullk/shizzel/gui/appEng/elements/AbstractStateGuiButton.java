package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.google.common.base.Splitter;

import net.minecraft.util.EnumChatFormatting;

public abstract class AbstractStateGuiButton extends AbstractGuiButtonBase
{
    protected IStateIconTexture stateIcon;

    public int iconXOffset = 0, iconYOffset = 0;

    public AbstractStateGuiButton(final int ID, final int xPosition, final int yPosition, final int buttonWidth, final int buttonHeight,
            final IStateIconTexture icon, final int iconXOffset, final int iconYOffset, final IStateIconTexture backgroundIcon)
    {
        // Call super
        super(ID, xPosition, yPosition, buttonWidth, buttonHeight, "");

        // Set the icon
        this.stateIcon = icon;

        // Set the offsets
        this.iconXOffset = iconXOffset;
        this.iconYOffset = iconYOffset;
    }

    protected void addAboutToTooltip(List<String> tooltip, String title, String text)
    {
        tooltip.add(EnumChatFormatting.WHITE + title);

        for (String line : Splitter.on("\n").split(WordUtils.wrap(text, 30, "\n", false)))
        {
            tooltip.add(EnumChatFormatting.GRAY + line.trim());
        }
    }
}
