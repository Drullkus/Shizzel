package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import appeng.api.config.ViewItems;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.util.EnumChatFormatting;

public class GUIButtonViewType extends AbstractStateGuiButton
{
    private String tooltipViewType = "";

    public GUIButtonViewType(int ID, int x, int y, String text)
    {
        super(ID, x, y, text);
    }

    @Override
    public void getTooltip(List<String> tooltip)
    {
        this.addAboutToTooltip(tooltip, ButtonToolTips.View.getLocal(), EnumChatFormatting.GRAY + this.tooltipViewType);
    }

    public void setViewMode(final ViewItems mode)
    {
        switch (mode)
        {
        case ALL:
            this.tooltipViewType = ButtonToolTips.StoredCraftable.getLocal();
            this.stateIcon = AEStateIconsEnum.VIEW_TYPE_ALL;
            break;

        case CRAFTABLE:
            this.tooltipViewType = ButtonToolTips.Craftable.getLocal();
            this.stateIcon = AEStateIconsEnum.VIEW_TYPE_CRAFT;
            break;

        case STORED:
            this.tooltipViewType = ButtonToolTips.StoredItems.getLocal();
            this.stateIcon = AEStateIconsEnum.VIEW_TYPE_STORED;
            break;
        }
    }
}
