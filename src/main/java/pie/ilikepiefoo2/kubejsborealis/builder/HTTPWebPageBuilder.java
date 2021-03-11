package pie.ilikepiefoo2.kubejsborealis.builder;

import pie.ilikepiefoo2.borealis.page.HTTPWebPage;
import pie.ilikepiefoo2.borealis.tag.Tag;

import java.util.function.Consumer;

/**
 * @author ILIKEPIEFOO2
 */
public class HTTPWebPageBuilder implements WebPageBuilder<HTTPWebPage> {
    public Consumer<Tag> header;
    public Consumer<Tag> body;
    public String icon = null;
    public String title = null;
    public String description = null;
    public boolean backButton = false;
    public boolean isAlwaysDirty = true;

    public HTTPWebPageBuilder icon(String icon)
    {
        this.icon = icon;
        return this;
    }
    public HTTPWebPageBuilder title(String title)
    {
        this.title = title;
        return this;
    }
    public HTTPWebPageBuilder description(String description)
    {
        this.description = description;
        return this;
    }
    public HTTPWebPageBuilder head(Consumer<Tag> header)
    {
        this.header = header;
        return this;
    }
    public HTTPWebPageBuilder body(Consumer<Tag> body)
    {
        this.body = body;
        return this;
    }
    public HTTPWebPageBuilder addBackButton(boolean backButton)
    {
        this.backButton = backButton;
        return this;
    }
    public HTTPWebPageBuilder addBackButton()
    {
        this.backButton = true;
        return this;
    }

    public HTTPWebPageBuilder markAlwaysDirty()
    {
        this.isAlwaysDirty = true;
        return this;
    }
    public HTTPWebPageBuilder markNeverDirty()
    {
        this.isAlwaysDirty = false;
        return this;
    }

    public HTTPWebPage build()
    {
        final HTTPWebPage webPage = new HTTPWebPage(){
            @Override
            public String getTitle()
            {
                return title == null ? super.getTitle() : title;
            }

            @Override
            public String getDescription()
            {
                return description == null ? super.getDescription() : description;
            }

            @Override
            public String getIcon()
            {
                return icon == null ? super.getIcon() : icon;
            }

            @Override
            public boolean addBackButton()
            {
                return backButton;
            }

            @Override
            public void head(Tag head)
            {
                super.head(head);
                if(header != null)
                    header.accept(head);
            }

            @Override
            public void body(Tag tag)
            {
                super.head(tag);
                if(body != null)
                    body.accept(tag);
            }
        };
        if(isAlwaysDirty){
            webPage.alwaysDirty();
        }else{
            webPage.neverDirty();
        }

        return webPage;
    }

}
