package pie.ilikepiefoo2.kubejsborealis.builder;

import pie.ilikepiefoo2.borealis.BorealisHomePageEvent;
import pie.ilikepiefoo2.borealis.page.HomePageEntry;

import java.util.ArrayList;
import java.util.List;

public class BorealisHomePageEntryBuilder {
    public HomePageEntry homePageEntry;
    public List<BorealisHomePageEntryBuilder> entries;

    public BorealisHomePageEntryBuilder(String title, String url)
    {
        homePageEntry = new HomePageEntry(title, url);
        this.entries = new ArrayList<>();
    }

    public BorealisHomePageEntryBuilder(String title, String url, String icon)
    {
        this.homePageEntry = new HomePageEntry(title, url, icon);
        this.entries = new ArrayList<>();
    }

    public BorealisHomePageEntryBuilder add(String title, String url)
    {
        BorealisHomePageEntryBuilder builder = new BorealisHomePageEntryBuilder(title, url);
        this.entries.add(builder);
        return builder;
    }

    public BorealisHomePageEntryBuilder add(String title, String url, String icon)
    {
        BorealisHomePageEntryBuilder builder = new BorealisHomePageEntryBuilder(title, url, icon);
        this.entries.add(builder);
        return builder;
    }

    public HomePageEntry build()
    {
        for(BorealisHomePageEntryBuilder entry : this.entries){
            this.homePageEntry.add(entry.build());
        }
        return this.homePageEntry;
    }

}
