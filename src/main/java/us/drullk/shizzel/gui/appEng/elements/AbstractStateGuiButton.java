package us.drullk.shizzel.gui.appEng.elements;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.apache.commons.lang3.text.WordUtils;

import com.google.common.base.Splitter;

import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public abstract class AbstractStateGuiButton extends AbstractGuiButtonBase
{
    private IStateIconTexture backgroundIcon;

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

        this.backgroundIcon = backgroundIcon;
    }

    protected void addAboutToTooltip(List<String> tooltip, String title, String text)
    {
        tooltip.add(EnumChatFormatting.WHITE + title);

        for (String line : Splitter.on("\n").split(WordUtils.wrap(text, 30, "\n", false)))
        {
            tooltip.add(EnumChatFormatting.GRAY + line.trim());
        }
    }

    private void drawScaledTexturedModalRect( final int xPosition, final int yPosition, final int u, final int v, final int width, final int height,
            final int textureWidth, final int textureHeight )
    {
        // No idea what this is
        float magic_number = 0.00390625F;

        // Calculate the UV's
        float minU = u * magic_number;
        float maxU = ( u + textureWidth ) * magic_number;
        float minV = v * magic_number;
        float maxV = ( v + textureHeight ) * magic_number;

        // Get the tessellator
        Tessellator tessellator = Tessellator.instance;

        // Start drawing
        tessellator.startDrawingQuads();

        // Top left corner
        tessellator.addVertexWithUV( xPosition, yPosition + height, this.zLevel, minU, maxV );

        // Top right corner
        tessellator.addVertexWithUV( xPosition + width, yPosition + height, this.zLevel, maxU, maxV );

        // Bottom right corner
        tessellator.addVertexWithUV( xPosition + width, yPosition, this.zLevel, maxU, minV );

        // Bottom left corner
        tessellator.addVertexWithUV( xPosition, yPosition, this.zLevel, minU, minV );

        // Draw
        tessellator.draw();
    }

    protected void drawIcon( final Minecraft minecraftInstance, final IStateIconTexture icon, final int xPos, final int yPos, final int iconWidth,
            final int iconHeight )
    {
        // Bind the sheet
        minecraftInstance.getTextureManager().bindTexture( icon.getTexture() );

        // Draw the icon
        this.drawScaledTexturedModalRect(xPos, yPos, icon.getU(), icon.getV(), iconWidth, iconHeight, icon.getWidth(), icon.getHeight());
    }

    @Override
    public void drawButton( final Minecraft minecraftInstance, final int x, final int y )
    {
        // Full white
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if( this.backgroundIcon != null )
        {
            // Draw the background
            this.drawIcon( minecraftInstance, this.backgroundIcon, this.xPosition, this.yPosition, this.width, this.height );
        }

        if( this.stateIcon != null )
        {
            // Draw the overlay icon
            this.drawIcon( minecraftInstance, this.stateIcon, this.xPosition + this.iconXOffset, this.yPosition + this.iconYOffset, this.width,
                    this.height );
        }

    }
}
