package us.drullk.shizzel.utils;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import us.drullk.shizzel.Shizzel;

public enum EnumBlockTextures
{
    BASE(new String[] { "base" }), BUS_COLOR(new String[] {
            "bus.color.border",
            "bus.color.light",
            "bus.color.side" }), CHISELING_TERMINAL(new String[] { "part_chisel_term_dark", // Dark - Chisel Outline
                    "part_chisel_term_colored", // Color - Background
                    "part_chisel_term_bright", // Bright - Chisel Sheen
                    "part_chisel_term_border" }); // Border

    private String[] textureNames;

    private IIcon[] textures;

    public static final EnumBlockTextures[] VALUES = EnumBlockTextures.values();

    EnumBlockTextures(String[] textureNames)
    {
        this.textureNames = textureNames;
        this.textures = new IIcon[this.textureNames.length];
    }

    public void registerTexture(TextureMap textureMap)
    {
        if (textureMap.getTextureType() == 0)
        {
            String header = Shizzel.MOD_ID + ":";

            for (int i = 0; i < this.textureNames.length; i++)
            {
                this.textures[i] = textureMap.registerIcon(header + this.textureNames[i]);
            }
        }
    }

    public IIcon getTexture()
    {
        return this.textures[0];
    }

    public IIcon[] getTextures()
    {
        return this.textures;
    }
}
