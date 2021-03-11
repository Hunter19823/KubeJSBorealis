package pie.ilikepiefoo2.kubejsborealis.events;

import dev.latvian.kubejs.event.EventJS;
import pie.ilikepiefoo2.borealis.BorealisPageEvent;
import pie.ilikepiefoo2.borealis.BorealisServer;
import pie.ilikepiefoo2.borealis.page.WebPage;
import pie.ilikepiefoo2.kubejsborealis.builder.WebPageBuilder;

/**
 * @author ILIKEPIEFOO2
 */
public class BorealisPageEventJS extends EventJS {
    private final BorealisPageEvent event;

    public BorealisPageEventJS(BorealisPageEvent event)
    {
        this.event = event;
    }

    public boolean checkPath(String... path)
    {
        return this.event.checkPath(path);
    }

    public void returnPage(WebPage webPage)
    {
        this.event.returnPage(webPage);
    }
    public void returnPage(WebPageBuilder<?> webPage)
    {
        this.event.returnPage(webPage.build());
    }

    public String[] getSplitUri()
    {
        return this.event.getSplitUri();
    }
    public String getUri()
    {
        return this.event.getUri();
    }

    public BorealisServer getServer()
    {
        return this.event.getBorealisServer();
    }
}
