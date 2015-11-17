package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import appeng.api.config.SortOrder;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class GUIButtonSortingMode extends AbstractStateGuiButton
{
    private String tooltipSortBy = "";

    public GUIButtonSortingMode( final int ID, final int xPosition, final int yPosition, final int width, final int height )
    {
        super( ID, xPosition, yPosition, width, height, AEStateIconsEnum.SORT_MODE_ALPHABETIC, 0, 0, AEStateIconsEnum.REGULAR_BUTTON );
    }

    @Override
    public void getTooltip(List<String> tooltip)
    {
        this.addAboutToTooltip(tooltip, ButtonToolTips.SortBy.getLocal(), EnumChatFormatting.GRAY + this.tooltipSortBy);
    }

    public void setSortMode(SortOrder order)
    {
        switch (order)
        {
        case AMOUNT:
            this.stateIcon = AEStateIconsEnum.SORT_MODE_AMOUNT;
            this.tooltipSortBy = StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.NumberOfItems");
            break;

        case INVTWEAKS:
            this.stateIcon = AEStateIconsEnum.SORT_MODE_INVTWEAK;
            this.tooltipSortBy = StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.InventoryTweaks");
            break;

        case MOD:
            this.stateIcon = AEStateIconsEnum.SORT_MODE_MOD;
            this.tooltipSortBy = StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.Mod");
            break;

        case NAME:
            this.stateIcon = AEStateIconsEnum.SORT_MODE_ALPHABETIC;
            this.tooltipSortBy = StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.ItemName");
            break;

        }
    }
}
