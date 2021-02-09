package pie.ilikepiefoo2.kubejsborealis;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.BorealisHomePageEvent;
import pie.ilikepiefoo2.borealis.BorealisPageEvent;
import pie.ilikepiefoo2.borealis.page.HomePageEntry;
import pie.ilikepiefoo2.borealis.page.WebPage;

import java.util.HashMap;
import java.util.Map;

import static pie.ilikepiefoo2.kubejsborealis.KubeJSHomePage.homeURI;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class KubeJSEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class, WebPage> knownPages = new HashMap<Class,WebPage>();

    @SubscribeEvent
    public static void homePageEvent(BorealisHomePageEvent event)
    {
        event.add(new HomePageEntry("KubeJS Documentaion",homeURI,"https://kubejs.latvian.dev/logo_title.png"));
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
                        WebPage page = new ClassPage(c);
                        knownPages.put(c,page);
                        event.returnPage(page);
                    }
                }
                catch (Exception ex)
                {
                    LOGGER.error(ex);
                }
            }

        }
    }



    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent event)
    {
        KubeJSHomePage.loadBindings();
    }
}