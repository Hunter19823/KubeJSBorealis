package pie.ilikepiefoo2.kubejsborealis.util;

import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.kubejsborealis.pages.ClassPage;

import javax.lang.model.type.NullType;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ReflectionHandler {
    private List<String> eventClassNames;
    private List<Class> classes;
    private static final Logger LOGGER = LogManager.getLogger(ReflectionHandler.class);

    public static void main(String[] args)
    {
        LOGGER.info(new ClassPage(dev.latvian.kubejs.bindings.BlockWrapper.class).getContent());
        try {
            FileUtils.writeStringToFile(new File("TestPage.html"), new ClassPage(dev.latvian.kubejs.bindings.BlockWrapper.class).getContent(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReflectionHandler()
    {
        eventClassNames = new ArrayList<>();
        try {
            getAllClasses();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("Total Class Names Found >> {}", eventClassNames.size());
    }

    public List<Class> configureToCurrentThread()
    {
        if(classes == null) {
            classes = eventClassNames.stream().map((className) -> {
                try {
                    return Class.forName(className);
                } catch (Exception | Error e) {
                    return NullType.class;
                }
            }).distinct().collect(Collectors.toList());
        }
        return classes;
    }

    public List<Class> applyFilter(Predicate<? super Class> predicate)
    {
        return classes.stream().filter(predicate).collect(Collectors.toList());
    }

    private void getAllClasses() throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        eventClassNames = findClassesWithinDir(new File(new File("").getCanonicalPath()+"\\mods"));
        eventClassNames = eventClassNames.stream().distinct().collect(Collectors.toList());
    }
    private static List findClassesWithinDir(File dir)
    {
        List classes = new ArrayList();
        if(!dir.exists())
            return classes;
        if(dir.isDirectory()){
            for (File file : dir.listFiles())
            {
                classes.addAll(findClassesWithinDir(file));
            }
        }else{
            if(dir.getName().endsWith(".jar")){
                try {
                    new JarFile(dir).stream().forEach(jarEntry -> {
                        if (!jarEntry.getName().contains("$") && jarEntry.getName().contains(".class")) {
                            try {
                                classes.add(jarEntry.getName().replace("/", ".").replace(".class", ""));
                            } catch (Exception | Error e) {}
                        }
                    });
                } catch (Exception | Error e) {}
            }
        }
        return classes;
    }
}
