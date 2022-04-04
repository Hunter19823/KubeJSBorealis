package pie.ilikepiefoo2.kubejsborealis.util;


import dev.latvian.mods.kubejs.event.EventJS;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.kubejsborealis.ConfigHandler;
import pie.ilikepiefoo2.kubejsborealis.KubeJSBorealis;
import pie.ilikepiefoo2.kubejsborealis.pages.ClassPage;
import pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage;

import javax.lang.model.type.NullType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author ILIKEPIEFOO2
 */
public class ReflectionHandler {
    private List<String> eventClassNames;
    private List<Class> classes;
    private static final Logger LOGGER = LogManager.getLogger(ReflectionHandler.class);
    private static ReflectionHandler INSTANCE;
    public static ReflectionHandler getInstance()
    {
        if(INSTANCE == null) {
            LOGGER.info("Setting up Reflection Handler... (This may take a while)");
            INSTANCE = new ReflectionHandler();
            LOGGER.info("Finished setting up Reflection Handler.");
        }
        return INSTANCE;
    }

    public static void main(String[] args)
    {
        Class test = net.minecraft.client.Minecraft.class;

        ClassPage example = new ClassPage(test);
        ReflectionHandler reflectionHandler = new ReflectionHandler();
        reflectionHandler.configureToCurrentThread();
        KubeJSBorealis.eventJSes = reflectionHandler.applyFilter(possibleClass -> EventJS.class.isAssignableFrom(possibleClass));
        LOGGER.info("Events Found >> {} : {}",KubeJSBorealis.eventJSes.size(), KubeJSBorealis.eventJSes);
        LOGGER.info("Test Class {} has {} inner classes.",test.getTypeName(),test.getDeclaredClasses().length);
        for(Class innerClass : test.getDeclaredClasses()){
            LOGGER.info(innerClass.getTypeName());
        }
        try {
            FileUtils.writeStringToFile(new File("TestPage.html"), example.getContent(), StandardCharsets.UTF_8);
            FileUtils.writeStringToFile(new File("TestHomePage.html"), new KubeJSHomePage().getContent(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReflectionHandler()
    {
        this(new File("mods"));
    }

    public ReflectionHandler(File location)
    {
        eventClassNames = new ArrayList<>();
        try {
            getAllClasses(location);
        } catch (IOException e) {
            LOGGER.error(e);
        }

        LOGGER.info("Total Class Names Found >> {}", eventClassNames.size());
    }

    public List<Class> configureToCurrentThread()
    {
        if(classes == null) {
            LOGGER.info("No configuration currently exists, finding all classes in current thread...");

            classes = eventClassNames.stream().map((className) -> {
                Class tempClass = NullType.class;
                try {
                    if(ConfigHandler.COMMON.printAllClasses.get())
                        LOGGER.info("Attempting To Load {}",className);
                    tempClass = Class.forName(className);
                    if (tempClass.isSynthetic())
                        tempClass = NullType.class;
                } catch (Throwable e) {
                    // If it wasn't loaded just ignore it
                }
                return tempClass;
            }).distinct().collect(Collectors.toList());
        }
        return classes;
    }

    public List<Class> applyFilter(Predicate<? super Class> predicate)
    {
        return classes.stream().filter(predicate).collect(Collectors.toList());
    }

    private void getAllClasses(File directory) throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        eventClassNames = findClassesWithinDir(directory,generateBlacklistRegex(ConfigHandler.COMMON.blacklistedPackages.get().stream().map(String::new).collect(Collectors.toList())),ConfigHandler.COMMON.printAllClasses.get());
        eventClassNames = eventClassNames.stream().distinct().collect(Collectors.toList());
    }
    private static String generateBlacklistRegex(List<String> blacklist)
    {
        String output = "";
        if(blacklist.size()>0) {
            output = "(";
            for (int i = 0; i < blacklist.size(); i++) {
                output += blacklist.get(i).replace(".","\\.");
                if (i != blacklist.size() - 1)
                    output += "|";
            }
            output += ").*";
        }
        LOGGER.info("Generated Blacklist Regex: \"{}\"",output);
        return output;
    }
    private static List findClassesWithinDir(File dir, String blackListRegex, boolean debug)
    {
        List output = new ArrayList();
        if(!dir.exists())
            return output;
        if(dir.isDirectory()){
            for (File file : dir.listFiles())
            {
                output.addAll(findClassesWithinDir(file, blackListRegex,debug));
            }
        }else{

            if(dir.getName().endsWith(".jar")){
                try {
                    new JarFile(dir).stream().forEach(jarEntry -> {
                        String name = jarEntry.getName().replace("/", ".");
                        if(!name.matches(blackListRegex)) {
                            if(debug) LOGGER.info("File Passed Regex: {}",name);
                            if (name.endsWith(".class")) {
                                try {
                                    output.add(name.replace(".class", ""));
                                } catch (Throwable e) {
                                }
                            }
                        }else{
                            if(debug) LOGGER.info("Blacklisted File Removed: {}",name);
                        }

                    });
                } catch (Throwable e) {}
            }
        }
        return output;
    }
}
