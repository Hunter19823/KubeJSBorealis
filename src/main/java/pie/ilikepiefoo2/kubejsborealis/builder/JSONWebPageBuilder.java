package pie.ilikepiefoo2.kubejsborealis.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import pie.ilikepiefoo2.borealis.page.JsonWebPage;

/**
 * @author ILIKEPIEFOO2
 */
public class JSONWebPageBuilder implements WebPageBuilder<JsonWebPage> {
    public JsonElement element = new JsonObject();

    public JSONWebPageBuilder json(String element)
    {
        this.element = JsonUtils.fromString(element);
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
