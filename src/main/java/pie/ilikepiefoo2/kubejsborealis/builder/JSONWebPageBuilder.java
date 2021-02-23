package pie.ilikepiefoo2.kubejsborealis.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.util.JsonUtilsJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import pie.ilikepiefoo2.borealis.page.JsonWebPage;

import java.util.function.Consumer;

public class JSONWebPageBuilder implements WebPageBuilder<JsonWebPage> {
    public JsonElement element = new JsonObject();

    public JSONWebPageBuilder json(String element)
    {
        this.element = JsonUtilsJS.fromString(element);
        return this;
    }public JSONWebPageBuilder json(JsonElement element)
    {
        this.element = element;
        return this;
    }
    public JSONWebPageBuilder json(Object element)
    {
        this.element = MapJS.of(element).toJson();
        return this;
    }

    @Override
    public JsonWebPage build()
    {
        JsonWebPage page = new JsonWebPage() {
            @Override
            public JsonElement getJson()
            {
                return element;
            }
        };
        return page;
    }
}
