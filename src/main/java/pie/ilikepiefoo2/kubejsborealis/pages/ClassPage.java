package pie.ilikepiefoo2.kubejsborealis.pages;

import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.tag.Tag;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.pages.KubeJSHomePage.homeURL;

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
        Tag classTag = body.h3(this.subject.toGenericString());
        if(this.subject.getSuperclass() != null)
        {
            classTag.text(" extends ");
            linkType(classTag,this.subject.getSuperclass());
            if(this.subject.getInterfaces().length > 0) {
                classTag.text(" implements ");
                for (Class<?> interfaces : this.subject.getInterfaces()) {
                    linkType(classTag, interfaces);
                    classTag.text(", ");
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
    }

    public static Tag linkType(Tag previous, Class<?> aclass){
        if(!aclass.isPrimitive() && !aclass.isArray()){
            previous.a(" "+aclass.getSimpleName()+" ",homeURL+aclass.getName());
        }else{
            if(aclass.isArray()){
                linkType(previous,aclass.getComponentType()).text("[] ");
            }else {
                previous.text(" " + aclass.getSimpleName() + " ");
            }
        }
        return previous;
    }
    public static void linkParameters(Tag previous, Parameter[] parameters){
        for(int i=0; i<parameters.length; i++){
            Parameter parameter = parameters[i];
            previous = linkType(previous,parameter.getType());
            if(i != parameters.length-1)
                previous = previous.text(parameter.getName()+",");
        }
    }

    public static void addField(Tag table, Field field)
    {
        Tag row = table.tr();
        addDataModifiers(row,field.getModifiers());
        row.attr("data-return-type",cleanseLambdaName(field.getName()));
        Tag td = row.td();

        String toolTip = compileAnnotationToolTip(field.getAnnotations());
        Tag name = linkType(td.text(Modifier.toString(field.getModifiers())+" "),field.getType()).span(" " + field.getName());
        if(toolTip.length() > 0)
            name.tooltip(toolTip);

        linkType(row.td(),field.getType());
    }
    public static void addMethod(Tag table, Method method)
    {
        Tag row = table.tr();
        addDataModifiers(row,method.getModifiers());

        Tag methodTag = row.td();

        methodTag.text(Modifier.toString(method.getModifiers())+" ");

        if(!method.getReturnType().getTypeName().equals(" void ")){
            if(method.getReturnType().isArray()) {
                row.attr("data-return-type", method.getReturnType().getComponentType().getSimpleName());
            }else{
                row.attr("data-return-type", method.getReturnType().getSimpleName());
            }
            linkType(methodTag,method.getReturnType());
        }else{
            row.attr("data-return-type", "void");
            methodTag.text("void");
        }
        String methodName = cleanseLambdaName(method.getName());
        row.attr("data-name",methodName);
        row.attr("data-parameter-count",method.getParameterCount()+"");

        String toolTip = compileAnnotationToolTip(method.getAnnotations());
        if(toolTip.length() > 0) {
            methodTag.span(methodName).tooltip(toolTip);
        }else{
            methodTag.span(methodName);
        }
        methodTag = methodTag.text("(");
        linkParameters(methodTag,method.getParameters());

        methodTag.text(")");

        linkType(row.td(),method.getReturnType());
    }
    public static void addConstructor(Tag table, Constructor constructor)
    {
        Tag methodTag;
        Tag row = table.tr();
        addDataModifiers(row,constructor.getModifiers());
        String constructorName = cleanseLambdaName(constructor.getName());

        row.attr("data-return-type",constructorName);
        row.attr("data-parameter-count",constructor.getParameterCount()+"");

        methodTag = row.td().span(constructorName);

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
    public static void addDataModifiers(Tag row, int mods)
    {
        if(Modifier.isPublic(mods))
            row.attr("data-access","public");
        if(Modifier.isPrivate(mods))
            row.attr("data-access","private");
        if(Modifier.isProtected(mods))
            row.attr("data-access","protected");
        if(Modifier.isFinal(mods))
            row.attr("data-final","true");
        if(Modifier.isStatic(mods))
            row.attr("data-static","true");
        if(Modifier.isAbstract(mods))
            row.attr("data-abstract","true");
    }
}
