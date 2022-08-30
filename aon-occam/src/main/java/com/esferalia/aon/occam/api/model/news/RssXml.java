package com.esferalia.aon.occam.api.model.news;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RssXml{
	

	private static final String XML_FILE_PATH = System.getProperty("user.home") + "/xmlfile.xml";
	private static final String RSS = "rss";
	private static final String DESCRIPTION = "description";
	private static final String CATEGORY = "category";
	private static final String LINK = "link";
	private static final String TITLE = "title";
	private static final String CHANNEL = "channel";
	private static final String IMAGE = "image";
	private static final String URL = "url";
	private static final String ITEM = "item";
	private static final String LANGUAGE = "language";
	private static final String PUB_DATE = "pubDate";
	private static final String AUTHOR = "author";
	private static final String GUID = "guid";
	
	private static final SimpleDateFormat FORMAT_DATE =new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	//Wed, 30 Jun 2019 09:00:00 GMT
	
	public static byte[] writeXml() {
		  try {
			  
			  	ByteArrayOutputStream bos = new ByteArrayOutputStream();
			  	
	            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	 
	            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
	 
	            Document document = documentBuilder.newDocument();
	 
	            // root element
	            Element rssElement = buildRss(document);
	            document.appendChild(rssElement);
	            
	            buildChannels(document).forEach(rssElement::appendChild);
	      
	            // create the xml file
	            //transform the DOM Object to an XML File
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            DOMSource domSource = new DOMSource(document);
	            
	            // ----SAVE FILE TEST
	            StreamResult streamResult = new StreamResult(new File(XML_FILE_PATH));
	            transformer.transform(domSource, streamResult);
	            
	            // ----TRANSFORM TO BYTE
	            StreamResult result = new StreamResult(bos);
	            transformer.transform(domSource, result);
	            
	            return bos.toByteArray();
	        } catch (ParserConfigurationException pce) {
	            pce.printStackTrace();
	        } catch (TransformerException tfe) {
	            tfe.printStackTrace();
	        }
		return null;
	}

	public static void readXml(byte[] bytes) {
	  try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(new ByteArrayInputStream(bytes));
			
            document.getDocumentElement().normalize();
            
            getElementByName(document.getDocumentElement(), CHANNEL).ifPresent(channelList->{
                System.out.println("----------------------------");
                System.out.println("Root Element: "+ CHANNEL);
	            for (int i = 0; i < channelList.getLength(); i++) {
	            	System.out.println("---------------CHANNEL: "+ (i+1) +"-------------");
	                Node channelNode = channelList.item(i);
	                if (channelNode.getNodeType() == Node.ELEMENT_NODE) {
	                    Element channelElement = (Element) channelNode;
	                    
	                    getValueByName(channelElement, TITLE).ifPresent(title->{
	                    	System.out.println("title : " + title);
	                    });
	                    
	                    getValueByName(channelElement, DESCRIPTION).ifPresent(description->{
	                    	System.out.println("description : " + description);
	                    });
	                    
	                    getValueByName(channelElement, LINK).ifPresent(link->{
	                    	System.out.println("link : " + link);
	                    });
	                    
	                    getValueByName(channelElement, CATEGORY).ifPresent(category->{
	                    	System.out.println("category : " + category);
	                    });

	                    getValueByName(channelElement, PUB_DATE).ifPresent(pubDate->{
	                    	System.out.println("pubDate : " + pubDate);
	                    });
	                    
	                    getValueByName(channelElement, LANGUAGE).ifPresent(language->{
	                    	System.out.println("language : " + language);
	                    });
	          
	                    getElementByName(channelElement, ITEM).ifPresent(itemList->{
		                    System.out.println("----------------------------");
		                    System.out.println("Root Element: "+ ITEM);
	                    	  for (int j = 0; j < itemList.getLength(); j++) {
	                    		  System.out.println("---------------ITEM: "+ (j+1) +"-------------");
		                    	  Node itemNode = itemList.item(j);
		                    	  if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
		      	                    Element itemElement = (Element) itemNode;
		      	                    
		      	                    getValueByName(itemElement, TITLE).ifPresent(title->{
				                    	System.out.println("title : " + title);
				                    });
				                    
				                    getValueByName(itemElement, DESCRIPTION).ifPresent(description->{
				                    	System.out.println("description : " + description);
				                    });
				                    
				                    getValueByName(itemElement, LINK).ifPresent(link->{
				                    	System.out.println("link : " + link);
				                    });
				                    
				                    getValueByName(channelElement, AUTHOR).ifPresent(author->{
				                    	System.out.println("author : " + author);
				                    });

				                    getValueByName(channelElement, GUID).ifPresent(guid->{
				                    	System.out.println("guid : " + guid);
				                    });
				                    
				                    getValueByName(channelElement, PUB_DATE).ifPresent(pubDate->{
				                    	System.out.println("pubDate : " + pubDate);
				                    });
		      	                  }
		                    }
	                    });
	                }
	            }
            });
        } catch(ParserConfigurationException | SAXException | IOException e) {
        	e.printStackTrace();
        }
	}
	
	private static Element buildRss(Document document) {
           Element rssElement = createElement(document, RSS, null);
           rssElement.setAttribute("version", "2.0");
           rssElement.setAttribute("encoding", "UTF-8");
           rssElement.setAttribute("standalone", "no");
           return rssElement;
	}
	
	private static List<Element> buildChannels(Document document){
		List<Element> channels = new ArrayList<>();
        channels.add(buildChannel(document));
        return channels;
	}
	
	private static List<Element> buildItems(Document document){
        List<Element> items = new ArrayList<>();
		items.add(buildItem(document));
        return items;
	}
	
	private static Element buildChannel(Document document) {
        Element channelElement = createElement(document, CHANNEL, null);

        // title
		Element title = createElement(document, TITLE, "aonsolutions title");  
		channelElement.appendChild(title);
		
	    // description
		Element description = createElement(document, DESCRIPTION, "New RSS tutorial");  
		channelElement.appendChild(description);
		
	    // link
		Element link = createElement(document, LINK, "https://aonsolutions.org/"); 
		channelElement.appendChild(link);
		
	    // category OPT
		Element category = createElement(document, CATEGORY, "Laboral"); 
		channelElement.appendChild(category);
		
	    // pubDate OPT
		Element pubDate = createElement(document, PUB_DATE, FORMAT_DATE.format(new Date())); 
		channelElement.appendChild(pubDate);
	    // language OPT
		Element language = createElement(document, LANGUAGE, "es"); 
		channelElement.appendChild(language);
		
	    // image OPT
		Element image = buildImage(document);
		channelElement.appendChild(image);
		
		//items
		buildItems(document).forEach(channelElement::appendChild);
	
        return channelElement;
	}
	
	private static Element buildImage(Document document) {
		Element image = createElement(document, IMAGE, null);
		
		String urlStr = "https://www.trecebits.com/wp-content/uploads/2021/04/rss-feed.jpg";
		
		//url
		Element url = createElement(document, URL, urlStr); 
		image.appendChild(url);
		
		//title
		Element title = createElement(document, TITLE, urlStr); 
		image.appendChild(title);
		
		//link
		Element link = createElement(document, LINK, urlStr); 
		image.appendChild(link);
		
		return image;
	}

	private static Element buildItem(Document document) {
		Element item = createElement(document, ITEM, null);  
		
		 // title
		Element title = createElement(document, TITLE, "ITEM title");
		item.appendChild(title);
		
	    // description
		Element description = createElement(document, DESCRIPTION, "ITEM RSS tutorial");
		item.appendChild(description);
		
	    // link
		Element link = createElement(document, LINK, "https://aonsolutions.org/"); 
		item.appendChild(link);
		
	    // author OPT
		Element author = createElement(document, AUTHOR, "AON");
		item.appendChild(author);
	
	    // guid OPT
		Element guid =  createElement(document, GUID, "guid"); 
		item.appendChild(guid);
		
	    // pubDate OPT
		Element pubDate = createElement(document, PUB_DATE, FORMAT_DATE.format(new Date()));  
		item.appendChild(pubDate);
		
		return item;
	}
	
	private static Element createElement(Document document, String name, String value) {
		Element element = document.createElement(name);
		if(value!=null) {
			element.appendChild(document.createTextNode(value));
		}
		return element;
	}
	
	private static Optional<String> getValueByName(Element element, String name) {
		Optional<NodeList> elementNode = getElementByName(element, name);
		if(elementNode.isPresent()) {
			return Optional.ofNullable(elementNode.get().item(0).getTextContent());
		}
		return Optional.empty();
	}
	
	private static Optional<NodeList> getElementByName(Element element, String name) {
		NodeList elementName = element.getElementsByTagName(name);
		if(elementName.getLength()>0) {
			return Optional.ofNullable(elementName);
		}
		return Optional.empty();
	}
	
	public static void main(String[] args) {
		byte[] bytes = writeXml();
		readXml(bytes);
		
		System.out.println("creado xml ---> "+ XML_FILE_PATH);
	}
}

