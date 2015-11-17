package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import appeng.api.config.SortDir;
import net.minecraft.util.StatCollector;

public class GUIButtonSortingDirection extends AbstractStateGuiButton
{
    public GUIButtonSortingDirection( final int ID, final int xPosition, final int yPosition, final int width, final int height )
    {
        super( ID, xPosition, yPosition, width, height, AEStateIconsEnum.SORT_DIR_ASC, 0, 0, AEStateIconsEnum.REGULAR_BUTTON );
    }

    @Override
    public void getTooltip(List<String> tooltip)
    {
        this.addAboutToTooltip(tooltip, StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.SortOrder"),
                StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.ToggleSortDirection"));
    }

    public void setSortingDirection(SortDir direction)
    {
        switch (direction)
        {
        case ASCENDING:
            this.stateIcon = AEStateIconsEnum.SORT_DIR_ASC;
            break;

        case DESCENDING:
            this.stateIcon = AEStateIconsEnum.SORT_DIR_DEC;
            break;
        }
    }
}
