package pie.ilikepiefoo2.kubejsborealis.util;

import com.google.common.collect.Lists;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSCommon;
import dev.latvian.kubejs.docs.KubeJSDocs;
import dev.latvian.kubejs.event.EventJS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.Borealis;
import pie.ilikepiefoo2.borealis.page.HomePageEntry;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ReflectionHandler {
    private List<Class> eventClasses;
    private static final Logger LOGGER = LogManager.getLogger(ReflectionHandler.class);
    /*
    public static void main(String[] args)
    {
        System.out.println(KubeJS.instance);
        ReflectionHandler handler = new ReflectionHandler();
        List<Class> events = handler.applyFilter(event -> EventJS.class.isAssignableFrom(event));
        System.out.println(events.size());
        System.out.println(events);
        try {
            handler.getAllClasses();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */
    public ReflectionHandler()
    {
        eventClasses = new ArrayList<>();
        try {
            getAllClasses();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Total Classes Found >> {}",eventClasses.size());
    }

    public List<Class> applyFilter(Predicate<? super Class> predicate)
    {
        return eventClasses.stream().filter(predicate).collect(Collectors.toList());
    }

    private void collectAllClasses()
    {
        eventClasses.clear();
        LOGGER.info("Now Collecting all Classes");
        try {
            getAllClasses();
        } catch (Exception | Error e) {}
        /*
        for(Package pack : Package.getPackages()){
            try {
                LOGGER.info("Attempting to collect classes for >> {}",pack.getName());
                Class[] classes = getClasses(pack.getName());
                LOGGER.info("Total classes collected >> {}",classes.length);
                eventClasses.addAll(Lists.asList(EventJS.class,classes).stream().collect(Collectors.toList()));
            } catch (Exception | Error e) {}
        }

         */
        LOGGER.info("Finished collecting all classes, now removing duplicates...");
        eventClasses = eventClasses.stream().distinct().collect(Collectors.toList());
    }

    private void getAllClasses() throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        eventClasses = findClassesWithinDir(new File(new File("").getCanonicalPath()+"\\mods"));
        LOGGER.info(new File("").getAbsolutePath());
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
                                classes.add(Class.forName((jarEntry.getName().replace("/", ".").replace(".class", ""))));
                            } catch (Exception | Error e) {}
                        }
                    });
                } catch (Exception | Error e) {}
            }
        }
        return classes;

    }
    /*
    private static Class[] getClasses(String packageName) throws IOException
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        ArrayList<Class> classes = new ArrayList();
        List<File> dirs = new ArrayList();
        List<String> known = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = getFileByName(resource.getFile());
            if(!known.contains(file.getPath())) {
                if(!file.isDirectory()) {
                    LOGGER.info("Found Potential Jar file >> {}", file.getPath());
                    known.add(file.getPath());
                    if (file.exists()) {
                        try {
                            new JarFile(file).stream().forEach(jarEntry -> {
                                if (!jarEntry.getName().contains("$") && jarEntry.getName().contains(".class")) {
                                    try {
                                        classes.add(Class.forName((jarEntry.getName().replace("/", ".").replace(".class", ""))));
                                    } catch (Exception | Error e) {
                                        // A catch all
                                        //e.printStackTrace();
                                    }
                                }
                            });
                        } catch (Exception | Error e) {}
                    }else{
                        LOGGER.info("Jar File didn't exist.");
                    }
                }else{
                    dirs.add(file);
                }
            }
        }
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }
    private static File getFileByName(String fileName)
    {
        File temp = new File(fileName);
        if(temp.exists())
            return temp;
        try {
            temp = new File(URLDecoder.decode(fileName, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {}
        if(temp.exists())
            return temp;
        try {
            temp = new File(URLDecoder.decode(fileName, StandardCharsets.UTF_16.toString()));
        } catch (UnsupportedEncodingException e) {}
        if(temp.exists())
            return temp;
        try {
            temp = new File(URLDecoder.decode(fileName, StandardCharsets.US_ASCII.toString()));
        } catch (UnsupportedEncodingException e) {}
        if(temp.exists())
            return temp;
        if(fileName.startsWith("file:"))
            temp = getFileByName(fileName.substring(6));
        if(temp.exists())
            return temp;
        if(fileName.contains(".jar")) {
            temp = getFileByName(fileName.substring(0, fileName.indexOf(".jar") + 4));
        }

        return temp;
    }

     */

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List findClasses(File directory, String packageName) {
        List classes = new ArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {}
            }
        }
        return classes;
    }
}
