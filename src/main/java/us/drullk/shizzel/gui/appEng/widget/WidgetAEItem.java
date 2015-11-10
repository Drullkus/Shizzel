package us.drullk.shizzel.gui.appEng.widget;

import java.util.List;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.render.AppEngRenderItem;
import appeng.util.item.AEItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class WidgetAEItem extends AbstractWidget
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private static final TextureManager TEXTURE_MANAGER = Minecraft.getMinecraft().getTextureManager();

    private final AppEngRenderItem aeItemRenderer;

    private IAEItemStack aeItemStack;

    public WidgetAEItem(IWidgetHost hostGUI, int x, int y, AppEngRenderItem itemRenderer)
    {
        super(hostGUI, x, y);

        this.aeItemRenderer = itemRenderer;
    }

    public IAEItemStack getItemStack()
    {
        return this.aeItemStack;
    }

    @Override
    public void drawWidget()
    {
        if (this.aeItemStack != null)
        {
            this.zLevel = 2.0F;
            this.aeItemRenderer.zLevel = 2.0F;

            this.aeItemRenderer.aeStack = this.aeItemStack;

            this.aeItemRenderer.renderItemAndEffectIntoGUI(WidgetAEItem.MC.fontRenderer, WidgetAEItem.TEXTURE_MANAGER,
                    this.aeItemStack.getItemStack(), this.xPosition + 1, this.yPosition + 1);

            this.aeItemRenderer.renderItemOverlayIntoGUI(WidgetAEItem.MC.fontRenderer, WidgetAEItem.TEXTURE_MANAGER,
                    this.aeItemStack.getItemStack(), this.xPosition + 1, this.yPosition + 1);

            this.zLevel = 0.0F;
            this.aeItemRenderer.zLevel = 0.0F;
        }
    }

    @Override
    public void getTooltip(List<String> tooltip)
    {
        if (this.aeItemStack != null)
        {
            ItemStack stack = this.aeItemStack.getItemStack();

            List<String> stackTooltip = stack.getTooltip(WidgetAEItem.MC.thePlayer, WidgetAEItem.MC.gameSettings.advancedItemTooltips);

            for (int index = 0; index < stackTooltip.size(); index++)
            {
                if (index == 0)
                {
                    stackTooltip.set(index, stack.getRarity().rarityColor + stackTooltip.get(index));
                }
                else
                {
                    stackTooltip.set(index, EnumChatFormatting.GRAY + stackTooltip.get(index));
                }

                tooltip.add(stackTooltip.get(index));
            }

            String modName = ((AEItemStack) this.aeItemStack).getModID();
            modName = modName.substring(0, 1).toUpperCase() + modName.substring(1);

            tooltip.add(EnumChatFormatting.BLUE + "" + EnumChatFormatting.ITALIC + modName);
        }
    }

    @Override
    public void mouseClicked()
    {

    }

    public void setItemStack(final IAEItemStack itemStack)
    {
        this.aeItemStack = itemStack;
    }
}
