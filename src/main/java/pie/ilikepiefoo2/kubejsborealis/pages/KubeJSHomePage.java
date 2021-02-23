package pie.ilikepiefoo2.kubejsborealis.pages;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptPack;
import dev.latvian.mods.rhino.NativeJavaArray;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.NativeJavaObject;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.Borealis;
import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.tag.Tag;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class KubeJSHomePage extends HTTPWebPage {
    private static final Logger LOGGER = Borealis.LOGGER;
    public static final String homeURI = "kubejs_auto_docs";
    public static final String homeURL = "/"+homeURI+"/";
    public static final Map<String,Class> knownEventJSClasses = new TreeMap<>();

    @Override
    public void body(Tag body)
    {
        LOGGER.debug("Creating Documentation Home Page now...");
        body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
        body.br();
        body.h1("").a("KubeJS Documentation", homeURL);
        /*
        body.text("This section is a WIP. Try starting ")
                .a("here","/kubejs_auto_docs/dev.latvian.kubejs.event.EventsJS")
                .text(". Due to technical issues I will need to manually add events to this page.");
         */

        body.br();
        addTable(body,"KubeJS Events",knownEventJSClasses);
        body.br();
        addTable(body,"Global Objects",global);
        if(startup.size() > 0) {
            body.br();
            addTable(body, "Startup Objects", startup);
        }
        if(client.size() > 0) {
            body.br();
            addTable(body, "Client Objects", client);
        }
        /*
        Tag defaultBindings = body.table();
        Tag header = defaultBindings.tr();
        header.th().a("Event","#events");
        header.th().text("Class");
        LOGGER.debug("Default Bindings currently has: "+DefaultBindings.GLOBAL.size()+" bindings.");
        getAllProperties(KubeJS.clientScriptManager);
        DefaultBindings.GLOBAL.keySet().iterator().forEachRemaining(
                key -> {
                    LOGGER.debug("Found binding \""+key+"\" adding to homepage.");
                    Tag row = defaultBindings.tr();
                    row.td().text(key);
                    linkClass(row.td(),DefaultBindings.GLOBAL.get(key).getClass());
                }
        );

         */



    }
    private static KubeJSHomePage instance;
    public static KubeJSHomePage getInstance()
    {
        if(instance == null)
        {
            loadBindings();
            instance = new KubeJSHomePage();
        }
        return instance;
    }

    private static Map<String,Class> startup;
    private static Map<String,Class> client;
    private static Map<String,Class> global;
    public static void loadBindings()
    {
        client = getAllProperties(KubeJS.clientScriptManager);
        startup = getAllProperties(KubeJS.startupScriptManager);
        merge();
    }
    public static void merge()
    {
        global = new TreeMap<>();
        ArrayList<String> removeKeys = new ArrayList<>();
        for(String key : client.keySet()){
            if(startup.containsKey(key)){
                global.put(key,client.get(key));
                removeKeys.add(key);
            }
        }
        for(String key : removeKeys)
        {
            client.remove(key);
            startup.remove(key);
        }
    }

    public static void addTable(Tag previous,String title,Map<String, Class> classMap)
    {
        previous.br();
        previous.h1("").a(title,"#"+title);
        previous.br();
        Tag table = previous.table();
        Tag header = table.tr();
        header.th().text("KJS Name");
        header.th().text("Type");
        for(String key : classMap.keySet()) {
            Class currentClass = classMap.get(key);
            Tag row = table.tr();
            row.td().text(key);
            row.td().a(currentClass.getName(),homeURL+currentClass.getName());
        }
        previous.br();
    }

    public static Map<String, Class> getAllProperties(ScriptManager manager)
    {
        Map<String, Class> classMap = new TreeMap<String,Class>();
        LOGGER.debug("Now loading all properties of script manager of type "+manager.type.name);
        for(ScriptPack pack : manager.packs.values())
        {
            LOGGER.debug("Now loading script pack");
            Object[] propertyIds = pack.scope.getIds();
            for(Object propertyId : propertyIds)
            {
                if(propertyId != null) {
                    LOGGER.debug("Now loading propertyId of " + propertyId.toString());
                    if (propertyId instanceof String) {
                        Class foundClass = getActualClass(pack.scope.get((String)propertyId,pack.scope));
                        LOGGER.debug("Now loading class "+foundClass.toGenericString());
                        classMap.put((String)propertyId,foundClass);
                    }
                }
            }
        }
        return classMap;
    }

    public static Class getActualClass(Object dummyClass)
    {
        Object unwrapped;
        if(dummyClass instanceof NativeJavaObject){
            unwrapped = ((NativeJavaObject) dummyClass).unwrap();
            if(unwrapped instanceof Class)
                return (Class)unwrapped;
            return unwrapped.getClass();
        }
        if(dummyClass instanceof NativeJavaMap){
            unwrapped = ((NativeJavaMap) dummyClass).unwrap();
            if(unwrapped instanceof Class)
                return (Class)unwrapped;
            return unwrapped.getClass();
        }
        if(dummyClass instanceof NativeJavaArray){
            unwrapped = ((NativeJavaArray) dummyClass).unwrap();
            if(unwrapped instanceof Class)
                return (Class) unwrapped;
            return unwrapped.getClass();
        }
        if(dummyClass instanceof Class)
        {
            return (Class) dummyClass;
        }
        return dummyClass.getClass();
    }
}