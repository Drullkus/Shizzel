package us.drullk.shizzel.gui.appEng.elements;

import com.google.common.base.Splitter;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public abstract class AbstractStateGuiButton extends AbstractGuiButtonBase
{
    protected IStateIconTexture stateIcon;

    public AbstractStateGuiButton(int ID, int x, int y, String text) {
        super(ID, x, y, text);
    }

    protected void addAboutToTooltip(List<String> tooltip, String title, String text )
    {
        tooltip.add(EnumChatFormatting.WHITE + title);

        for(String line : Splitter.on("\n").split(WordUtils.wrap(text, 30, "\n", false)))
        {
            tooltip.add(EnumChatFormatting.GRAY + line.trim());
        }
    }
}
