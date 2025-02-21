import { CreateComponent } from "../../../components/CreateComponent.js";
import { CSS, TAG, EVENT } from "../../../environments/environments.js";
import { createDiv } from "../../../services/utilsComponents.js";

// import { xmlToJson } from "../../../services/xmlToJson.js";

import '../../../css/aon-rss.css';

const openRss = async (rss) => {
    // const resElement = buildRss(rss);

    // const xml = new XMLSerializer().serializeToString(resElement);
    // const blob = new Blob([xml], {type: "application/rss+xml"});
    
    // const url = URL.createObjectURL(blob);

    // open(url, '_blank');

    // const link = document.createElement('a');
    // link.href = url;
    // link.download = 'rss.xml';
    // document.body.appendChild(link);

    // link.click();

    // setTimeout(() => {
    //     URL.revokeObjectURL(url);
    //     document.body.removeChild(link);
    // }, 0)



    const url = ".../../../assets/json/rss1.xml";
    const res = await fetch(url);
    const xmlText = await res.text();
    const parser = new DOMParser().parseFromString(xmlText, "text/xml");
    const json = xmlToJson(parser, " ");

    
    buildRssView(json);
}

/**
 * 
 * @param {Object} json 
 */
const buildRssView = (json) => {

    const {rss:{channel}} = json;

    let rssMain = document.getElementsByClassName("rss-respuesta")[0];
    rssMain.innerHTML = "";

    let h2 = document.createElement("h2");
    h2.innerHTML = channel.title;
    rssMain.appendChild(h2);

    let div = document.createElement(TAG.DIV);
    div.className = "caja_rss_respuestas";
    rssMain.appendChild(div);

    for (const item of channel.item) {
        console.log(item);

        let resRespuesta = document.createElement(TAG.DIV);
        resRespuesta.className = "rss_respuesta";
        div.appendChild(resRespuesta);

        let resTitulo = document.createElement("strong");
        resTitulo.innerHTML = item.title;
        resTitulo.style.cursor = "pointer";
        resRespuesta.appendChild(resTitulo);
        
        let resEnlace = document.createElement("a");
        resEnlace.href = item.link;
        resEnlace.innerHTML = `<img src="../../../assets/aon.png" align="absmiddle" style="width:15px;" />`;
        resRespuesta.appendChild(resEnlace);

        let resTexto = document.createElement(TAG.DIV);
        resTexto.style.display = "none";
        resTexto.innerHTML = item.description;
        resRespuesta.appendChild(resTexto);

        resTitulo.addEventListener(EVENT.CLICK, ()=>{
            const display = resTexto.style.display;
            resTexto.style.display = display === "none" ? "block" : "none";
        });
    }
}

const buildRss = (rss) => {
    const rssElement = document.createElement('rss');
    rssElement.setAttribute('version', '2.0');

    rss.channel
    .map(channel => buildChannel(channel) )
    .forEach( channel => rssElement.appendChild(channel) );

    return rssElement;
}

const buildChannel = (channel) => {
    const channelElement = document.createElement('channel');

    const title = document.createElement('title');
    title.innerHTML = channel.title;
    channelElement.appendChild(title);
        
    const description = document.createElement('description');
    description.innerHTML = channel.description;
    channelElement.appendChild(description);

    const link = document.createElement('link');
    link.innerHTML = channel.link;
    channelElement.appendChild(link);

    if(channel.category){
        const category = document.createElement('category');
        category.innerHTML = channel.category;
        channelElement.appendChild(category);
    }

    if(channel.pubDate){
        const pubDate = document.createElementNS(null, 'pubDate');
        pubDate.innerHTML = channel.pubDate;
        channelElement.appendChild(pubDate);
    }

    if(channel.language){
        const language = document.createElement('language');
        language.innerHTML = channel.language;
        channelElement.appendChild(language);
    }
    
    const image = channel.image;
    if(image){
        const imgEl = document.createElement('image');
        channelElement.appendChild(imgEl);

        const url = document.createElement('url');
        url.innerHTML = image.url;
        imgEl.appendChild(url);

        const imgTitle = document.createElement('title');
        imgTitle.innerHTML = image.title;
        imgEl.appendChild(imgTitle);

        const imgLink = document.createElement('link');
        imgLink.innerHTML = image.link;
        imgEl.appendChild(imgLink);
    }

    channel.item
    .map(item => buildRssItem(item) )
    .forEach( item => channelElement.appendChild(item) );
    
    return channelElement;
}

const buildRssItem = (item) => {
    const itemElement = document.createElement('item');

    const title = document.createElement('title');
    title.innerHTML = item.title;
    itemElement.appendChild(title);

    const description = document.createElement('description');
    description.innerHTML = item.description;
    itemElement.appendChild(description);

    const link = document.createElement('link');
    link.innerHTML = item.link;
    itemElement.appendChild(link);

    if(item.author){
        const author = document.createElement('author');
        author.innerHTML = item.author;
        itemElement.appendChild(author);
    }

    if(item.guid){
        const guid = document.createElement('guid');
        guid.innerHTML = item.link;
        itemElement.appendChild(guid);
    }

    if(item.pubDate){
        const pubDate = document.createElementNS(null, 'pubDate');
        pubDate.innerHTML = item.pubDate;
        itemElement.appendChild(pubDate);
    }

    return itemElement;
}

const formatPublishedDateForDateTime = (dateString) => {
    const timestamp = Date.parse(dateString);
    const date = new Date(timestamp);
    return `${date.getFullYear()}-${addLeadingZero(date.getMonth() + 1)}-${date.getDate()}`;
};
  
const formatPublishedDateForDisplay = (dateString) => {
    const timestamp = Date.parse(dateString);
    const date = new Date(timestamp);
    return `${date.getDate()} ${getMonthStringFromInt(date.getMonth())} ${date.getFullYear()}`;
};
  

const addLeadingZero = (num) => {
    num = num.toString();
    while (num.length < 2) num = "0" + num;
    return num;
};

const getMonthStringFromInt = (int) => {
    const months = [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec",
    ];

    return months[int];
};

/**
 * DELETE
 */
const createRssViewer = () => {
    const divParent = document.getElementById("aonRssAddDiv");

    let div = createDiv({classes:[CSS.AON_COL_SM_6]});
    div.appendTo(divParent);
    const cardThree = CreateComponent.createAonCard({id: "cardThree", title:"test"}, div);

    const content = cardThree.getContent();

    const rssMain = document.createElement(TAG.DIV);
    rssMain.className = "rss-respuesta";
    content.appendChild(rssMain);
}


export const RssUtils = {
    openRss
}