package pie.ilikepiefoo2.kubejsborealis;

import dev.latvian.kubejs.event.EventJS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage;
import pie.ilikepiefoo2.kubejsborealis.util.ReflectionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModConfig.Loading e) -> ConfigHandler.onConfigLoad());
        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModConfig.Reloading e) -> ConfigHandler.onConfigLoad());
    }


    private void fmlServerStarting(FMLServerAboutToStartEvent event)
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

    /*
     * This method is used to generate a list of all the EventJS class locations using reflection.
     * This is purely for testing.
     */
    public static void main(String[] args)
    {
        Reflections reflections = new Reflections("dev.latvian.kubejs",new SubTypesScanner(false), new ResourcesScanner());
        Set<Class<? extends EventJS>> classes = reflections.getSubTypesOf(EventJS.class);
        for(Class eventClass : classes)
        {
            String link = "https://github.com/KubeJS-Mods/KubeJS/tree/master/common/src/main/java/"+eventClass.getName().replaceAll("\\.","/")+".java";
            System.out.println(KubeJSHomePage.class.getSimpleName()+".knownEventJSClasses.put(\""+eventClass.getSimpleName()+"\", "+eventClass.getCanonicalName()+".class);");
        }
    }
}
