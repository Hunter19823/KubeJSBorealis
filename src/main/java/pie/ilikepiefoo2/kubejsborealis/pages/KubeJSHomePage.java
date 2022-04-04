package pie.ilikepiefoo2.kubejsborealis.pages;

import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.NativeJavaArray;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.util.DynamicFunction;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo2.borealis.Borealis;
import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.borealis.tag.Tag;
import pie.ilikepiefoo2.kubejsborealis.ConfigHandler;
import pie.ilikepiefoo2.kubejsborealis.KubeJSBorealis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;

import static pie.ilikepiefoo2.kubejsborealis.pages.ClassPage.compileAnnotationToolTip;
import static pie.ilikepiefoo2.kubejsborealis.pages.ClassPage.linkType;

/**
 * @author ILIKEPIEFOO2
 */
public class KubeJSHomePage extends HTTPWebPage {
    private static final Logger LOGGER = Borealis.LOGGER;
    public static final String homeURI = "kubejs_auto_docs";
    public static final String homeURL = "/"+homeURI+"/";

    @Override
    public PageType getPageType()
    {
        return ConfigHandler.COMMON.kubejsDocumentation.get();
    }

    @Override
    public void body(Tag body)
    {
        LOGGER.debug("Creating Documentation Home Page now...");
        body.img("https://kubejs.latvian.dev/logo_title.png").style("height", "7em");
        body.br();
        body.h1("").a("KubeJS Documentation", homeURL);

        if(KubeJSBorealis.getAllJSEvents().size() > 0) {
            body.br();
            addClassTable(body.table(),"Scanned EventJS Class",KubeJSBorealis.getAllJSEvents());
        }
        if(global.size() > 0) {
            body.br();
            addTable(body, "Global Objects", global);
        }
        if(startup.size() > 0) {
            body.br();
            addTable(body, "Startup Objects", startup);
        }
        if(client.size() > 0) {
            body.br();
            addTable(body, "Client Objects", client);
        }
        if(server.size() > 0) {
            body.br();
            addTable(body, "Server Objects", server);
        }

        body.script(getTableSortScript());



    }
    private static KubeJSHomePage instance;
    public static KubeJSHomePage getInstance()
    {
        if(instance == null)
        {
            loadBindings();
            instance = new KubeJSHomePage();
            instance.neverDirty();
        }
        return instance;
    }

    private static Map<String,Class> startup = new HashMap<>();
    private static Map<String,Class> client = new HashMap<>();
    private static Map<String,Class> server = new HashMap<>();
    private static Map<String,Class> global = new HashMap<>();
    public static void loadBindings()
    {
        client = getAllProperties(ScriptType.CLIENT.manager.get());
        startup = getAllProperties(ScriptType.STARTUP.manager.get());
        server = getAllProperties(ScriptType.SERVER.manager.get());
        merge(client,startup,server);
    }
    public static void merge(Map<String,Class> first, Map<String,Class> second, Map<String, Class> third)
    {
        global = new TreeMap<>();
        ArrayList<String> removeKeys = new ArrayList<>();
        for(String key : first.keySet()){
            if(second.containsKey(key) && third.containsKey(key)){
                global.put(key,first.get(key));
                removeKeys.add(key);
            }
        }
        for(String key : removeKeys)
        {
            first.remove(key);
            second.remove(key);
            third.remove(key);
        }
    }

    public static void addClassTable(Tag table, String title, List<Class> classList)
    {
        Tag header = table.tr();
        header.th().a(title,"#"+title.replaceAll(" ","_"));
        header.th().text("Class Name");
        classList.forEach(
            subject ->
            {
                Tag previous = table.tr();
                if(subject.getDeclaringClass() != null) {
                    previous.attr("data-declaring-class", subject.getDeclaringClass().getTypeName());
                } else{
                    previous.attr("data-declaring-class", "Unknown");
                }
                previous.attr("data-name",ClassPage.cleanseLambdaName(subject.getSimpleName()));
                previous.attr("data-package-name",subject.getPackage().getName());
                String toolTip = compileAnnotationToolTip(subject.getAnnotations());
                if(toolTip.length() > 0) {
                    linkType(previous.td(), subject).tooltip(toolTip.replace("\n","<br>").replace("\r","<br>"));
                }else{
                    linkType(previous.td(), subject);
                }
                previous.td().span(subject.getName());
            }
    );
    }

    public static void addTable(Tag previous,String title,Map<String, Class> classMap)
    {
        previous.br();
        previous.h1("").a(title,"#"+title);
        previous.br();
        Tag table = previous.table();
        Tag header = table.tr();
        header.th().span("KJS Name");
        header.th().span("Type");
        header.th().span("Location");
        for(String key : classMap.keySet()) {
            Class currentClass = classMap.get(key);
            Tag row = table.tr();
            String classType;
            if(currentClass.isEnum()) {
                classType = "enum";
            }else if(currentClass.isPrimitive()) {
                classType = "primitive";
            }else if(currentClass.isAssignableFrom(DynamicFunction.class)) {
                classType = "function";
            }else {
                classType = "object";
            }
            row.attr("data-class-type",classType);
            row.attr("data-name",key);
            row.attr("data-return-type",currentClass.getName());
            row.td().span(key);
            row.td().span(classType);
            row.td().a(currentClass.getName(),homeURL+currentClass.getName());
        }
        previous.br();
    }

    public static Map<String, Class> getAllProperties( ScriptManager manager)
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
                    LOGGER.debug("Now loading propertyId of " + propertyId);
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
                return (Class) unwrapped;
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

    public static String getTableSortScript()
    {
        if(tableSortScript != null)
            return tableSortScript;
        StringBuilder builder = new StringBuilder();
        try {
            Files.lines(new File("src/main/resources/script.js").toPath(), StandardCharsets.UTF_8)
                .forEach(s -> builder.append(s).append("\n"));
        } catch (IOException e) {
            LOGGER.error("Could not find Sorting script!");
        }
        tableSortScript = builder.toString();
        return tableSortScript;
    }
    private static String tableSortScript = "function tableToJson(table) {\n" +
            "    var data = {rows:[]};\n" +
            "    var sortableData = {};\n" +
            "    for (var i=1; i<table.rows.length; i++) {\n" +
            "        var tableRow = table.rows[i];\n" +
            "        Object.keys(tableRow.dataset).forEach( key =>{\n" +
            "            if(sortableData[key] === undefined){\n" +
            "                sortableData[key] = {key:key,possible:[]}\n" +
            "            }\n" +
            "            if(!sortableData[key].possible.includes(tableRow.dataset[key])){\n" +
            "                sortableData[key].possible.push(tableRow.dataset[key]);\n" +
            "            }\n" +
            "        });\n" +
            "        data['rows'].push(tableRow);\n" +
            "    }\n" +
            "    Object.keys(sortableData).forEach( key => {\n" +
            "        sortableData[key].possible.sort(function(value1,value2){\n" +
            "            let obfuscatedName1 = value1.includes('func') ? true : value1.includes('field');\n" +
            "            let obfuscatedName2 = value2.includes('func') ? true : value2.includes('field');\n" +
            "            switch (true) {\n" +
            "                case value1 > value2:\n" +
            "                    if(!obfuscatedName1) {\n" +
            "                        return -1;\n" +
            "                    }else{\n" +
            "                        if(obfuscatedName2){\n" +
            "                            return -1;\n" +
            "                        }else{\n" +
            "                            return 1;\n" +
            "                        }\n" +
            "                    }\n" +
            "                case value1 < value2:\n" +
            "                    return 1;\n" +
            "                case value1 === value2:\n" +
            "                    return 0;\n" +
            "            }\n" +
            "        });\n" +
            "    });\n" +
            "    data['sortKeys'] = sortableData;\n" +
            "    data['tbody'] = table.getElementsByTagName('tbody')[0];\n" +
            "    return data;\n" +
            "}\n" +
            "\n" +
            "const sortColumn = function(index, key, value) {\n" +
            "    let tableJson = tableData[index];\n" +
            "\n" +
            "    let rows = tableJson.rows;\n" +
            "    let tableBody = tableJson['tbody'];\n" +
            "    let newRows = Array.from(rows);\n" +
            "\n" +
            "    let invert = true;\n" +
            "    newRows.sort(function(rowA, rowB) {\n" +
            "\n" +
            "        let cellA = rowA.dataset[key];\n" +
            "        let cellB = rowB.dataset[key];\n" +
            "        if(invert){\n" +
            "            [cellA,cellB] = [cellB, cellA];\n" +
            "        }\n" +
            "\n" +
            "        if(cellA === value){\n" +
            "            if(cellB === value){\n" +
            "                return 0;\n" +
            "            }else{\n" +
            "                return 1;\n" +
            "            }\n" +
            "        }else if(cellB === value){\n" +
            "            return -1;\n" +
            "        }else{\n" +
            "            return 0;\n" +
            "        }\n" +
            "    });\n" +
            "\n" +
            "\n" +
            "    [].forEach.call(rows, function(row) {\n" +
            "        tableBody.removeChild(row);\n" +
            "    });\n" +
            "\n" +
            "\n" +
            "    newRows.forEach(function(newRow) {\n" +
            "        tableBody.appendChild(newRow);\n" +
            "    });\n" +
            "\n" +
            "    tableJson.rows = newRows;\n" +
            "};\n" +
            "const tableData = {};\n" +
            "let tables = document.getElementsByTagName('table');\n" +
            "for(let i=0; i<tables.length; i++){\n" +
            "    let table = tables[i];\n" +
            "    let tableJson = tableToJson(table);\n" +
            "    tableData[i] = tableJson;\n" +
            "    if(tableJson.sortKeys !== undefined) {\n" +
            "        let sortText = document.createElement('span');\n" +
            "        sortText.innerText=\"Sort By: \";\n" +
            "\n" +
            "        let select = document.createElement('select');\n" +
            "        select.id = 'table: '+i;\n" +
            "        Object.keys(tableJson.sortKeys).forEach(sortKey => {\n" +
            "            let sort = tableJson.sortKeys[sortKey];\n" +
            "            let parentNode;\n" +
            "            parentNode = document.createElement('optgroup');\n" +
            "            parentNode.label = sort.key;\n" +
            "            select.appendChild(parentNode);\n" +
            "            sort.possible.forEach(value => {\n" +
            "                let option = document.createElement('option');\n" +
            "                option.value = sort.key+\":\"+value;\n" +
            "                option.text = value;\n" +
            "                parentNode.appendChild(option);\n" +
            "            });\n" +
            "        });\n" +
            "        select.addEventListener('change',function(event){\n" +
            "            let tableID = parseInt(this.id.substring(6));\n" +
            "            let sortData = event.target.value.split(':');\n" +
            "            sortColumn(tableID, sortData[0], sortData[1]);\n" +
            "        });\n" +
            "        sortText.appendChild(select);\n" +
            "        table.prepend(sortText);\n" +
            "        /*\n" +
            "        let sortData = select.value.split(':');\n" +
            "        sortColumn(i,sortData[0],sortData[1]);\n" +
            "         */\n" +
            "    }\n" +
            "}\n";
}