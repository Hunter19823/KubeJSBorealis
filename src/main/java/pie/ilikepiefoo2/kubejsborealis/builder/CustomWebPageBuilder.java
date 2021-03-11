package pie.ilikepiefoo2.kubejsborealis.builder;


import io.netty.handler.codec.http.HttpResponseStatus;
import pie.ilikepiefoo2.borealis.page.PageType;
import pie.ilikepiefoo2.borealis.page.WebPage;

import java.util.function.Supplier;

/**
 * @author ILIKEPIEFOO2
 */
public class CustomWebPageBuilder implements WebPageBuilder<WebPage> {
    public Supplier<String> getContentType;
    public Supplier<String> getContent;
    public Supplier<HttpResponseStatus> getStatus;
    public Supplier<PageType> getPageType;

    public Supplier<String> getContentType()
    {
        return getContentType;
    }

    public CustomWebPageBuilder setContentType(Supplier<String> getContentType)
    {
        this.getContentType = getContentType;
        return this;
    }

    public Supplier<String> getGetContent()
    {
        return getContent;
    }

    public CustomWebPageBuilder setGetContent(Supplier<String> getContent)
    {
        this.getContent = getContent;
        return this;
    }

    public Supplier<HttpResponseStatus> getGetStatus()
    {
        return getStatus;
    }

    public CustomWebPageBuilder setGetStatus(Supplier<HttpResponseStatus> getStatus)
    {
        this.getStatus = getStatus;
        return this;
    }

    public Supplier<PageType> getGetPageType()
    {
        return getPageType;
    }

    public CustomWebPageBuilder setGetPageType(Supplier<PageType> getPageType)
    {
        this.getPageType = getPageType;
        return this;
    }

    @Override
    public WebPage build()
    {
        final WebPage page = new WebPage() {
            @Override
            public String getContentType()
            {
                return getContentType == null ? "text/html" : getContentType.get();
            }

            @Override
            public HttpResponseStatus getStatus()
            {
                return getStatus == null ? HttpResponseStatus.OK : getStatus.get();
            }

            @Override
            public PageType getPageType()
            {
                return getPageType == null ? PageType.ENABLED : getPageType.get();
            }

            @Override
            public String getContent()
            {
                return getContent == null ? "No content supplier provided." : getContent.get();
            }
        };
        return page;
    }
}
