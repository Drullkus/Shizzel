package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import appeng.api.config.SearchBoxMode;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.util.EnumChatFormatting;

public class GUIButtonSearchMode
        extends AbstractStateGuiButton
{
    private String tooltipMode = "";

    public GUIButtonSearchMode(final int ID, final int xPosition, final int yPosition, final int buttonWidth, final int buttonHeight,
            final SearchBoxMode currentMode)
    {
        super(ID, xPosition, yPosition, buttonWidth, buttonHeight, null, 0, 0, AEStateIconsEnum.REGULAR_BUTTON);

        this.setSearchMode(currentMode);
    }

    @Override
    public void getTooltip(final List<String> tooltip)
    {
        this.addAboutToTooltip(tooltip, ButtonToolTips.SearchMode.getLocal(), EnumChatFormatting.GRAY + this.tooltipMode);
    }

    public void setSearchMode(final SearchBoxMode mode)
    {
        switch (mode)
        {
        case AUTOSEARCH:
            this.tooltipMode = ButtonToolTips.SearchMode_Auto.getLocal();
            this.stateIcon = AEStateIconsEnum.SEARCH_MODE_AUTO;
            break;

        case MANUAL_SEARCH:
            this.tooltipMode = ButtonToolTips.SearchMode_Standard.getLocal();
            this.stateIcon = AEStateIconsEnum.SEARCH_MODE_MANUAL;
            break;

        case NEI_AUTOSEARCH:
            this.tooltipMode = ButtonToolTips.SearchMode_NEIAuto.getLocal();
            this.stateIcon = AEStateIconsEnum.SEARCH_MODE_NEI_AUTO;
            break;

        case NEI_MANUAL_SEARCH:
            this.tooltipMode = ButtonToolTips.SearchMode_NEIStandard.getLocal();
            this.stateIcon = AEStateIconsEnum.SEARCH_MODE_NEI_MANUAL;
            break;

        }
    }

}
