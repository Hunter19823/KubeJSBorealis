package pie.ilikepiefoo2.kubejsborealis.pages;

import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.borealis.tag.Tag;
import pie.ilikepiefoo2.kubejsborealis.ConfigHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.*;

/**
 * @author ILIKEPIEFOO2
 */
public class ClassPage extends HTTPWebPage {
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
        body.h1("").a("KubeJS Documentation", homeURL);
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
            constructors.sort((subjectA, subjectB) -> this.compareDeclaringClass(subjectA,subjectB));
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
            fields.sort((subjectA, subjectB) -> this.compareDeclaringClass(subjectA,subjectB));
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
            methods.sort((subjectA, subjectB) -> this.compareDeclaringClass(subjectA,subjectB));
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

    public int compareDeclaringClass(Member subjectA, Member subjectB)
    {
        if (subjectA.getDeclaringClass().equals(subjectB))
            return 0;
        if (subjectA.getDeclaringClass().equals(this.subject))
            return -1;
        if (subjectB.getDeclaringClass().equals(this.subject))
            return 1;
        return 0;
    }

    public static Tag linkType(Tag previous, Class<?> subject)
    {
        return linkType(previous,subject,getNameOfClass(subject));
    }
    public static Tag linkType(Tag previous, Class<?> subject, String name)
    {
        Tag result = previous;
        Class<?> referenceClass = subject;
        if(subject.isArray()) {
            referenceClass = subject.getComponentType();
        }
        if(!referenceClass.isPrimitive()){
            result = result.a(name,getLinkToClass(referenceClass));
        }else{
            result = result.span(name);
        }

        return result;
    }
    public static String getLinkToClass(Class<?> subject)
    {
        if(subject.isArray())
            return getLinkToClass(subject.getComponentType());
        if(!subject.isPrimitive())
            return homeURL+subject.getName();
        return "";
    }
    public static String getNameOfClass(Class<?> subject)
    {
        if(subject.isArray())
            return getNameOfClass(subject.getComponentType())+"[]";
        return subject.getSimpleName();
    }
    public static void linkParameters(Tag previous, Parameter[] parameters, Type[] genericTypes)
    {
        Tag combine;
        for(int i=0; i<parameters.length; i++){
            Parameter parameter = parameters[i];
            combine = previous.span(" ");
            linkType(combine,parameter.getType()).tooltip().style("display","inline").text(genericTypes[i].getTypeName());
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

        Tag td = row.td();

        String toolTip = compileAnnotationToolTip(field.getAnnotations());
        Tag name = td.text(Modifier.toString(field.getModifiers())+" ");
        linkType(name,field.getType());
        name.span(" " + field.getName());
        if(toolTip.length() > 0)
            name.tooltip().style("display","inline").text(toolTip);


        linkType(row.td(),field.getType(),field.getGenericType().getTypeName());

        if(includeInheritedFields)
            row.td().a(field.getDeclaringClass().getName(),getLinkToClass(field.getDeclaringClass()));

        //linkType(row.td(),field.getType());
    }
    public static void addMethod(Tag table, Method method)
    {
        Tag row = table.tr();
        addDataAttributes(row,method);
        Tag column = row.td();

        // Access Modifiers
        column.span(Modifier.toString(method.getModifiers())+" ");

        // Return Type
        if(!method.getReturnType().getTypeName().equals(" void ")){
            linkType(column,method.getReturnType()).tooltip().style("display","inline").text(method.getGenericReturnType().getTypeName());
            column.text(" ");
        }else{
            column = column.span("void");
        }

        // Method name
        String methodName = cleanseLambdaName(method.getName());
        String toolTip = compileAnnotationToolTip(method.getAnnotations());
        if(toolTip.length() > 0) {
            column.span(methodName).tooltip().style("display","inline").text(toolTip);
        }else{
            column.text(methodName);
        }
        // Parameters
        column.text("(");
        linkParameters(column,method.getParameters(),method.getGenericParameterTypes());
        column.text(")");

        // Return Type Column
        linkType(row.td(),method.getReturnType(),method.getGenericReturnType().getTypeName());

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
        row.attr("data-declaring-class",constructor.getDeclaringClass().getTypeName());
        row.attr("data-name",cleanseLambdaName(constructor.getName()));
        row.attr("data-parameters-count",constructor.getParameterCount()+"");
        addDataAttributes(row,constructor.getModifiers());
    }
    public static void addDataAttributes(Tag row, Method method){
        row.attr("data-declaring-class",method.getDeclaringClass().getTypeName());
        row.attr("data-name",cleanseLambdaName(method.getName()));
        row.attr("data-return-type-generic",method.getGenericReturnType().getTypeName());
        row.attr("data-parameters-count",method.getParameterCount()+"");
        addDataAttribute(row,"data-return-type", method.getReturnType());
        addDataAttributes(row,method.getModifiers());
    }
    public static void addDataAttributes(Tag row, Field field){
        row.attr("data-declaring-class",field.getDeclaringClass().getTypeName());
        row.attr("data-name",cleanseLambdaName(field.getName()));
        row.attr("data-return-type-generic",field.getGenericType().getTypeName());
        addDataAttribute(row,"data-return-type", field.getType());
        addDataAttributes(row,field.getModifiers());
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
        if(Modifier.isTransient(mods)) {
            row.attr("data-transient", "true");
        }else{
            row.attr("data-transient", "false");
        }
        if(Modifier.isSynchronized(mods)) {
            row.attr("data-synchronized", "true");
        }else{
            row.attr("data-synchronized", "false");
        }
        if(Modifier.isStrict(mods)) {
            row.attr("data-strict", "true");
        }else{
            row.attr("data-strict", "false");
        }
        if(Modifier.isVolatile(mods)) {
            row.attr("data-volatile", "true");
        }else{
            row.attr("data-volatile", "false");
        }
    }
}
