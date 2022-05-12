package pie.ilikepiefoo2.kubejsborealis;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.BorealisHomePageEvent;
import pie.ilikepiefoo2.borealis.BorealisPageEvent;
import pie.ilikepiefoo2.borealis.page.HomePageEntry;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.borealis.page.WebPage;
import pie.ilikepiefoo2.kubejsborealis.builder.BorealisHomePageEntryBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.CustomWebPageBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.HTTPWebPageBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.JSONWebPageBuilder;
import pie.ilikepiefoo2.kubejsborealis.events.BorealisHomePageEventJS;
import pie.ilikepiefoo2.kubejsborealis.events.BorealisPageEventJS;
import pie.ilikepiefoo2.kubejsborealis.pages.ClassPage;
import pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static pie.ilikepiefoo2.kubejsborealis.KubeJSEvents.CUSTOM_PAGE_EVENT;
import static pie.ilikepiefoo2.kubejsborealis.KubeJSEvents.HOMEPAGE_EVENT;
import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.homeURI;

/**
 * @author ILIKEPIEFOO2
 */
@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class KubeJSEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class, WebPage> knownPages = new HashMap<Class,WebPage>();

    @SubscribeEvent
    public static void homePageEvent(BorealisHomePageEvent event)
    {
        if(!ConfigHandler.COMMON.kubejsDocumentation.get().equals(PageType.DISABLED)) {
            event.add(new HomePageEntry("KubeJS Borealis", homeURI, "https://kubejs.latvian.dev/logo_title.png"));
            if(ConfigHandler.COMMON.customPages.get()) {
                new BorealisHomePageEventJS(event).post(ScriptType.SERVER, HOMEPAGE_EVENT);
            }
        }
    }

    @SubscribeEvent
    public static void onPageEvent(BorealisPageEvent event)
    {
        if(event.getSplitUri()[0].equals(homeURI)) {
            if (event.getSplitUri().length == 1)
            {
                event.returnPage(KubeJSHomePage.getInstance());
            }
            else
            {
                try
                {
                    Class c = Class.forName(event.getSplitUri()[1]);
                    if(knownPages.containsKey(c)){
                        event.returnPage(knownPages.get(c));
                    }else {
                        ClassPage page = new ClassPage(c);
                        page.neverDirty();
                        knownPages.put(c,page);
                        event.returnPage(page);
                    }
                }
                catch (Exception ex)
                {
                    LOGGER.warn(ex);
                }
                catch (Error error)
                {
                    LOGGER.error(error);
                }
            }
        }else{
            if(ConfigHandler.COMMON.customPages.get()) {
                new BorealisPageEventJS(event).post(ScriptType.SERVER, CUSTOM_PAGE_EVENT);
            }
        }
    }



    @SubscribeEvent
    public static void onServerStart( ServerStartingEvent event)
    {
        Consumer<ServerStartingEvent> consumer = (fmlServerStartingEvent) -> {
            fmlServerStartingEvent.getServer().close();
        };
        //KubeJSHomePage.loadBindings();
    }


}