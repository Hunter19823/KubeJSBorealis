package pie.ilikepiefoo2.kubejsborealis;

import pie.ilikepiefoo2.kubejsborealis.builder.BorealisHomePageEntryBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.HTTPWebPageBuilder;
import pie.ilikepiefoo2.kubejsborealis.builder.JSONWebPageBuilder;

public class BorealisWrapper {
    public BorealisHomePageEntryBuilder newHomePageEntry(String title, String url)
    {
        return new BorealisHomePageEntryBuilder(title,url);
    }
    public BorealisHomePageEntryBuilder newHomePageEntry(String title, String url, String icon)
    {
        return new BorealisHomePageEntryBuilder(title,url,icon);
    }
    public HTTPWebPageBuilder newHTTPPage()
    {
        return new HTTPWebPageBuilder();
    }
    public HTTPWebPageBuilder newHTMLPage()
    {
        return new HTTPWebPageBuilder();
    }
    public JSONWebPageBuilder newJSONPage()
    {
        return new JSONWebPageBuilder();
    }
}
