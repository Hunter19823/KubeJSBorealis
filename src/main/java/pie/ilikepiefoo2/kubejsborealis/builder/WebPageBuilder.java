package pie.ilikepiefoo2.kubejsborealis.builder;

import pie.ilikepiefoo2.borealis.page.WebPage;

public interface WebPageBuilder<T extends WebPage> {
    T build();
}
