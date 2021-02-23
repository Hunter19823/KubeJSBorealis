
onEvent('borealis.homepage', event => {
    console.info(Object.keys(event));
    let customHomePage = HomePageEntry(
        "Custom HomePage added by KubeJS Script",
        "custom",
        "https://media.forgecdn.net/avatars/thumbnails/341/801/64/64/637485008791691372.png"
    );
    customHomePage.add(
        "Custom JSON Page",
        "custom.json",
        "https://i.imgur.com/OVxZy1w.png"
    );
    event.add(customHomePage);

    console.info("homepage event fired");
});
onEvent('borealis.page', event => {
    console.info(Object.keys(event));
    if(event.checkPath('custom','custom.json')){
        let customJsonPage = JSONWebPage()
            .json(
                {
                    'Name': "Hi I'm a custom JSON sent from a KubeJS Server Script!",
                    "Purpose" : "My purpose is to allow server/pack developers with the ability to export custom data from the minecraft server using KubeJS.",
                    "Uses?" : "Some of my potential uses involve custom embeds in webpages or server usage statitistics."
                }
            );
        event.returnPage(customJsonPage);
    }else if(event.checkPath('custom')){
        let customPage = HTMLPage()
            .body( (body) => {
                body.h1("Welcome to my custom KubeJS page!");
                body.h3("I'm a nifty tool for server/pack developers that gives the ability to add custom homepages for their minecraft server.");

                let table = body.table();
                let row = table.tr();
                row.th().text("Use");
                row.th().text("Explanation");

                row = table.tr();
                row.td().text("Voting Links");
                row.td().text("Ever wanted a single place to put all of a server's voting links? Why not make a webpage for that?")
                    .text(" It is reloadable with KubeJS scripts, meaning it can be updated without restarting the server.");

                row = table.tr();
                row.td().text("Whitelisted Server");
                let description = row.td().span("Are you a whitelisted server that has troubles centralizing your whitelisting information?");
                description.br();
                description.span("Why not integrate it directly into your server and just link users to your custom webpage?");
                description.br();
                description.span("The only caveat might be that the details page only exists while your server is online.");


                row = table.tr();
                row.td().a("Custom JSON Files","custom/custom.json");
                description = row.td().span("Is there some bit of minecraft data you wish you could display on your website? Or maybe could dump into a JSON?");
                description.br();
                description.span("Well you can generate the JSON in KubeJS and link it using a custom JSON Page.");
            })
            .title('KJS Custom Page');
        event.returnPage(customPage);
    }
    console.info("page event fired");
});
