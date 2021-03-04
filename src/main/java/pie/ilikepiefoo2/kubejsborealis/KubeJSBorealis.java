package pie.ilikepiefoo2.kubejsborealis;

import dev.latvian.kubejs.event.EventJS;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage;
import pie.ilikepiefoo2.kubejsborealis.util.ReflectionHandler;

import java.util.List;
import java.util.Set;

@Mod("kubejsborealis")
public class KubeJSBorealis {
    private static final Logger LOGGER = LogManager.getLogger(KubeJSBorealis.class);
    public static final String MOD_NAME = "KubeJSBorealis";
    public static final String MOD_ID = "kubejsborealis";
    private static ReflectionHandler reflectionHandler;
    private static List<Class> eventJSes;

    public KubeJSBorealis()
    {
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::homePageEvent);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::onPageEvent);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::onServerStart);
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::bindingsEvent);
        MinecraftForge.EVENT_BUS.addListener(this::fmlServerStarting);
        LOGGER.info("Setting up Reflection Handler... (This may take a while)");
        reflectionHandler = new ReflectionHandler();
        LOGGER.info("Finished setting up Reflection Handler.");
    }


    private void fmlServerStarting(FMLServerAboutToStartEvent event)
    {
        LOGGER.info("Configuring ReflectionHandler to current thread...");
        reflectionHandler.configureToCurrentThread();
        LOGGER.info("Reflection Handler configured. Now collecting all EventJSes");
        eventJSes = reflectionHandler.applyFilter(possibleClass -> EventJS.class.isAssignableFrom(possibleClass));
        LOGGER.info("Events Found >> {} ",eventJSes.size());
    }

    public static List<Class> getAllJSEvents()
    {
        return eventJSes;
    }

    public static ReflectionHandler getReflectionHandler()
    {
        return reflectionHandler;
    }

    /*
     * This method is used to generate a list of all the EventJS class locations using reflection.
     *
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
