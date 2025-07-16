package dev.boxadactle.neoforge;

import dev.boxadactle.flatedit.FlatEdit;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(FlatEdit.MOD_ID)
public class FlatEditNeoforge {

    public FlatEditNeoforge() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
                (minecraft, screen) -> FlatEdit.createConfigScreen(screen)
        );
    }

    @EventBusSubscriber(modid = FlatEdit.MOD_ID, value = Dist.CLIENT)
    public static class FlatEditEvents {

        @SubscribeEvent
        public static void go(FMLClientSetupEvent e) {
            FlatEdit.init();
        }

    }

}
