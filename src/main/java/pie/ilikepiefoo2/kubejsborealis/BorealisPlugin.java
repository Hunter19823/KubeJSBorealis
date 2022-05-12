package pie.ilikepiefoo2.kubejsborealis;

import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.kubejsborealis.builder.BorealisHomePageEntryBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.CustomWebPageBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.HTTPWebPageBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.JSONWebPageBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BorealisPlugin extends dev.latvian.mods.kubejs.KubeJSPlugin {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void addBindings( BindingsEvent event ) {
        if(event.type == ScriptType.SERVER)
        {
            event.add("borealis",new BorealisWrapper());
            event.add("Borealis",new BorealisWrapper());
            event.add("PageType", PageType.class);
            for(PageType type : PageType.values())
                event.add(type.name().toUpperCase(),type);
            if(ConfigHandler.COMMON.customPages.get()) {
                event.add("HttpResponseStatus", HttpResponseStatus.class);
                for(Field field : HttpResponseStatus.class.getDeclaredFields()) {
                    if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                        try {
                            event.add(field.getName().toUpperCase(), field.get(null));
                        } catch (IllegalAccessException e) {
                            LOGGER.error(e);
                        }
                    }
                }
                event.addFunction("CustomPage", args -> new CustomWebPageBuilder());
                event.addFunction("CustomWebPage", args -> new CustomWebPageBuilder());
                event.addFunction("HTMLPage", args -> new HTTPWebPageBuilder());
                event.addFunction("HTTPWebPage", args -> new HTTPWebPageBuilder());
                event.addFunction("HttpWebPage", args -> new HTTPWebPageBuilder());
                event.addFunction("JSONWebPage", args -> new JSONWebPageBuilder());
                event.addFunction("JsonWebPage", args -> new JSONWebPageBuilder());
                event.addFunction("HomePageEntry", args -> args.length == 2
                                                           ? new BorealisHomePageEntryBuilder((String) args[ 0 ], (String) args[ 1 ])
                                                           : new BorealisHomePageEntryBuilder((String) args[ 0 ], (String) args[ 1 ],
                                                                                              (String) args[ 2 ]));
                event.addFunction("HomepageEntry", args -> args.length == 2
                                                           ? new BorealisHomePageEntryBuilder((String) args[ 0 ], (String) args[ 1 ])
                                                           : new BorealisHomePageEntryBuilder((String) args[ 0 ], (String) args[ 1 ],
                                                                                              (String) args[ 2 ]));
                event.addFunction("Homepageentry", args -> args.length == 2
                                                           ? new BorealisHomePageEntryBuilder((String) args[ 0 ], (String) args[ 1 ])
                                                           : new BorealisHomePageEntryBuilder((String) args[ 0 ], (String) args[ 1 ],
                                                                                              (String) args[ 2 ]));
            }
        }
    }
}
