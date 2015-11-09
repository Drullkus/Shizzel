package us.drullk.shizzel.gui.appEng.elements;

import appeng.api.config.SortDir;
import net.minecraft.util.StatCollector;

import java.util.List;

public class GUIButtonSortingDirection extends AbstractStateGuiButton
{
    public GUIButtonSortingDirection(int ID, int x, int y, String text) {
        super(ID, x, y, text);
    }

    @Override
    public void getTooltip(List<String> tooltip)
    {
        this.addAboutToTooltip( tooltip, StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.SortOrder"),
                StatCollector.translateToLocal("gui.tooltips.appliedenergistics2.ToggleSortDirection"));
    }

    public void setSortingDirection(SortDir direction)
    {
        switch ( direction )
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
