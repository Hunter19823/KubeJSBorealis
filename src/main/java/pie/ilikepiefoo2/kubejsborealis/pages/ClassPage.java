package pie.ilikepiefoo2.kubejsborealis.pages;

import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.borealis.tag.Tag;
import pie.ilikepiefoo2.kubejsborealis.ConfigHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.addClassTable;
import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.getTableSortScript;
import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.homeURL;

/**
 * @author ILIKEPIEFOO2
 */
public class ClassPage extends HTTPWebPage {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Class subject;
    private static boolean includeInheritedClasses = ConfigHandler.COMMON.includeInheritedClasses.get();
    private static boolean includeInheritedConstructors = ConfigHandler.COMMON.includeInheritedConstructors.get();
    private static boolean includeInheritedFields = ConfigHandler.COMMON.includeInheritedFields.get();
    private static boolean includeInheritedMethods = ConfigHandler.COMMON.includeInheritedMethods.get();

    public ClassPage(Class subject)
    {
        this.subject = subject;

        this.addBackButton();
    }

    @Override
    public PageType getPageType()
    {
        return ConfigHandler.COMMON.kubejsClassPage.get();
    }

    @Override
    public void body(Tag body)
    {
        Tag tempTag;
        Tag rowTag;
        Class<?>[] innerClasses;
        List<Constructor<?>> constructors;
        List<Field> fields;
        List<Method> methods;
        includeInheritedClasses = ConfigHandler.COMMON.includeInheritedClasses.get();
        includeInheritedConstructors = ConfigHandler.COMMON.includeInheritedConstructors.get();
        includeInheritedFields = ConfigHandler.COMMON.includeInheritedFields.get();
        includeInheritedMethods = ConfigHandler.COMMON.includeInheritedMethods.get();

        // Logo and link to documentation homepage.
        body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
        body.br();
        body.h1("").a("KubeJS Borealis", homeURL);
        body.br();

        // Class modifiers and package name
        tempTag = body.h3("");
        tempTag.span(Modifier.toString(this.subject.getModifiers())+" "+this.subject.getPackage().getName()+".");
        // Class name
        linkType(tempTag,this.subject);
        // Super class
        if(this.subject.getSuperclass() != null)
        {
            tempTag.text(" extends ");
            // Link to super class
            linkType(tempTag,this.subject.getSuperclass());
        }
        // Interfaces
        if(this.subject.getInterfaces().length > 0) {
            tempTag.text(" implements ");
            // Link each implemented interface
            for(int i=0; i<this.subject.getInterfaces().length; i++){
                linkType(tempTag, this.subject.getInterfaces()[i]);
                if(i != this.subject.getInterfaces().length -1)
                    tempTag.span(", ");
            }
        }
        // Link Enclosing Class
        if(this.subject.getEnclosingClass() != null) {
            linkType(body.h3("Enclosing Class: "),this.subject.getEnclosingClass());
        }
        // Declaring Class
        if(this.subject.getDeclaringClass() != null){
            linkType(body.h3("Declaring Class: "),this.subject.getDeclaringClass());
        }
        body.br();

        // Subclasses
        if(includeInheritedClasses){
            innerClasses = this.subject.getClasses();
        }else{
            innerClasses = this.subject.getDeclaredClasses();
        }
        if(innerClasses.length > 0){
            addClassTable(body.table(),"Subclass",Arrays.asList(innerClasses));
            body.br();
        }

        // Constructors
        if(includeInheritedConstructors){
            constructors = Arrays.asList(this.subject.getConstructors());
        }else{
            constructors = Arrays.asList(this.subject.getDeclaredConstructors());
        }
        if(constructors.size() > 0) {
            tempTag = body.table();
            rowTag = tempTag.tr();
            rowTag.th().a("Constructors", "#constructors");
            if(includeInheritedConstructors)
                rowTag.th().text("Declaring Class");
            constructors.sort(Comparator.comparing(Constructor::getName));
            constructors.sort(this::compareAccess);
            for (Constructor constructor : constructors) {
                addConstructor(tempTag, constructor);
            }

            body.br();
        }

        // Fields
        if(includeInheritedFields){
            fields = Arrays.asList(this.subject.getFields());
        }else{
            fields = Arrays.asList(this.subject.getDeclaredFields());
        }
        if(fields.size() > 0) {
            tempTag = body.table();
            rowTag = tempTag.tr();
            rowTag.th().a("Fields", "#fields");
            rowTag.th().text("Type");
            if(includeInheritedFields)
                rowTag.th().text("Declaring Class");
            fields.sort(Comparator.comparing(Field::getName));
            fields.sort(this::compareAccess);
            for (Field field : fields) {
                addField(tempTag, field);
            }
            body.br();
        }

        // Methods
        if(includeInheritedMethods){
            methods = Arrays.asList(this.subject.getMethods());
        }else{
            methods = Arrays.asList(this.subject.getDeclaredMethods());
        }
        if(methods.size() > 0) {
            tempTag = body.table();
            rowTag = tempTag.tr();
            rowTag.th().a("Methods", "#methods");
            rowTag.th().text("Return-Type");
            if(includeInheritedMethods)
                rowTag.th().text("Declaring Class");


            methods.sort(Comparator.comparing(Method::getName));
            methods.sort(this::compareAccess);
            //methods.sort(this::compareDeclaringClass);
            for (Method method : methods) {
                addMethod(tempTag, method);
            }

            body.br();
        }
        body.script(getTableSortScript());
    }
    /*
    public static Tag linkGenerics(Tag previous, Class<?> theClass)
    {
        if(theClass.getTypeParameters().length > 0) {
            LogManager.getLogger().info("Generic for: {}",theClass.getName());
            LogManager.getLogger().info("Generic Type Count: {}",theClass.getTypeParameters().length);
            previous = previous.text("<");
            for (int i=0; i< theClass.getTypeParameters().length; i++) {
                TypeVariable<? extends Class<?>> typeParameter = theClass.getTypeParameters()[i];
                LogManager.getLogger().info("Super Class: {}",typeParameter.getGenericDeclaration().getSuperclass());
                LogManager.getLogger().info("Generic Definition: {}",typeParameter.toString());
                LogManager.getLogger().info("Class Name: {}",typeParameter.getTypeName());
                LogManager.getLogger().info("Bounds: {}",typeParameter.getBounds()[0].getTypeName());
                LogManager.getLogger().info("Annotated Bounds: {}",typeParameter.getAnnotatedBounds()[0].getType().getTypeName());
                previous = previous.text(typeParameter.getName());

                if(typeParameter.getGenericDeclaration().getSuperclass() != null){
                    previous = linkType(previous.text(typeParameter.getName()+" extends "),typeParameter.getGenericDeclaration().getSuperclass());
                }else{
                    previous = previous.text(typeParameter.getName());
                }


                if(i != theClass.getTypeParameters().length-1){
                    previous = previous.text(",");
                }
            }
            previous = previous.text(">");
        }
        return previous;
    }
    */


    public int compareAccess(Member subjectA, Member subjectB) {
        boolean isPublicA = Modifier.isPublic(subjectA.getModifiers());
        boolean isPublicB = Modifier.isPublic(subjectB.getModifiers());
        if(isPublicA && isPublicB)
            return 0;
        if(isPublicA || isPublicB)
            return (isPublicA ? -1 : 1);
        return 0;
    }

    public static Tag linkType(Tag previous, Type subject)
    {
        return linkType(previous,subject,getNameOfClass(subject));
    }

    public static Tag linkType(Tag previous, Type subject, String name)
    {
        Tag result = previous;
        if(subject instanceof Class<?> referenceClass) {
            while (referenceClass.isArray()) {
                referenceClass = referenceClass.getComponentType();
            }
            if (!referenceClass.isPrimitive()) {
                result = result.a(name, getLinkToClass(referenceClass));
                result = linkGeneric(result, subject);
            }
            else {
                result = result.span(name);
            }
        }else if(subject instanceof ParameterizedType referenceClass) {
            if(referenceClass.getRawType() instanceof Class<?> rawClass) {
                result = result.a(name, getLinkToClass(rawClass));
                result = linkGeneric(result, subject);
            }else {
                result = result.span(name);
                result = linkGeneric(result, subject);
            }
        }else{
            result = result.span(name);
        }

        return result;
    }
    private static Tag linkGeneric(Tag previous, Type subject) {
        if(subject == null)
            return previous;
        if(subject instanceof ParameterizedType parameterizedType){
            int count = 0;
            for(var type : parameterizedType.getActualTypeArguments()){
                if(count == 0)
                    previous.parent.text("<");
                else
                    previous.parent.text(",");
                count++;
                previous = linkType(previous.parent, type);
            }
            if(count > 0)
                previous.parent.text(">");
        }
        return previous;
    }

    public static String getLinkToClass(Class<?> subject)
    {
        if(subject.isArray())
            return getLinkToClass(subject.getComponentType());
        if(!subject.isPrimitive())
            return homeURL+subject.getName();
        return "";
    }

    public static String getNameOfClass(Type subject)
    {
        if(subject instanceof Class<?> referenceClass) {
            if(referenceClass.isArray())
                return getNameOfClass(referenceClass.getComponentType())+"[]";
            var remap = RemappingHelper.getMinecraftRemapper().getMappedClass(referenceClass);
            if(remap != null && !remap.isEmpty())
                return remap.substring(remap.lastIndexOf('.')+1);
            return referenceClass.getSimpleName();
        }else if(subject instanceof ParameterizedType referenceClass) {
            return getNameOfClass(referenceClass.getRawType());
        }else {
            return subject.getTypeName();
        }
    }

    public static void linkParameters(Tag previous, Parameter[] parameters, Type[] genericTypes)
    {
        Tag combine;
        for(int i=0; i<genericTypes.length; i++){
            Parameter parameter = parameters[i];
            var type = genericTypes[i];
            combine = previous.span(" ");
            if(genericTypes.length == parameters.length) {
                linkType(combine, type).tooltip().style("display", "inline").text(type.getTypeName());
            }else{
                linkType(combine, parameter.getType()).tooltip().style("display", "inline");
            }
            combine.text(" ");
            combine.text(parameter.getName());
            if(i != parameters.length-1)
                previous.text(",");
        }
    }

    public static void addConstructor(Tag table, Constructor constructor)
    {
        Tag methodTag;
        Tag row = table.tr();
        addDataAttributes(row,constructor);
        String constructorName = cleanseLambdaName(constructor.getDeclaringClass().getSimpleName());

        row = row.td();
        methodTag = row.span(Modifier.toString(constructor.getModifiers())+" "+constructorName);

        String tooltip = compileAnnotationToolTip(constructor.getAnnotations());
        if(tooltip.length() > 0)
            methodTag.tooltip().style("display","inline").text(tooltip);

        row.text("(");
        linkParameters(row,constructor.getParameters(),constructor.getGenericParameterTypes());
        row.text(")");

        if(includeInheritedConstructors)
            row.td().a(constructor.getDeclaringClass().getName(),getLinkToClass(constructor.getDeclaringClass()));
    }

    public static void addField(Tag table, Field field)
    {
        Tag row = table.tr();
        addDataAttributes(row,field);
        String properName = getFieldName(field.getDeclaringClass(),field);

        Tag td = row.td();

        String toolTip = compileAnnotationToolTip(field.getAnnotations());
        if(!properName.equals(field.getName()))
            toolTip += "Original Name: "+field.getName()+"\n";
        Tag name = td.text(Modifier.toString(field.getModifiers())+" ");
        try {
            linkType(name, field.getGenericType());
        } catch (Exception e) {
            linkType(name,field.getType());
        }
        name.span(" " + properName);
        if(toolTip.length() > 0)
            name.tooltip().style("display","inline").text(toolTip);

        try {
            linkType(row.td(), field.getGenericType());
        } catch (Exception e) {
            linkType(row.td(),field.getType(),field.getGenericType().getTypeName());
        }

        if(includeInheritedFields)
            row.td().a(field.getDeclaringClass().getName(),getLinkToClass(field.getDeclaringClass()));

    }

    public static void addMethod(Tag table, Method method)
    {
        Tag row = table.tr();
        addDataAttributes(row,method);
        String properName = getMethodName(method.getDeclaringClass(),method);

        Tag column = row.td();

        // Access Modifiers
        column.span(Modifier.toString(method.getModifiers())+" ");

        // Return Type
        if(!method.getReturnType().getTypeName().equals(" void ")){
            try {
                linkType(column, method.getGenericReturnType()).tooltip().style("display", "inline").text(method.getGenericReturnType().getTypeName());
            } catch (Exception e) {
                linkType(column, method.getReturnType()).tooltip().style("display", "inline").text(method.getGenericReturnType().getTypeName());
            }
            column.text(" ");
        }else{
            column = column.span("void");
        }

        // Method name
        String methodName = cleanseLambdaName(properName);

        String toolTip = compileAnnotationToolTip(method.getAnnotations());
        if(!method.getName().equals(properName))
            toolTip += "Original Name: "+method.getName()+"\n";

        if(toolTip.length() > 0) {
            column.span(methodName).tooltip().style("display","inline").text(toolTip);
        }else{
            column.text(methodName);
        }
        // Parameters
        column.text("(");
        try {
            linkParameters(column, method.getParameters(), method.getGenericParameterTypes());
        } catch(TypeNotPresentException e){
            LOGGER.error(e);
            linkParameters(column, method.getParameters(), method.getTypeParameters());
        }
        column.text(")");

        // Return Type Column
        try{
            linkType(row.td(), method.getGenericReturnType());
        }catch(Exception e){
            linkType(row.td(),method.getReturnType(),method.getGenericReturnType().getTypeName());
        }

        // Inherited Methods
        if(includeInheritedMethods)
            row.td().a(method.getDeclaringClass().getName(),getLinkToClass(method.getDeclaringClass()));

    }
    public static String compileAnnotationToolTip(Annotation[] annotations)
    {
        StringBuilder builder = new StringBuilder();
        for(Annotation annotation : annotations) {
            builder.append(annotation.toString());
            builder.append("\n");
        }
        return (builder.toString());
    }
    public static String cleanseLambdaName(String name)
    {
        if(name.contains("lambda$")){
            return name.substring(7,name.indexOf('$',7));
        }else {
            return name;
        }
    }
    public static void addDataAttributes(Tag row, Constructor constructor){
        addDataAttributes(row,constructor.getModifiers());
        //row.attr("data-declaring-class",constructor.getDeclaringClass().getTypeName());
        //row.attr("data-name",cleanseLambdaName(constructor.getName()));
        row.attr("data-parameters-count",constructor.getParameterCount()+"");
    }
    public static void addDataAttributes(Tag row, Method method){
        addDataAttributes(row,method.getModifiers());
        row.attr("data-declaring-class",getNameOfClass(method.getDeclaringClass()));
        //row.attr("data-name",cleanseLambdaName(method.getName()));
        row.attr("data-return-type-generic",method.getGenericReturnType().getTypeName());
        row.attr("data-parameters-count",method.getParameterCount()+"");
        addDataAttribute(row,"data-return-type", method.getReturnType());
    }
    public static void addDataAttributes(Tag row, Field field){
        addDataAttributes(row,field.getModifiers());
        row.attr("data-declaring-class",getNameOfClass(field.getDeclaringClass()));
        //row.attr("data-name",cleanseLambdaName(getFieldName(field.getDeclaringClass(),field)));
        row.attr("data-return-type-generic",field.getGenericType().getTypeName());
        addDataAttribute(row,"data-return-type", field.getType());
    }
    public static void addDataAttribute(Tag row, String key, Class<?> type)
    {
        if(!type.getTypeName().equals(Void.TYPE.getTypeName())){
            if (type.isArray()) {
                row.attr(key, type.getComponentType().getSimpleName()+"[]");
            } else {
                row.attr(key, type.getSimpleName());
            }
        }else{
            row.attr(key, "void");
        }
    }
    public static void addDataAttributes(Tag row, int mods)
    {
        if(Modifier.isPublic(mods))
            row.attr("data-access","public");
        if(Modifier.isPrivate(mods))
            row.attr("data-access","private");
        if(Modifier.isProtected(mods))
            row.attr("data-access","protected");
        if(Modifier.isAbstract(mods))
            row.attr("data-access", "abstract");
        if(Modifier.isFinal(mods)){
            row.attr("data-final","true");
        }else{
            row.attr("data-final", "false");
        }
        if(Modifier.isStatic(mods)){
            row.attr("data-static","true");
        }else{
            row.attr("data-static", "false");
        }
//        if(Modifier.isTransient(mods)) {
//            row.attr("data-transient", "true");
//        }else{
//            row.attr("data-transient", "false");
//        }
//        if(Modifier.isSynchronized(mods)) {
//            row.attr("data-synchronized", "true");
//        }else{
//            row.attr("data-synchronized", "false");
//        }
//        if(Modifier.isStrict(mods)) {
//            row.attr("data-strict", "true");
//        }else{
//            row.attr("data-strict", "false");
//        }
//        if(Modifier.isVolatile(mods)) {
//            row.attr("data-volatile", "true");
//        }else{
//            row.attr("data-volatile", "false");
//        }
    }

    public static String getFieldName(Class<?> type, Field field) {
        String out = RemappingHelper.getMinecraftRemapper().getMappedField(type, field);
        if(out == null || out.isEmpty())
            out = field.getName();
        return out;
    }
    public static String getMethodName(Class<?> type, Method method) {
        String out = RemappingHelper.getMinecraftRemapper().getMappedMethod(type, method);
        if(out == null || out.isEmpty())
            out = method.getName();
        return out;
    }
}
