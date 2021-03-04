package pie.ilikepiefoo2.kubejsborealis.pages;

import com.google.common.reflect.TypeParameter;
import org.apache.logging.log4j.LogManager;
import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.tag.Tag;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.*;

public class ClassPage extends HTTPWebPage {
    private final Class subject;

    public ClassPage(Class subject)
    {
        this.subject = subject;
        this.addBackButton();
    }

    @Override
    public void body(Tag body)
    {
        body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
        body.br();
        body.h1("").a("KubeJS Documentation", homeURL);
        body.br();
        Tag classTag = body.h3("");
        classTag.span(Modifier.toString(this.subject.getModifiers())+" "+this.subject.getPackage().getName()+".");
        linkType(classTag,this.subject);
        if(this.subject.getSuperclass() != null)
        {
            classTag.text(" extends ");
            linkType(classTag,this.subject.getSuperclass());
            if(this.subject.getInterfaces().length > 0) {
                classTag.text(" implements ");
                for(int i=0; i<this.subject.getInterfaces().length; i++){
                    linkType(classTag, this.subject.getInterfaces()[i]);
                    if(i != this.subject.getInterfaces().length -1)
                        classTag.span(", ");
                }
            }
        }
        if(this.subject.getEnclosingClass() != null) {
            linkType(body.h3("Enclosing Class: "),this.subject.getEnclosingClass());
        }
        if(this.subject.getDeclaringClass() != null){
            linkType(body.h3("Declaring Class: "),this.subject.getDeclaringClass());
        }
        Tag firstRow;
        body.br();
        if(this.subject.getDeclaredConstructors().length > 0) {
            Tag constructorTable = body.table();
            firstRow = constructorTable.tr();
            firstRow.th().a("Constructors", "#constructors");

            List<Constructor> constructors = Arrays.asList(this.subject.getDeclaredConstructors());
            Collections.sort(constructors, Comparator.comparing(Constructor::getName));
            for (Constructor constructor : constructors) {
                addConstructor(constructorTable, constructor);
            }

            body.br();
        }
        if(this.subject.getDeclaredFields().length > 0) {
            Tag fieldTable = body.table();
            firstRow = fieldTable.tr();
            firstRow.th().a("Fields", "#fields");
            firstRow.th().text("Type");


            List<Field> fields = Arrays.asList(this.subject.getDeclaredFields());
            Collections.sort(fields, Comparator.comparing(Field::getName));
            for (Field field : fields) {
                addField(fieldTable, field);
            }
            body.br();
        }
        if(this.subject.getDeclaredMethods().length > 0) {
            Tag methodTable = body.table();
            firstRow = methodTable.tr();
            firstRow.th().a("Methods", "#methods");
            firstRow.th().text("Return-Type");

            List<Method> methods = Arrays.asList(this.subject.getDeclaredMethods());
            Collections.sort(methods, Comparator.comparing(Method::getName));
            for (Method method : methods) {
                addMethod(methodTable, method);
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

    public static Tag linkType(Tag previous, Class<?> aclass){
        if(!aclass.isPrimitive() && !aclass.isArray()){
            return previous.a(aclass.getSimpleName(),homeURL+aclass.getName());
        }else{
            if(aclass.isArray()){
                linkType(previous,aclass.getComponentType());
                return previous.text("[]");
            }else {
                return previous.text(aclass.getSimpleName());
            }
        }
    }
    public static void linkParameters(Tag previous, Parameter[] parameters){
        for(int i=0; i<parameters.length; i++){
            Parameter parameter = parameters[i];
            previous.text(" ");
            linkType(previous,parameter.getType());
            previous.text(" ");
            previous.text(parameter.getName());
            if(i != parameters.length-1)
                previous.text(",");
        }
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
            name.tooltip(toolTip);

        row.td().span(field.getGenericType().getTypeName());

        //linkType(row.td(),field.getType());
    }
    public static void addMethod(Tag table, Method method)
    {
        Tag row = table.tr();
        addDataAttributes(row,method);

        Tag methodTag = row.td();
        methodTag.text(Modifier.toString(method.getModifiers())+" ");

        if(!method.getReturnType().getTypeName().equals(" void ")){
            linkType(methodTag,method.getReturnType()).text(" ");
        }else{
            methodTag.text("void");
        }
        String methodName = cleanseLambdaName(method.getName());


        String toolTip = compileAnnotationToolTip(method.getAnnotations());
        if(toolTip.length() > 0) {
            methodTag.span(methodName).tooltip(toolTip);
        }else{
            methodTag.span(methodName);
        }
        methodTag = methodTag.text("(");
        linkParameters(methodTag,method.getParameters());

        methodTag.text(")");

        row.td().span(method.getGenericReturnType().getTypeName());
        //linkType(row.td(),method.getReturnType());
    }
    public static void addConstructor(Tag table, Constructor constructor)
    {
        Tag methodTag;
        Tag row = table.tr();
        addDataAttributes(row,constructor);
        String constructorName = cleanseLambdaName(constructor.getDeclaringClass().getSimpleName());


        methodTag = row.td().span(Modifier.toString(constructor.getModifiers())+" "+constructorName);

        String tooltip = compileAnnotationToolTip(constructor.getAnnotations());
        if(tooltip.length() > 0)
            methodTag.tooltip(tooltip);

        methodTag.text("(");
        linkParameters(methodTag,constructor.getParameters());
        methodTag.text(")");
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
        row.attr("data-name",cleanseLambdaName(constructor.getName()));
        row.attr("data-parameters-count",constructor.getParameterCount()+"");
        addDataAttributes(row,constructor.getModifiers());
    }
    public static void addDataAttributes(Tag row, Method method){
        row.attr("data-name",cleanseLambdaName(method.getName()));
        row.attr("data-return-type-generic",method.getGenericReturnType().getTypeName());
        row.attr("data-parameters-count",method.getParameterCount()+"");
        addDataAttribute(row,"data-return-type", method.getReturnType());
        addDataAttributes(row,method.getModifiers());
    }
    public static void addDataAttributes(Tag row, Field field){
        row.attr("data-name",cleanseLambdaName(field.getName()));
        row.attr("data-return-type-generic",field.getGenericType().getTypeName());
        addDataAttribute(row,"data-return-type", field.getType());
        addDataAttributes(row,field.getModifiers());
    }
    public static void addDataAttribute(Tag row, String key, Class type)
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
