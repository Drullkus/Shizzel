package us.drullk.shizzel.rendering;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

public class ModelRegistry {
    public static final ModelRegistry instance = new ModelRegistry();

    private Map<ModelResourceLocation, IBakedModel> modelMap = new HashMap<ModelResourceLocation, IBakedModel>();

    public void register(ModelResourceLocation location, IBakedModel model){
        this.modelMap.put(location, model);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event){
        for (ModelResourceLocation loc : this.modelMap.keySet()){
            event.getModelRegistry().putObject(loc, modelMap.get(loc));
        }
    }
}
