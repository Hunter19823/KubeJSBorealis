package pie.ilikepiefoo2.kubejsborealis;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.kubejsborealis.util.ReflectionHandler;

import java.util.Collections;
import java.util.List;

/**
 * @author ILIKEPIEFOO2
 */
public class ConfigHandler {
    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.EnumValue<PageType> kubejsDocumentation;
        public final ForgeConfigSpec.EnumValue<PageType> kubejsClassPage;
        public final ForgeConfigSpec.BooleanValue includeInheritedConstructors;
        public final ForgeConfigSpec.BooleanValue includeInheritedFields;
        public final ForgeConfigSpec.BooleanValue includeInheritedMethods;
        public final ForgeConfigSpec.BooleanValue includeInheritedClasses;
        public final ForgeConfigSpec.BooleanValue customPages;
        public final ForgeConfigSpec.BooleanValue reflectionHandler;
        public final ForgeConfigSpec.BooleanValue printAllClasses;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistedPackages;

        public Common(ForgeConfigSpec.Builder builder)
        {
            builder.push("KubeJSDocumentation");
                builder.comment("Enable/Disable KubeJS Documentation Homepage",
                        "REQUIRED_AUTH is deprecated and will be removed in a future version");
                kubejsDocumentation = builder
                        .defineEnum("documentationHomePage",PageType.ENABLED);
                builder.comment("Enable/Disable All KubeJS Class Pages",
                        "REQUIRED_AUTH is deprecated and will be removed in a future version");
                kubejsClassPage = builder
                        .defineEnum("classPage",PageType.ENABLED);

                builder.push("classPageSpecifics");
                includeInheritedConstructors = builder
                        .comment("Should every ClassPage include inherited Constructors as well defined ones?")
                        .define("includeInheritedConstructors",false);
                includeInheritedFields = builder
                        .comment("Should every ClassPage include inherited Fields as well defined ones?")
                        .define("includeInheritedFields",false);
                includeInheritedMethods = builder
                        .comment("Should every ClassPage include inherited Methods as well defined ones?")
                        .define("includeInheritedMethods",false);
                includeInheritedClasses = builder
                        .comment("Should every ClassPage include inherited Classes as well defined ones?")
                        .define("includeInheritedClasses",false);
                builder.pop();
            builder.pop();
            builder.push("customPages");
                customPages = builder
                        .comment("Enable customPages event for KJS?")
                        .define("customPages",false);
            builder.pop();
            builder.push("experimental");
                builder.push("Reflection Handler");
                    builder.comment("Enable Reflection Handler?",
                            "The reflection handler tries to find all classes that extend EventJS and add them to the KubeJS homepage.",
                            "(Experimental Feature that can crash JVM in some instances and not produce a crash report)");
                    reflectionHandler = builder
                            .define("reflectionHandler",false);
                    blacklistedPackages = builder
                    .comment("List of blacklisted packages to load classes from for the reflection handler.",
                            "If you have AE2 installed and experiencing crashing, try adding \"appeng\".")
                    .defineList("blacklistedPackages",
                            Collections.emptyList(),
                            s -> s instanceof String
                    );
                builder.pop();
                builder.push("Print All Classes for Debugging");
                    builder.comment("Forces the reflection handler to print out the names of all classes as it's loading them.",
                            "This will spam the log file so only enable this when you are experiencing crashing issues.",
                            "The latest log should contain the last class that was attempted to be loaded before the crash.",
                            "The package should be added to the blacklisted packages config.");
                    printAllClasses = builder
                            .define("printAllClasses",false);
                builder.pop();
            builder.pop();
        }
    }

    public static void onConfigLoad()
    {
        if(COMMON.reflectionHandler.get())
            ReflectionHandler.getInstance();
    }


}
