package us.drullk.shizzel.gui.appEng;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import appeng.api.config.SearchBoxMode;
import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.TerminalStyle;
import appeng.api.config.ViewItems;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.me.ItemRepo;
import appeng.client.render.AppEngRenderItem;
import appeng.core.AEConfig;
import appeng.util.Platform;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import us.drullk.shizzel.Shizzel;
import us.drullk.shizzel.appEng.PartChiselingTerminal;
import us.drullk.shizzel.container.appEng.ContainerChiselingTerminal;
import us.drullk.shizzel.gui.appEng.elements.*;
import us.drullk.shizzel.gui.appEng.widget.AbstractWidget;
import us.drullk.shizzel.gui.appEng.widget.WidgetAEItem;
import us.drullk.shizzel.networking.appEng.PacketChiselingTerminalServer;
import us.drullk.shizzel.utils.EnumBlockTextures;
import us.drullk.shizzel.utils.GuiHelper;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GUIChiselingTerminal extends AbstractGuiWithScrollbar
        implements ISortSource
{
    protected static final int BUTTON_CLEAR_GRID_ID = 0, BUTTON_CLEAR_GRID_POS_X = 98, BUTTON_CLEAR_GRID_POS_Y = 89;
    protected static final int BUTTON_SORT_ORDER_ID = 1, BUTTON_SORT_ORDER_POS_X = -18, BUTTON_SORT_ORDER_POS_Y = 8;
    protected static final int BUTTON_SORT_DIR_ID = 2, BUTTON_SORT_DIR_POS_X = BUTTON_SORT_ORDER_POS_X, BUTTON_SORT_DIR_POS_Y = BUTTON_SORT_ORDER_POS_Y + 20;
    protected static final int BUTTON_VIEW_TYPE_ID = 3, BUTTON_VIEW_TYPE_POS_X = BUTTON_SORT_ORDER_POS_X, BUTTON_VIEW_TYPE_POS_Y = BUTTON_SORT_DIR_POS_Y + 20;
    protected static final int BUTTON_SEARCH_MODE_ID = 5, BUTTON_SEARCH_MODE_POS_X = BUTTON_SORT_ORDER_POS_X, BUTTON_SEARCH_MODE_POS_Y = BUTTON_VIEW_TYPE_POS_Y + 20;
    protected static final int BUTTON_TERM_STYLE_ID = 6, BUTTON_TERM_STYLE_POS_X = BUTTON_SORT_ORDER_POS_X, BUTTON_TERM_STYLE_POS_Y = BUTTON_SEARCH_MODE_POS_Y + 20;
    protected static final int BUTTON_AE_SIZE = 16;
    protected static final int BUTTON_TINY_SIZE = 8;
    protected static final int GUI_WIDTH = 230;
    protected static final int GUI_HEIGHT = 243;
    protected static final int GUI_VIEW_CELL_TEXTURE_WIDTH = 35;
    protected static final int GUI_VIEW_CELL_TEXTURE_HEIGHT = 104;
    protected static final int GUI_UPPER_TEXTURE_HEIGHT = 35;
    protected static final int GUI_TEXTURE_ROW_V = 35;
    protected static final int GUI_MAIN_BODY_WIDTH = GUI_WIDTH - GUI_VIEW_CELL_TEXTURE_WIDTH;
    protected static final int ME_DEFAULT_ROWS = 3;
    protected static final int ME_COLUMNS = 9;
    protected static final int ME_ITEM_POS_X = 7;
    protected static final int ME_ITEM_POS_Y = 17;
    protected static final int ME_GRID_WIDTH = 161;
    protected static final int ME_ROW_HEIGHT = 18;
    protected static final int GUI_TEXTURE_LOWER_HEIGHT = GUI_HEIGHT - ME_ROW_HEIGHT - GUI_UPPER_TEXTURE_HEIGHT;
    protected static final int SCROLLBAR_POS_X = 175;
    protected static final int SCROLLBAR_POS_Y = 18;
    protected static final int SCROLLBAR_HEIGHT = 52;
    protected static final int SEARCH_POS_X = 98;
    protected static final int SEARCH_POS_Y = 6;
    protected static final int SEARCH_WIDTH = 65;
    protected static final int SEARCH_HEIGHT = 10;
    protected static final int SEARCH_MAX_CHARS = 15;
    protected static final int TITLE_POS_X = 8;
    protected static final int TITLE_POS_Y = 6;
    protected static final long WIDGET_TOOLTIP_UPDATE_INTERVAL = 3000L;
    private AppEngRenderItem aeItemRenderer = new AppEngRenderItem();
    private String guiTitle;
    private int widgetCount = ME_DEFAULT_ROWS * ME_COLUMNS;
    private List<WidgetAEItem> itemWidgets = new ArrayList<WidgetAEItem>();
    private EntityPlayer player;
    private GuiTextField searchField;
    private final ItemRepo repo;
    private SortOrder sortingOrder = SortOrder.NAME;
    private SortDir sortingDirection = SortDir.ASCENDING;
    private ViewItems viewMode = ViewItems.ALL;
    private TerminalStyle terminalStyle = TerminalStyle.SMALL;
    private int lowerTerminalYOffset = 0;
    private int previousMouseX = 0;
    private int previousMouseY = 0;
    private WidgetAEItem previousWidgetUnderMouse = null;
    private long lastTooltipUpdateTime = 0;
    private int numberOfWidgetRows = ME_DEFAULT_ROWS;
    private GUIButtonSortingMode btnSortingMode;
    private GUIButtonSortingDirection btnSortingDirection;
    private GUIButtonViewType btnViewType;
    private GUIButtonSearchMode btnSearchMode;

    private boolean viewNeedsUpdate = true;

    public GUIChiselingTerminal(PartChiselingTerminal part, EntityPlayer player )
    {
        // Call super
        super(new ContainerChiselingTerminal( part, player ));

        // Set the player
        this.player = player;

        // Set the width and height
        this.xSize = GUI_WIDTH;
        this.ySize = GUI_HEIGHT;

        // Set the title
        this.guiTitle = "shizzel.gui.chiselingterminal.name";

        // Create the repo
        this.repo = new ItemRepo( this.scrollBar, this );

        // Get the terminal style
        this.terminalStyle = (TerminalStyle)AEConfig.instance.getConfigManager().getSetting( Settings.TERMINAL_STYLE );

    }

    /**
     * Checks if the click was a region deposit.
     *
     * @param mouseX
     * @param mouseY
     * @return True if click was handled.
     */
    private boolean clickHandler_RegionDeposit( final int mouseX, final int mouseY )
    {
        // Is the player holding the space key?
        if( Keyboard.isKeyDown( Keyboard.KEY_SPACE ) )
        {
            // Get the slot the mouse is over
            Slot slotClicked = this.getSlotAtPosition( mouseX, mouseY );

            // Was there a slot under the mouse?
            if( slotClicked != null )
            {
                // Ask the server to move the inventory
                new PacketChiselingTerminalServer().createRequestDepositRegion( this.player, slotClicked.slotNumber ).sendPacketToServer();

                // Do not pass to super
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the click was inside the search box.
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     * @return True if click was handled.
     */
    private boolean clickHandler_SearchBox( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Was the mouse right-clicked over the search field?
        if( ( mouseButton == GuiHelper.MOUSE_BUTTON_RIGHT ) &&
                GuiHelper.INSTANCE.isPointInGuiRegion( SEARCH_POS_Y, SEARCH_POS_X,
                        SEARCH_HEIGHT, SEARCH_WIDTH, mouseX, mouseY, this.guiLeft, this.guiTop ) )
        {
            // Clear the search field
            this.searchField.setText( "" );

            // Update the repo
            this.repo.searchString = "";

            // Repo needs update
            this.viewNeedsUpdate = true;

            // Inform search field.
            this.searchField.mouseClicked( mouseX - this.guiLeft, mouseY - this.guiTop, mouseButton );

            // Do not pass to super
            return true;
        }

        return false;
    }

    /**
     * Checks if the click was inside the widgets.
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     * @return True if click was handled.
     */
    private boolean clickHandler_Widgets( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Was the click inside the ME grid?
        if( GuiHelper.INSTANCE.isPointInGuiRegion( ME_ITEM_POS_Y, ME_ITEM_POS_X,
                this.numberOfWidgetRows * ME_ROW_HEIGHT, ME_GRID_WIDTH, mouseX, mouseY, this.guiLeft,
                this.guiTop ) )
        {
            // Click + empty hand is extract
            boolean doExtract = ( this.player.inventory.getItemStack() == null );

            // Shift+Right click is extract
            doExtract |= ( Keyboard.isKeyDown( Keyboard.KEY_LSHIFT ) && ( mouseButton == GuiHelper.MOUSE_BUTTON_RIGHT ) );

            // Extracting?
            if( doExtract )
            {
                // Search for the widget the mouse is over, and send extract request.
                this.sendItemWidgetClicked( mouseX, mouseY, mouseButton );
            }
            else
            {
                // Inform the server the user would like to deposit the currently held item into the ME network.
                new PacketChiselingTerminalServer().createRequestDeposit( this.player, mouseButton ).sendPacketToServer();
            }

            // Do not pass to super
            return true;
        }

        return false;
    }

    /**
     * Extracts or inserts an item to/from the player held stack based on the
     * direction the mouse wheel was scrolled.
     *
     * @param deltaZ
     */
    private void doMEWheelAction( final int deltaZ )
    {
        // Get the mouse position
        int mouseX = ( Mouse.getEventX() * this.width ) / this.mc.displayWidth;
        int mouseY = this.height - ( ( Mouse.getEventY() * this.height ) / this.mc.displayHeight ) - 1;

        // Is the mouse inside the ME area?
        if( GuiHelper.INSTANCE.isPointInGuiRegion( ME_ITEM_POS_Y, ME_ITEM_POS_X,
                this.numberOfWidgetRows * ME_ROW_HEIGHT, ME_GRID_WIDTH, mouseX, mouseY, this.guiLeft,
                this.guiTop ) )
        {
            // Which direction was the scroll?
            if( deltaZ > 0 )
            {
                // Is the player holding anything?
                if( this.player.inventory.getItemStack() != null )
                {
                    // Inform the server the user would like to deposit 1 of the currently held items into the ME network.
                    new PacketChiselingTerminalServer().createRequestDeposit( this.player, GuiHelper.MOUSE_WHEEL_MOTION ).sendPacketToServer();
                }
            }
            else
            {
                // Extract an item based on the widget we are over
                this.sendItemWidgetClicked( mouseX, mouseY, GuiHelper.MOUSE_WHEEL_MOTION );

            }
        }
    }

    /**
     * Draws the AE item widgets.
     *
     * @param mouseX
     * @param mouseY
     * @return
     */
    private WidgetAEItem drawItemWidgets( final int mouseX, final int mouseY )
    {
        boolean hasNoOverlay = true;

        WidgetAEItem widgetUnderMouse = null;

        // Draw the item widgets
        for( int index = 0; index < this.widgetCount; ++index )
        {
            // Get the widget
            WidgetAEItem currentWidget = this.itemWidgets.get( index );

            // Draw the widget
            currentWidget.drawWidget();

            // Is the mouse over this widget?
            if( hasNoOverlay && currentWidget.isMouseOverWidget( mouseX, mouseY ) )
            {
                // Draw the overlay
                currentWidget.drawMouseHoverUnderlay();

                // Set that we have an overlay
                hasNoOverlay = false;

                // Set this widget as the widget under the mouse
                widgetUnderMouse = currentWidget;
            }
        }

        return widgetUnderMouse;
    }

    /**
     * If the user has clicked on an item widget this will inform the server
     * so that the item can be extracted from the AE network.
     *
     * @param mouseX
     * @param mouseY
     * @param mouseButton
     */
    private void sendItemWidgetClicked( final int mouseX, final int mouseY, final int mouseButton )
    {
        for( int index = 0; index < this.widgetCount; ++index )
        {
            // Get the widget
            WidgetAEItem currentWidget = this.itemWidgets.get( index );

            // Is the mouse over this widget
            if( currentWidget.isMouseOverWidget( mouseX, mouseY ) )
            {
                // Get the AE itemstack this widget represents
                IAEItemStack widgetStack = currentWidget.getItemStack();

                // Did we get an item?
                if( widgetStack != null )
                {
                    // Should the item be crafted?
                    if( widgetStack.getStackSize() == 0 )
                    {
                        if( widgetStack.isCraftable() )
                        {
                            // TODO: Get with the AE2 team to see if I can get this working.
                            //new PacketServerArcaneCraftingTerminal().createRequestAutoCraft( this.player, widgetStack ).sendPacketToServer();
                        }
                    }
                    else
                    {
                        // Get the state of the shift keys
                        boolean isShiftHeld = Keyboard.isKeyDown( Keyboard.KEY_LSHIFT ) || Keyboard.isKeyDown( Keyboard.KEY_RSHIFT );

                        // Let the server know the user is requesting an itemstack.
                        new PacketChiselingTerminalServer().createRequestExtract( this.player, widgetStack, mouseButton, isShiftHeld )
                                .sendPacketToServer();
                    }
                }

                // Stop searching
                return;
            }
        }
    }

    /**
     * Calculates the number of rows needed based on terminal style, and updates
     * the gui parameters.
     */
    private void setupTerminalStyle()
    {
        int extraRows = 0;

        // Tall?
        if( this.terminalStyle == TerminalStyle.TALL )
        {
            extraRows = Math.max( 0, ( ( this.height - GUI_HEIGHT ) / 18 ) - 3 );
        }

        // Update the size and top of the GUI
        this.ySize = GUI_HEIGHT + ( extraRows * ME_ROW_HEIGHT );
        this.guiTop = ( this.height - this.ySize ) / 2;

        // Update the number of rows
        this.numberOfWidgetRows = ME_DEFAULT_ROWS + extraRows;

        // Update number of widgets
        this.widgetCount = this.numberOfWidgetRows * ME_COLUMNS;

        // Clear old widgets
        this.itemWidgets.clear();

        // Create the widgets and bridge slots
        for( int row = 0; row < this.numberOfWidgetRows; ++row )
        {
            for( int column = 0; column < ME_COLUMNS; ++column )
            {
                // Calculate the index and position
                int index = ( row * ME_COLUMNS ) + column;
                int posX = ME_ITEM_POS_X + ( column * AbstractWidget.WIDGET_SIZE );
                int posY = ME_ITEM_POS_Y + ( row * AbstractWidget.WIDGET_SIZE );

                // Create the ME slot
                this.itemWidgets.add( new WidgetAEItem( this, posX, posY, this.aeItemRenderer ) );
            }
        }

        // Update the scroll bar range
        this.updateScrollbarRange();

        // Update the scroll bar height
        this.setScrollBarHeight( SCROLLBAR_HEIGHT + ( extraRows * ME_ROW_HEIGHT ) );

        // Update the lower terminal portion Y offset
        int prevYOffset = this.lowerTerminalYOffset;
        this.lowerTerminalYOffset = ( extraRows * ME_ROW_HEIGHT );

        // Update the container
        if( prevYOffset != this.lowerTerminalYOffset )
        {
            ( (ContainerChiselingTerminal)this.inventorySlots ).changeSlotsYOffset( this.lowerTerminalYOffset - prevYOffset );
        }

        // Clear any tooltip
        this.tooltip.clear();

    }

    /**
     * Assigns the network items to the widgets
     */
    private void updateMEWidgets()
    {
        int repoIndex = 0;
        // List all items
        for( int index = 0; index < this.widgetCount; ++index )
        {
            IAEItemStack stack = this.repo.getReferenceItem( repoIndex++ );

            // Did we get a stack?
            if( stack != null )
            {
                // TODO: Prevent craftable items from being shown until AE2 team accepts pull request.
                if( stack.getStackSize() == 0 )
                {
                    index-- ;
                    continue;
                }

                // Set the item
                this.itemWidgets.get( index ).setItemStack( stack );
            }
            else
            {
                // Set to null
                this.itemWidgets.get( index ).setItemStack( null );
            }
        }
    }

    private void updateScrollbarRange()
    {
        // TODO: This needs some work to prevent overscroll
        // Calculate the total number of rows needed to display ALL items
        int totalNumberOfRows = (int)Math.ceil( this.repo.size() / (double)ME_COLUMNS );

        // Calculate the scroll based on how many rows can be shown
        int max = Math.max( 0, totalNumberOfRows - this.numberOfWidgetRows );

        // Update the scroll bar
        this.scrollBar.setRange( 0, max, 2 );
    }

    /**
     * Updates the sorting modes and refreshes the gui.
     */
    private void updateSorting()
    {
        // Set the direction icon
        this.btnSortingDirection.setSortingDirection( this.sortingDirection );

        // Set the order icon
        this.btnSortingMode.setSortMode( this.sortingOrder );

        // Set the view mode
        this.btnViewType.setViewMode( this.viewMode );

        // Repo needs update
        this.viewNeedsUpdate = true;

    }

    /**
     * Updates the repo's view, the scroll bar range, and the widgets.
     */
    private void updateView()
    {
        // Mark clean
        this.viewNeedsUpdate = false;

        // Update repo view
        this.repo.updateView();

        // Update the scroll bar
        this.updateScrollbarRange();

        // Update the widgets
        this.updateMEWidgets();
    }

    /**
     * Draws the gui texture
     */
    @Override
    protected void drawGuiContainerBackgroundLayer( final float alpha, final int mouseX, final int mouseY )
    {
        // Does the view need updating?
        if( this.viewNeedsUpdate )
        {
            this.updateView();
        }

        // Full white
        GL11.glColor4f( 1.0F, 1.0F, 1.0F, 1.0F );

        // Set the texture
        this.mc.renderEngine.bindTexture(new ResourceLocation(Shizzel.MOD_ID,"textures/gui/chisel_terminal.png"));

        // Draw the upper portion: Label, Search, First row
        this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, GUI_MAIN_BODY_WIDTH,
                GUI_UPPER_TEXTURE_HEIGHT );

        // Draw the extra rows
        for( int i = 0; i < ( this.numberOfWidgetRows - ME_DEFAULT_ROWS ); ++i )
        {
            int yPos = this.guiTop + GUI_UPPER_TEXTURE_HEIGHT + ( i * ME_ROW_HEIGHT );

            // Draw the texture
            this.drawTexturedModalRect( this.guiLeft, yPos, 0, GUI_TEXTURE_ROW_V,
                    GUI_MAIN_BODY_WIDTH, ME_ROW_HEIGHT );
        }

        // Draw the lower portion, bottom two rows, crafting grid, player inventory
        this.drawTexturedModalRect( this.guiLeft, this.guiTop + GUI_UPPER_TEXTURE_HEIGHT + this.lowerTerminalYOffset, 0,
                ME_ROW_HEIGHT + 17, GUI_MAIN_BODY_WIDTH,
                GUI_TEXTURE_LOWER_HEIGHT + 18 );

        // Draw view cells
        this.drawTexturedModalRect( this.guiLeft + GUI_MAIN_BODY_WIDTH, this.guiTop,
                GUI_MAIN_BODY_WIDTH, 0, GUI_VIEW_CELL_TEXTURE_WIDTH,
                GUI_VIEW_CELL_TEXTURE_HEIGHT );

        // Bind the AE states texture
        Minecraft.getMinecraft().renderEngine.bindTexture( AEStateIconsEnum.AE_STATES_TEXTURE );

        // Draw the view cell backgrounds
        int u = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getU(), v = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getV();
        int h = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getHeight(), w = AEStateIconsEnum.VIEW_CELL_BACKGROUND.getWidth();
        int x = this.guiLeft + ContainerChiselingTerminal.VIEW_SLOT_XPOS, y = this.guiTop +
                ContainerChiselingTerminal.VIEW_SLOT_YPOS;
        for( int row = 0; row < 5; row++ )
        {
            this.drawTexturedModalRect( x, y + ( row * 18 ), u, v, w, h );
        }
    }

    /**
     * Draw the foreground layer.
     */
    @Override
    protected void drawGuiContainerForegroundLayer( final int mouseX, final int mouseY )
    {
        // Call super
        super.drawGuiContainerForegroundLayer( mouseX, mouseY );

        // Draw the title
        this.fontRendererObj.drawString( this.guiTitle, TITLE_POS_X, TITLE_POS_Y, 0x000000 );

        // Draw the search field.
        this.searchField.drawTextBox();

        // Enable lighting
        GL11.glEnable( GL11.GL_LIGHTING );

        // Draw the widgets and get which one the mouse is over
        WidgetAEItem widgetUnderMouse = this.drawItemWidgets( mouseX, mouseY );

        // Should we force a tooltip update?
        boolean forceTooltipUpdate = ( ( System.currentTimeMillis() - this.lastTooltipUpdateTime ) >= WIDGET_TOOLTIP_UPDATE_INTERVAL );

        // Has the mouse moved, or timeout reached?
        if( forceTooltipUpdate || ( this.previousMouseX != mouseX ) || ( this.previousMouseY != mouseY ) )
        {
            // Do we have a widget under the mouse?
            if( widgetUnderMouse != null )
            {
                // Has the widget changed?
                if( forceTooltipUpdate || ( widgetUnderMouse != this.previousWidgetUnderMouse ) )
                {
                    // Clear the tooltip
                    this.tooltip.clear();

                    // Get the tooltip from the widget
                    widgetUnderMouse.getTooltip( this.tooltip );

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
                this.addTooltipFromButtons( mouseX, mouseY );
            }

            // Set the previous position
            this.previousMouseX = mouseX;
            this.previousMouseY = mouseY;

            // Set the previous widget
            this.previousWidgetUnderMouse = widgetUnderMouse;
        }

        // Draw the tooltip
        this.drawTooltip( mouseX - this.guiLeft, mouseY - this.guiTop, false );
    }

    /**
     * Returns the scroll bar parameters.
     *
     * @return
     */
    @Override
    protected ScrollbarParams getScrollbarParameters()
    {
        return new ScrollbarParams(
                SCROLLBAR_POS_X,
                SCROLLBAR_POS_Y,
                SCROLLBAR_HEIGHT +
                        ( ( ME_DEFAULT_ROWS - this.numberOfWidgetRows ) * ME_ROW_HEIGHT ) );
    }

    /**
     * Called when the player types a key.
     */
    @Override
    protected void keyTyped( final char key, final int keyID )
    {
        // Did they press the escape key?
        if( keyID == Keyboard.KEY_ESCAPE )
        {
            // Close the screen.
            this.mc.thePlayer.closeScreen();
            return;
        }

        // Prevent only spaces
        if( ( key == ' ' ) && ( this.searchField.getText().length() == 0 ) )
        {
            return;
        }

        if( this.searchField.textboxKeyTyped( key, keyID ) )
        {
            // Get the search query
            String newSearch = this.searchField.getText().trim().toLowerCase();

            // Has the query changed?
            if( !newSearch.equals( this.repo.searchString ) )
            {
                // Set the search string
                this.repo.searchString = newSearch;

                // Repo needs update
                this.viewNeedsUpdate = true;
            }
        }
        else
        {
            super.keyTyped( key, keyID );
        }

    }

    /**
     * Called when the mouse is clicked while the gui is open
     */
    @Override
    protected void mouseClicked( final int mouseX, final int mouseY, final int mouseButton )
    {
        // Handled by the widget area?
        if( clickHandler_Widgets( mouseX, mouseY, mouseButton ) )
        {
            return;
        }

        // Handled by region deposit?
        if( clickHandler_RegionDeposit( mouseX, mouseY ) )
        {
            return;
        }

        // Handled by search box?
        if( clickHandler_SearchBox( mouseX, mouseY, mouseButton ) )
        {
            return;
        }

        // Get search mode
        SearchBoxMode searchBoxMode = (SearchBoxMode)AEConfig.instance.settings.getSetting( Settings.SEARCH_MODE );

        // Inform search field of click if auto mode is not on
        if( !( searchBoxMode == SearchBoxMode.AUTOSEARCH || searchBoxMode == SearchBoxMode.NEI_AUTOSEARCH ) )
        {
            this.searchField.mouseClicked( mouseX - this.guiLeft, mouseY - this.guiTop, mouseButton );
        }

        // Pass to super
        super.mouseClicked( mouseX, mouseY, mouseButton );
    }

    @Override
    protected void onButtonClicked( final GuiButton button, final int mouseButton )
    {
        boolean sortingChanged = false;

        boolean wasLeftClick = true;

        // Which button was clicked?
        switch ( mouseButton )
        {
        case GuiHelper.MOUSE_BUTTON_LEFT:
            // Already true
            break;

        case GuiHelper.MOUSE_BUTTON_RIGHT:
            // Set to false
            wasLeftClick = false;
            break;

        default:
            // Don't handle any other buttons
            return;
        }

        switch ( button.id )
        {
        // Sort order
        case BUTTON_SORT_ORDER_ID:
            switch ( this.sortingOrder )
            {
            case AMOUNT:
                this.sortingOrder = ( wasLeftClick ? SortOrder.MOD : SortOrder.NAME );
                break;

            case INVTWEAKS:
                break;

            case MOD:
                this.sortingOrder = ( wasLeftClick ? SortOrder.NAME : SortOrder.AMOUNT );
                break;

            case NAME:
                this.sortingOrder = ( wasLeftClick ? SortOrder.AMOUNT : SortOrder.MOD );
                break;
            }
            sortingChanged = true;
            break;

        // Sorting direction
        case BUTTON_SORT_DIR_ID:
            switch ( this.sortingDirection )
            {
            case ASCENDING:
                this.sortingDirection = SortDir.DESCENDING;
                break;

            case DESCENDING:
                this.sortingDirection = SortDir.ASCENDING;
                break;

            }
            sortingChanged = true;
            break;

        // View type
        case BUTTON_VIEW_TYPE_ID:

            // Rotate view mode
            this.viewMode = Platform.rotateEnum( this.viewMode, !wasLeftClick, Settings.VIEW_MODE.getPossibleValues() );

            sortingChanged = true;
            break;

        // Terminal style
        case BUTTON_TERM_STYLE_ID:
            switch ( this.terminalStyle )
            {
            case SMALL:
                this.terminalStyle = TerminalStyle.TALL;
                break;

            case TALL:
                this.terminalStyle = TerminalStyle.SMALL;
                break;

            default:
                this.terminalStyle = TerminalStyle.SMALL;
                break;

            }

            // Update the AE settings
            AEConfig.instance.getConfigManager().putSetting( Settings.TERMINAL_STYLE, this.terminalStyle );

            // Reinit
            this.initGui();

            break;

        // Search mode
        case BUTTON_SEARCH_MODE_ID:
            // Rotate search mode
            SearchBoxMode searchBoxMode = (SearchBoxMode)AEConfig.instance.settings.getSetting( Settings.SEARCH_MODE );
            searchBoxMode = Platform.rotateEnum( searchBoxMode, !wasLeftClick, Settings.SEARCH_MODE.getPossibleValues() );

            // Set focus
            this.searchField.setFocused( ( searchBoxMode == SearchBoxMode.AUTOSEARCH ) || ( searchBoxMode == SearchBoxMode.NEI_AUTOSEARCH ) );

            // Update the settings
            AEConfig.instance.settings.putSetting( Settings.SEARCH_MODE, searchBoxMode );

            // Update the button
            this.btnSearchMode.setSearchMode( searchBoxMode );

            // Clear the tooltip
            this.tooltip.clear();
            this.lastTooltipUpdateTime = 0;

            break;
        }

        // Was the sorting mode changed?
        if( sortingChanged )
        {
            // Update the sorting
            this.updateSorting();

            // Reset the tooltip
            this.lastTooltipUpdateTime = 0;

            // Send to server
            new PacketChiselingTerminalServer().createRequestSetSort( this.player, this.sortingOrder, this.sortingDirection, this.viewMode )
                    .sendPacketToServer();
        }
    }

    @Override
    protected void onScrollbarMoved()
    {
        // Update the widgets
        this.updateMEWidgets();

        // Clear the tooltip
        this.tooltip.clear();
        this.lastTooltipUpdateTime = 0;
    }

    /**
     * Draws all screen elements, specifically calling on TC to draw the aspects
     * of whatever the mouse is over.
     */
    @Override
    public void drawScreen( final int mouseX, final int mouseY, final float mouseBtn )
    {
        // Call super
        super.drawScreen( mouseX, mouseY, mouseBtn );

    }

    /**
     * Gets the sorting order.
     */
    @Override
    public Enum getSortBy()
    {
        return this.sortingOrder;
    }

    /**
     * Gets the sorting direction.
     */
    @Override
    public Enum getSortDir()
    {
        return this.sortingDirection;
    }

    /**
     * Gets what items (stored vs crafting) to show.
     */
    @Override
    public Enum getSortDisplay()
    {
        return this.viewMode;
    }

    /**
     * If the mouse wheel has moved, passes the data to the scrollbar
     */
    @Override
    public void handleMouseInput()
    {
        // Get the delta z for the scroll wheel
        int deltaZ = Mouse.getEventDWheel();

        // Did it move?
        if( deltaZ != 0 )
        {

            // Is the mouse inside of, or to the left of, the GUI?
            int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
            if( mouseX <= ( this.guiLeft + GUI_WIDTH ) )
            {
                // Is shift being held?
                if( Keyboard.isKeyDown( Keyboard.KEY_LSHIFT ) || Keyboard.isKeyDown( Keyboard.KEY_RSHIFT ) )
                {
                    // Extract or insert based on the motion of the wheel
                    this.doMEWheelAction( deltaZ );
                }
                else
                {
                    // Inform the scroll bar
                    this.scrollBar.wheel( deltaZ );

                    // Update the item widgets
                    this.onScrollbarMoved();
                }

                return;
            }
        }

        // Call super
        super.handleMouseInput();
    }

    /**
     * Sets the gui up.
     */
    @Override
    public void initGui()
    {
        // Call super
        super.initGui();

        // Reset the mouse wheel state.
        Mouse.getDWheel();

        // Enable repeat keys
        Keyboard.enableRepeatEvents( true );

        // Calculate row count
        this.setupTerminalStyle();

        // Get the current search box mode
        SearchBoxMode searchBoxMode = (SearchBoxMode)AEConfig.instance.settings.getSetting( Settings.SEARCH_MODE );

        if( this.searchField == null )
        {
            // Set up the search bar
            this.searchField = new GuiTextField( this.fontRendererObj, SEARCH_POS_X, SEARCH_POS_Y,
                    SEARCH_WIDTH, SEARCH_HEIGHT );

            // Set the search field to draw in the foreground
            this.searchField.setEnableBackgroundDrawing( false );

            // Set maximum length
            this.searchField.setMaxStringLength( SEARCH_MAX_CHARS );
        }

        // Start focused?
        this.searchField.setFocused((searchBoxMode == SearchBoxMode.AUTOSEARCH) || (searchBoxMode == SearchBoxMode.NEI_AUTOSEARCH));

        // Set the search string
        this.searchField.setText(this.repo.searchString);

        // Clear any existing buttons
        this.buttonList.clear();

        // Add sort order button
        this.buttonList.add( this.btnSortingMode = new GUIButtonSortingMode( BUTTON_SORT_ORDER_ID, this.guiLeft +
                BUTTON_SORT_ORDER_POS_X, this.guiTop + BUTTON_SORT_ORDER_POS_Y,
                BUTTON_AE_SIZE, BUTTON_AE_SIZE ) );

        // Add sort direction button
        this.buttonList.add( this.btnSortingDirection = new GUIButtonSortingDirection( BUTTON_SORT_DIR_ID, this.guiLeft +
                BUTTON_SORT_DIR_POS_X, this.guiTop + BUTTON_SORT_DIR_POS_Y,
                BUTTON_AE_SIZE, BUTTON_AE_SIZE ) );

        // Add view type button
        this.buttonList.add( this.btnViewType = new GUIButtonViewType( BUTTON_VIEW_TYPE_ID, this.guiLeft +
                BUTTON_VIEW_TYPE_POS_X, this.guiTop + BUTTON_VIEW_TYPE_POS_Y,
                BUTTON_AE_SIZE, BUTTON_AE_SIZE ) );

        // Add search mode button
        this.buttonList.add( this.btnSearchMode = new GUIButtonSearchMode( BUTTON_SEARCH_MODE_ID, this.guiLeft +
                BUTTON_SEARCH_MODE_POS_X, this.guiTop + BUTTON_SEARCH_MODE_POS_Y,
                BUTTON_AE_SIZE, BUTTON_AE_SIZE, searchBoxMode ) );

        // Add terminal style button
        this.buttonList.add( new GUIButtonTerminalStyle( BUTTON_TERM_STYLE_ID, this.guiLeft +
                BUTTON_TERM_STYLE_POS_X, this.guiTop + BUTTON_TERM_STYLE_POS_Y,
                BUTTON_AE_SIZE, BUTTON_AE_SIZE, this.terminalStyle ) );

        // Add the container as a listener
        ( (ContainerPartArcaneCraftingTerminal)this.inventorySlots ).registerForUpdates();

        // Request a full update from the server
        new PacketChiselingTerminalServer().createRequestFullList( this.player ).sendPacketToServer();

    }

    /**
     * Called when the gui is closing.
     */
    @Override
    public void onGuiClosed()
    {
        // Call super
        super.onGuiClosed();

        // Disable repeat keys
        Keyboard.enableRepeatEvents( false );
    }

    /**
     * Called to update the amount of an item in the ME network.
     *
     * @param change
     */
    public void onReceiveChange( final IAEItemStack change )
    {
        // Update the repository
        this.repo.postUpdate( change );

        // Repo needs update
        this.viewNeedsUpdate = true;
    }

    /**
     * Called when the server sends a full list of all
     * items in the AE network in response to our request.
     *
     * @param itemList
     */
    public void onReceiveFullList( final IItemList<IAEItemStack> itemList )
    {
        // Update the repository
        for( IAEItemStack stack : itemList )
        {
            this.repo.postUpdate( stack );
        }

        // Repo needs update
        this.viewNeedsUpdate = true;
    }

    /**
     * Called to update what the player is holding.
     *
     * @param heldItemstack
     */
    public void onReceivePlayerHeld( final IAEItemStack heldItemstack )
    {
        ItemStack itemStack = null;

        // Get the stack
        if( heldItemstack != null )
        {
            itemStack = heldItemstack.getItemStack();
        }

        // Set what the player is holding
        this.player.inventory.setItemStack( itemStack );
    }

    /**
     * Called when the server sends the sorting order and direction.
     *
     * @param order
     * @param direction
     */
    public void onReceiveSorting( final SortOrder order, final SortDir direction, final ViewItems viewMode )
    {
        // Set the direction
        this.sortingDirection = direction;

        // Set the order
        this.sortingOrder = order;

        // Set view mode
        this.viewMode = viewMode;

        // Update
        this.updateSorting();
    }

    /**
     * Called when the server wants the client to force an update to the aspect
     * costs.
     */
    public void onServerSendForceUpdateCost()
    {
        ( (ContainerPartArcaneCraftingTerminal)this.inventorySlots ).getCraftingCost( true );
    }

    /**
     * Called when the view cells change.
     */
    public void onViewCellsChanged( final ItemStack[] viewCells )
    {
        // Update the repo
        this.repo.setViewCell( viewCells );

        // Repo needs update
        this.viewNeedsUpdate = true;
    }

}
