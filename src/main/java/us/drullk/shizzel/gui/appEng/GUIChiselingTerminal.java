package us.drullk.shizzel.gui.appEng;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import appeng.api.config.SearchBoxMode;
import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.widgets.GuiScrollbar;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.me.ItemRepo;
import appeng.core.AEConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.PartChiselingTerminal;
import us.drullk.shizzel.container.appEng.ContainerChiselingTerminal;
import us.drullk.shizzel.gui.appEng.elements.AEStateIconsEnum;
import us.drullk.shizzel.gui.appEng.elements.AbstractGUIBase;
import us.drullk.shizzel.gui.appEng.elements.GUIButtonSortingDirection;
import us.drullk.shizzel.gui.appEng.elements.GUIButtonSortingMode;
import us.drullk.shizzel.gui.appEng.elements.GUIButtonViewType;
import us.drullk.shizzel.gui.appEng.widget.WidgetAEItem;
import us.drullk.shizzel.networking.appEng.PacketChiselingTerminalServer;
import us.drullk.shizzel.utils.Helper;

@SideOnly(Side.CLIENT)
public class GUIChiselingTerminal extends AbstractGUIBase implements ISortSource
{
    private static final int GUI_WIDTH = 230;

    private static final int GUI_UPPER_HEIGHT = 35;

    private static final int GUI_MIDDLE_HEIGHT = 18;

    private static final int GUI_LOWER_HEIGHT = 53;

    private static final int TITLE_POS_X = 8;

    private static final int TITLE_POS_Y = 6;

    private static final int SEARCH_POS_X = 98;

    private static final int SEARCH_POS_Y = 6;

    private static final int SEARCH_WIDTH = 65;

    private static final int SEARCH_HEIGHT = 10;

    private static final int GUI_VIEW_CELL_TEXTURE_WIDTH = 35;

    private static final int GUI_VIEW_CELL_TEXTURE_HEIGHT = 104;

    private static final int GUI_MAIN_BODY_WIDTH = GUI_WIDTH - GUI_VIEW_CELL_TEXTURE_WIDTH;

    private static final int ME_COLUMNS = 9;

    private static final int ME_ROWS = 3;

    private static final int ME_GRID_WIDTH = 161;

    private static final int ME_ITEM_POS_X = 7;

    private static final int ME_ITEM_POS_Y = 17;

    private static final long WIDGET_TOOLTIP_UPDATE_INTERVAL = 3000L;

    // ------------------------------------------

    private boolean viewNeedsUpdate = true;

    private int rows = 0;

    private int previousMouseX = 0;

    private int previousMouseY = 0;

    private int lowerTerminalYOffset = 0;

    private int widgetCount = ME_ROWS * ME_COLUMNS;

    private long lastTooltipUpdateTime;

    private String guiTitle;

    private SortOrder sortOrder = SortOrder.NAME;

    private SortDir sortDirection = SortDir.ASCENDING;

    private ViewItems viewItems = ViewItems.ALL;

    private GUIButtonSortingDirection buttonSortDir;

    private GUIButtonSortingMode buttonSortMode;

    private GUIButtonViewType buttonViewType;

    private GuiTextField searchField;

    private GuiScrollbar scrollBar;

    private ItemRepo itemRepo;

    private EntityPlayer entityPlayer;

    private List<WidgetAEItem> itemWidgets = new ArrayList<WidgetAEItem>();

    private WidgetAEItem previousWidgetUnderMouse = null;

    // ------------------------------------------

    public GUIChiselingTerminal(PartChiselingTerminal part, EntityPlayer entPlayer)
    {
        super(new ContainerChiselingTerminal(part, entPlayer));

        this.entityPlayer = entPlayer;

    }

    public void onReceiveFullList(IItemList<IAEItemStack> itemList)
    {
        for (IAEItemStack stack : itemList)
        {
            this.itemRepo.postUpdate(stack);
        }

        this.viewNeedsUpdate = true;
    }

    public void onReceiveChange(IAEItemStack itemChange)
    {
        this.itemRepo.postUpdate(itemChange);

        this.viewNeedsUpdate = true;
    }

    public void onReceivePlayerHeld(IAEItemStack heldItemChange)
    {
        ItemStack itemStack = null;

        if (heldItemChange != null)
        {
            itemStack = heldItemChange.getItemStack();
        }

        this.entityPlayer.inventory.setItemStack(itemStack);
    }

    public void onReceiveSorting(SortOrder order, SortDir direction, ViewItems viewMode)
    {
        this.sortDirection = direction;

        // Set the order
        this.sortOrder = order;

        // Set view mode
        this.viewItems = viewMode;

        // Update
        this.updateSorting();
    }

    private void updateSorting()
    {
        // Set the direction icon
        this.buttonSortDir.setSortingDirection(this.sortDirection);

        // Set the order icon
        this.buttonSortMode.setSortMode(this.sortOrder);

        // Set the view mode
        this.buttonViewType.setViewMode(this.viewItems);

        // Repo needs update
        this.viewNeedsUpdate = true;
    }

    private void updateView()
    {
        this.viewNeedsUpdate = false;

        this.itemRepo.updateView();

        this.updateScrollbarRange();

        this.updateMEWidgets();
    }

    private void updateScrollbarRange()
    {
        this.scrollBar.setRange(0, (this.itemRepo.size() + this.ME_COLUMNS - 1) / this.ME_COLUMNS - this.rows, Math.max(1, this.rows / 6));
    }

    private void updateMEWidgets()
    {
        int repoIndex = 0;

        for (int index = 0; index < this.widgetCount; ++index)
        {
            IAEItemStack stack = this.itemRepo.getReferenceItem(repoIndex++);

            if (stack != null)
            {
                if (stack.getStackSize() == 0)
                {
                    index--;
                    continue;
                }

                this.itemWidgets.get(index).setItemStack(stack);
            }
            else
            {
                this.itemWidgets.get(index).setItemStack(null);
            }
        }
    }

    private WidgetAEItem drawItemWidgets(int cursorX, int cursorY)
    {
        boolean hasNoOverlay = true;

        WidgetAEItem widgetUnderMouse = null;

        for (int index = 0; index < this.widgetCount; ++index)
        {
            WidgetAEItem currentWidget = this.itemWidgets.get(index);

            currentWidget.drawWidget();

            if (hasNoOverlay && currentWidget.isMouseOverWidget(cursorX, cursorY))
            {
                currentWidget.drawMouseHoverUnderlay();

                hasNoOverlay = false;

                widgetUnderMouse = currentWidget;
            }
        }

        return widgetUnderMouse;
    }

    private boolean clickHandler_Widgets(final int mouseX, final int mouseY, final int mouseButton)
    {
        if (Helper.isPointInGuiRegion(ME_ITEM_POS_Y, ME_ITEM_POS_X, this.rows * GUI_MIDDLE_HEIGHT, ME_GRID_WIDTH, mouseX, mouseY, this.guiLeft, this.guiTop))
        {
            boolean doExtract = (this.entityPlayer.inventory.getItemStack() == null);

            doExtract |= (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (mouseButton == 1));

            if (doExtract)
            {
                this.sendItemWidgetClicked(mouseX, mouseY, mouseButton);
            }
            else
            {
                // Inform the server the user would like to deposit the currently held item into the ME network.
                new PacketChiselingTerminalServer().createRequestDeposit(this.entityPlayer, mouseButton).sendPacketToServer();
            }

            // Do not pass to super
            return true;
        }

        return false;
    }

    private void sendItemWidgetClicked(final int mouseX, final int mouseY, final int mouseButton)
    {
        for (int index = 0; index < this.widgetCount; ++index)
        {
            WidgetAEItem currentWidget = this.itemWidgets.get(index);

            if (currentWidget.isMouseOverWidget(mouseX, mouseY))
            {
                IAEItemStack widgetStack = currentWidget.getItemStack();

                if (widgetStack != null)
                {
                    if (widgetStack.getStackSize() == 0)
                    {
                        if (widgetStack.isCraftable())
                        {
                            //new PacketChiselingTerminalServer().createRequestAutoCraft( this.player, widgetStack ).sendPacketToServer();
                        }
                    }
                    else
                    {
                        boolean isShiftHeld = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

                        new PacketChiselingTerminalServer().createRequestExtract(this.entityPlayer, widgetStack, mouseButton, isShiftHeld)
                                .sendPacketToServer();
                    }
                }

                return;
            }
        }
    }

    private boolean clickHandler_RegionDeposit(final int mouseX, final int mouseY)
    {
        // Is the player holding the space key?
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
        {
            // Get the slot the mouse is over
            Slot slotClicked = this.getSlotAtPosition(mouseX, mouseY);

            // Was there a slot under the mouse?
            if (slotClicked != null)
            {
                new PacketChiselingTerminalServer().createRequestDepositRegion(this.entityPlayer, slotClicked.slotNumber).sendPacketToServer();

                return true;
            }
        }

        return false;
    }

    private boolean clickHandler_SearchBox(final int mouseX, final int mouseY, final int mouseButton)
    {
        // Was the mouse right-clicked over the search field?
        if ((mouseButton == 1) &&
                Helper.isPointInGuiRegion(SEARCH_POS_Y, SEARCH_POS_X,
                        SEARCH_HEIGHT, SEARCH_WIDTH, mouseX, mouseY, this.guiLeft, this.guiTop))
        {
            // Clear the search field
            this.searchField.setText("");

            // Update the repo
            this.itemRepo.searchString = "";

            // Repo needs update
            this.viewNeedsUpdate = true;

            // Inform search field.
            this.searchField.mouseClicked(mouseX - this.guiLeft, mouseY - this.guiTop, mouseButton);

            // Do not pass to super
            return true;
        }

        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float alpha, int cursorX, int cursorY)
    {
        if (this.viewNeedsUpdate)
        {
            this.updateView();
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.renderEngine.bindTexture(new ResourceLocation(Shizzel.MOD_ID, "textures/gui/guiTest"));

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GUI_MAIN_BODY_WIDTH, GUI_UPPER_HEIGHT);

        for (int i = 0; i < (this.rows - ME_ROWS); ++i)
        {
            int yPos = this.guiTop + GUI_UPPER_HEIGHT + (i * GUI_MIDDLE_HEIGHT);

            this.drawTexturedModalRect(this.guiLeft, yPos, 0, GUI_UPPER_HEIGHT,
                    GUI_MAIN_BODY_WIDTH, GUI_MIDDLE_HEIGHT);
        }

        this.drawTexturedModalRect(this.guiLeft, this.guiTop + GUI_UPPER_HEIGHT + this.lowerTerminalYOffset, 0,
                GUI_MIDDLE_HEIGHT + 17, GUI_MAIN_BODY_WIDTH, GUI_LOWER_HEIGHT + 18);

        this.drawTexturedModalRect(this.guiLeft + GUI_MAIN_BODY_WIDTH, this.guiTop, GUI_MAIN_BODY_WIDTH, 0, GUI_VIEW_CELL_TEXTURE_WIDTH, GUI_VIEW_CELL_TEXTURE_HEIGHT);

        Minecraft.getMinecraft().renderEngine.bindTexture(AEStateIconsEnum.AE_STATES_TEXTURE);

        int u = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getU(), v = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getV();
        int h = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getHeight(), w = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getWidth();
        int x = this.guiLeft + ContainerChiselingTerminal.VIEW_SLOT_XPOS,
                y = this.guiTop + ContainerChiselingTerminal.VIEW_SLOT_YPOS;

        for (int row = 0; row < 5; row++)
        {
            this.drawTexturedModalRect(x, y + (row * 18), u, v, w, h);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int cursorX, int cursorY)
    {
        super.drawGuiContainerForegroundLayer(cursorX, cursorY);

        this.fontRendererObj.drawString(this.guiTitle, TITLE_POS_X, TITLE_POS_Y, 0x000000);

        // Draw the search field.
        this.searchField.drawTextBox();

        // Enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);

        // Draw the widgets and get which one the mouse is over
        WidgetAEItem widgetUnderMouse = this.drawItemWidgets(cursorX, cursorY);

        // Should we force a tooltip update?
        boolean forceTooltipUpdate = ((System.currentTimeMillis() - this.lastTooltipUpdateTime) >= WIDGET_TOOLTIP_UPDATE_INTERVAL);

        // Has the mouse moved, or timeout reached?
        if (forceTooltipUpdate || (this.previousMouseX != cursorX) || (this.previousMouseY != cursorY))
        {
            // Do we have a widget under the mouse?
            if (widgetUnderMouse != null)
            {
                // Has the widget changed?
                if (forceTooltipUpdate || (widgetUnderMouse != this.previousWidgetUnderMouse))
                {
                    // Clear the tooltip
                    this.tooltip.clear();

                    // Get the tooltip from the widget
                    widgetUnderMouse.getTooltip(this.tooltip);

                    // Set the time
                    this.lastTooltipUpdateTime = System.currentTimeMillis();
                }
            }
            else
            {
                // Clear the tooltip
                this.tooltip.clear();

                // Set the time
                this.lastTooltipUpdateTime = System.currentTimeMillis();

                // Get the tooltip from the buttons
                this.addTooltipFromButtons(cursorX, cursorY);
            }

            // Set the previous position
            this.previousMouseX = cursorX;
            this.previousMouseY = cursorY;

            // Set the previous widget
            this.previousWidgetUnderMouse = widgetUnderMouse;
        }

        // Draw the tooltip
        this.drawTooltip(cursorX - this.guiLeft, cursorY - this.guiTop, false);
    }

    @Override
    public Enum getSortBy()
    {
        return this.sortOrder;
    }

    @Override
    public Enum getSortDir()
    {
        return this.sortDirection;
    }

    @Override
    public Enum getSortDisplay()
    {
        return this.viewItems;
    }

    @Override
    protected void keyTyped(final char key, final int keyID)
    {
        // Did they press the escape key?
        if (keyID == Keyboard.KEY_ESCAPE)
        {
            // Close the screen.
            this.mc.thePlayer.closeScreen();
            return;
        }

        // Prevent only spaces
        if ((key == ' ') && (this.searchField.getText().length() == 0))
        {
            return;
        }

        if (this.searchField.textboxKeyTyped(key, keyID))
        {
            // Get the search query
            String newSearch = this.searchField.getText().trim().toLowerCase();

            // Has the query changed?
            if (!newSearch.equals(this.itemRepo.searchString))
            {
                // Set the search string
                this.itemRepo.searchString = newSearch;

                // Repo needs update
                this.viewNeedsUpdate = true;
            }
        }
        else
        {
            super.keyTyped(key, keyID);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton)
    {
        if (this.clickHandler_Widgets(mouseX, mouseY, mouseButton))
        {
            return;
        }

        if (this.clickHandler_RegionDeposit(mouseX, mouseY))
        {
            return;
        }

        if (this.clickHandler_SearchBox(mouseX, mouseY, mouseButton))
        {
            return;
        }

        // Get search mode
        SearchBoxMode searchBoxMode = (SearchBoxMode) AEConfig.instance.settings.getSetting(Settings.SEARCH_MODE);

        // Inform search field of click if auto mode is not on
        if (!(searchBoxMode == SearchBoxMode.AUTOSEARCH || searchBoxMode == SearchBoxMode.NEI_AUTOSEARCH))
        {
            this.searchField.mouseClicked(mouseX - this.guiLeft, mouseY - this.guiTop, mouseButton);
        }

        // Pass to super
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
