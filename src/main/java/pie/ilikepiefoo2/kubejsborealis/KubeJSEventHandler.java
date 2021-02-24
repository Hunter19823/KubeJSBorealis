package pie.ilikepiefoo2.kubejsborealis;

import dev.latvian.kubejs.script.BindingsEvent;
import dev.latvian.kubejs.script.ScriptType;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
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

import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.homeURI;

@Mod.EventBusSubscriber(Dist.DEDICATED_SERVER)
public class KubeJSEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<Class, WebPage> knownPages = new HashMap<Class,WebPage>();

    @SubscribeEvent
    public static void homePageEvent(BorealisHomePageEvent event)
    {
        event.add(new HomePageEntry("KubeJS Documentaion",homeURI,"https://kubejs.latvian.dev/logo_title.png"));
        new BorealisHomePageEventJS(event).post(ScriptType.SERVER,"borealis.homepage");
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
        }else{
            new BorealisPageEventJS(event).post(ScriptType.SERVER,"borealis.page");
        }
    }



    @SubscribeEvent
    public static void onServerStart(FMLServerStartingEvent event)
    {
        //KubeJSHomePage.loadBindings();
    }

    @SubscribeEvent
    public static void bindingsEvent(BindingsEvent event)
    {
        if(event.type == ScriptType.SERVER)
        {
            event.add("borealis",new BorealisWrapper());
            event.add("Borealis",new BorealisWrapper());
            event.add("PageType",PageType.class);
            for(PageType type : PageType.values())
                event.addConstant(type.name().toUpperCase(),type);
            event.add("HttpResponseStatus", HttpResponseStatus.class);
            for(Field field : HttpResponseStatus.class.getDeclaredFields())
                if(Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                    try {
                        event.addConstant(field.getName().toUpperCase(),field.get(null));
                    } catch (IllegalAccessException e) {
                        LOGGER.error(e);
                    }
                }

            event.addFunction("CustomPage", args -> new CustomWebPageBuilder());
            event.addFunction("CustomWebPage", args -> new CustomWebPageBuilder());
            event.addFunction("HTMLPage", args -> new HTTPWebPageBuilder());
            event.addFunction("HTTPWebPage", args -> new HTTPWebPageBuilder());
            event.addFunction("HttpWebPage", args -> new HTTPWebPageBuilder());
            event.addFunction("JSONWebPage", args -> new JSONWebPageBuilder());
            event.addFunction("JsonWebPage", args -> new JSONWebPageBuilder());
            event.addFunction("HomePageEntry", args -> args.length == 2 ? new BorealisHomePageEntryBuilder((String) args[0],(String) args[1]) : new BorealisHomePageEntryBuilder((String) args[0],(String) args[1],(String) args[2]));
            event.addFunction("HomepageEntry", args -> args.length == 2 ? new BorealisHomePageEntryBuilder((String) args[0],(String) args[1]) : new BorealisHomePageEntryBuilder((String) args[0],(String) args[1],(String) args[2]));
            event.addFunction("Homepageentry", args -> args.length == 2 ? new BorealisHomePageEntryBuilder((String) args[0],(String) args[1]) : new BorealisHomePageEntryBuilder((String) args[0],(String) args[1],(String) args[2]));
        }
    }
}