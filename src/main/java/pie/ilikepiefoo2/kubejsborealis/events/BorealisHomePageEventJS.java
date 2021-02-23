package pie.ilikepiefoo2.kubejsborealis.events;

import dev.latvian.kubejs.event.EventJS;
import pie.ilikepiefoo2.borealis.BorealisHomePageEvent;
import pie.ilikepiefoo2.borealis.BorealisServer;
import pie.ilikepiefoo2.borealis.page.HomePageEntry;
import pie.ilikepiefoo2.kubejsborealis.builder.BorealisHomePageEntryBuilder;

public class BorealisHomePageEventJS extends EventJS {
    public BorealisHomePageEvent event;

    public BorealisHomePageEventJS(BorealisHomePageEvent e)
    {
        this.event = e;
    }

    public void add(HomePageEntry entry)
    {
        this.event.add(entry);
    }
    public void add(BorealisHomePageEntryBuilder entryBuilder)
    {
        this.event.add(entryBuilder.build());
    }
    public void add(String title, String url)
    {
        HomePageEntry entry = new HomePageEntry(title,url);
        this.event.add(entry);
    }
    public void add(String title, String url, String icon)
    {
        HomePageEntry entry = new HomePageEntry(title,url,icon);
        this.event.add(entry);
    }

    public BorealisServer getServer()
    {
        return this.event.getBorealisServer();
    }
}
