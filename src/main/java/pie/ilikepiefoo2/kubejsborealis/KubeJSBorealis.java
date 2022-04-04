package pie.ilikepiefoo2.kubejsborealis;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.kubejsborealis.util.ReflectionHandler;

import java.util.ArrayList;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.KubeJSBorealis.MOD_ID;

/**
 * @author ILIKEPIEFOO2
 */
@Mod(MOD_ID)
public class KubeJSBorealis {
    private static final Logger LOGGER = LogManager.getLogger(KubeJSBorealis.class);
    public static final String MOD_NAME = "KubeJSBorealis";
    public static final String MOD_ID = "kubejsborealis";
    public static List<Class> eventJSes = new ArrayList<>();

    public KubeJSBorealis()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,ConfigHandler.COMMON_SPEC);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::homePageEvent);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::onPageEvent);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::bindingsEvent);
        MinecraftForge.EVENT_BUS.addListener(this::fmlServerStarting);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(( ModConfigEvent.Loading e) -> ConfigHandler.onConfigLoad());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(( ModConfigEvent.Reloading e) -> ConfigHandler.onConfigLoad());
    }


    private void fmlServerStarting( ServerAboutToStartEvent event)
    {
        if(ConfigHandler.COMMON.reflectionHandler.get()) {
            try {
                LOGGER.info("Configuring ReflectionHandler to current thread...");
                ReflectionHandler.getInstance().configureToCurrentThread();
                LOGGER.info("Reflection Handler configured. Now collecting all EventJS classes");
                eventJSes = ReflectionHandler.getInstance().applyFilter(possibleClass -> EventJS.class.isAssignableFrom(possibleClass));
                LOGGER.info("Events Found >> {} ", eventJSes.size());
            } catch (Throwable e) {
                // If there is an error configuring to current thread...
                LOGGER.error("KJS Borealis could not load reflectionHandler: {}", e);
            }
        }
    }

    public static List<Class> getAllJSEvents()
    {
        return eventJSes;
    }
}
