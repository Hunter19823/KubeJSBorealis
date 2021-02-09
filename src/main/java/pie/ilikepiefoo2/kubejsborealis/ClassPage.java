package pie.ilikepiefoo2.kubejsborealis;

import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.tag.Tag;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.KubeJSHomePage.homeURL;

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
            if(this.subject.getSuperclass().isInterface()) {
                classTag.text(" extends ");
            }else{
                classTag.text(" implements ");
            }
            linkType(classTag,this.subject.getSuperclass());
        }
        if(this.subject.getEnclosingClass() != null) {
            linkType(body.h3("Enclosing Class: "),this.subject.getEnclosingClass());
        }
        if(this.subject.getDeclaringClass() != null){
            linkType(body.h3("Declaring Class: "),this.subject.getDeclaringClass());
        }
        body.br();

        Tag constructorTable = body.table();
        Tag firstRow = constructorTable.tr();
        firstRow.th().a("Constructors","#constructors");

        List<Constructor> constructors = Arrays.asList(this.subject.getDeclaredConstructors());
        Collections.sort(constructors, Comparator.comparing(Constructor::getName));
        for(Constructor constructor : constructors){
            addConstructor(constructorTable,constructor);
        }

        body.br();
        Tag fieldTable = body.table();
        firstRow = fieldTable.tr();
        firstRow.th().a("Fields","#fields");
        firstRow.th().text("Type");


        List<Field> fields = Arrays.asList(this.subject.getDeclaredFields());
        Collections.sort(fields, Comparator.comparing(Field::getName));
        for(Field field : fields){
            addField(fieldTable, field);
        }

        body.br();
        Tag methodTable = body.table();
        firstRow = methodTable.tr();
        firstRow.th().a("Methods","#methods");
        firstRow.th().text("Returns");


        List<Method> methods = Arrays.asList(this.subject.getDeclaredMethods());
        Collections.sort(methods, Comparator.comparing(Method::getName));
        for(Method method : methods){
            addMethod(methodTable,method);
        }

        body.br();
    }

    public static Tag linkType(Tag previous,Class<?> aclass){
        if(!aclass.isPrimitive() && !aclass.isArray()){
            previous.a(" "+aclass.getSimpleName()+" ",homeURL+aclass.getName());
        }else{
            previous.text(" "+aclass.getSimpleName()+" ");
        }
        return previous;
    }

    public static Tag linkClass(Tag previous,Class<?> aclass){
        if(!aclass.isPrimitive() && !aclass.isArray()){
            previous.a(" "+aclass.getName()+" ",homeURL+aclass.getName());
        }else{
            previous.text(" "+aclass.getSimpleName()+" ");
        }
        return previous;
    }

    public static void addField(Tag table, Field field)
    {
        Tag row = table.tr();
        linkType(row.td().text(Modifier.toString(field.getModifiers())+" "),field.getType()).text(" "+field.getName());

        linkType(row.td(),field.getType());
    }
    public static void addMethod(Tag table, Method method)
    {
        Tag row = table.tr();
        Tag methodTag = row.td().text(Modifier.toString(method.getModifiers())+" ");
        if(method.getName().contains("lambda$")){
            methodTag = methodTag.text(method.getName().substring(7,method.getName().indexOf('$',7)));
        }else {
            methodTag = methodTag.text(method.getName());
        }
        methodTag = methodTag.text("(");
        for(Parameter parameter : method.getParameters()){
            methodTag = linkType(methodTag,parameter.getType());
            methodTag = methodTag.text(parameter.getName()+",");
        }
        methodTag.text(")");
        if(!method.getReturnType().getTypeName().equals("void")){
            linkType(row.td(),method.getReturnType());
        }else{
            row.td().text("void");
        }
    }
    public static void addConstructor(Tag table, Constructor constructor)
    {
        Tag methodTag;
        if(constructor.getName().contains("lambda$")){
            methodTag = table.tr().td().text(constructor.getName().substring(7,constructor.getName().indexOf('$',7))+"(");
        }else {
            methodTag = table.tr().td().text(constructor.getName() + "(");
        }
        for(Parameter parameter : constructor.getParameters()){
            methodTag = linkType(methodTag,parameter.getType());
            methodTag = methodTag.text(parameter.getName()+",");
        }
        methodTag.text(")");
    }
}
