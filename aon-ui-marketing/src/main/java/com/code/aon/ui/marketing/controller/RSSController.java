package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.Criteria;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.marketing.News;
import com.code.aon.registry.Category;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class RSSController {

	private static final Logger LOGGER = LoggerFactory.getLogger(RSSController.class.getName());
	
	public static final String RSS_FILE = "rss.xml";
	public static final String CHANNEL_PARAMETER = "channel";
	
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String LINK_ELEMENT = "link";
	private static final String TITLE_ELEMENT = "title";
	private static final String CHANNEL_ELEMENT = "channel";
	private static final String IMAGE_ELEMENT = "image";
	private static final String URL_ELEMENT = "url";
	private static final String ITEM_ELEMENT = "item";
	private static final String RSS_ELEMENT = "rss";
	private static final String PUB_DATE_ELEMENT = "pubDate";
	
	private static SimpleDateFormat RFC822DATEFORMAT =
			new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	
	public Category category;
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	public void onInit( ActionEvent event ) throws IOException {
		setCategory(null);
	}
	
	public String getDownloadURL() {
		String url = null;
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		try {
			url = ds.getDomainURL() + "/" + RSS_FILE;
			if ( (category != null) && (category.getId() != null) ) {
				url += "?" + CHANNEL_PARAMETER + "=" + category.getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
		return url;
	}

	public void onDownloadRSS( ActionEvent event ) throws IOException {
		Document document = null;
		StatelessSession session = null;
		try {
			String sfn = HibernateUtil.getSessionFactoryName(News.class.getName());
			session = HibernateUtil.getSessionFactory(sfn).openStatelessSession();
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			document = createDocument(session, ds.getDomainURL());			
		} catch ( Throwable e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			if ( session != null ) {
				session.close();
			}
		}
		if ( document != null ) {
	        byte[] data = getData(document);
			InputStream in = new ByteArrayInputStream(data);	
	        DownloadUtil.downloadAttachment(RSS_FILE, MimeType.MIME_RSS, in, data.length);			
		}
	}
	
	private static String getImageURL( RegistryAttachment ra, String urlPreffix ) {
		if ( (ra != null) && (ra.getId() != null) ) {
			return urlPreffix + ra.getDownloadURL();
		}
		return null;
	}	
	
	private static Element createChannel( Element root, Category category, String urlPreffix ) {
		Element channel = root.addElement(CHANNEL_ELEMENT);
		channel.addElement(TITLE_ELEMENT).addText( category.getName() );
		channel.addElement(LINK_ELEMENT).addText( category.getUrl() );
		channel.addElement(DESCRIPTION_ELEMENT).addText( category.getDescription() );
		String url = getImageURL(category.getRegistryAttachment(), urlPreffix);
		if ( url != null ) {
			Element image = channel.addElement(IMAGE_ELEMENT);
			image.addElement(TITLE_ELEMENT).addText( category.getName() );
			image.addElement(URL_ELEMENT).addText(url);
			image.addElement(LINK_ELEMENT).addText( category.getUrl() );
		}
		return channel;
	}

	private static Element createItem( Element channel, News news ) {
		Element item = channel.addElement(ITEM_ELEMENT);
		item.addElement(TITLE_ELEMENT).addText( news.getTitle() );
		item.addElement(LINK_ELEMENT).addText( news.getUrl() );
		item.addElement(DESCRIPTION_ELEMENT).addText( news.getDescription() );
		if ( news.getInitDate() != null ) {
			String date = RFC822DATEFORMAT.format(news.getInitDate());
			item.addElement(PUB_DATE_ELEMENT).addText( date );
		}
		return item;
	}
	
	private static Document createDocument( StatelessSession session, String urlPreffix ) {
		 Document document = DocumentHelper.createDocument();
		 Element root = document.addElement( RSS_ELEMENT );
		 root.addAttribute("version", "2.0");
		 
		 Criteria criteria = session.createCriteria(News.class);
		 criteria.add(Restrictions.eq("active", Boolean.TRUE));
		 criteria.add(Restrictions.eq("rss", Boolean.TRUE));
		 Date now = new Date();
		 Criterion initDateExpr1 = Restrictions.isNull("initDate");
		 Criterion initDateExpr2 = Restrictions.le("initDate", now);
		 criteria.add(Restrictions.or(initDateExpr1, initDateExpr2));
		 Criterion endDateExpr1 = Restrictions.isNull("endDate");
		 Criterion endDateExpr2 = Restrictions.ge("endDate", now);
		 criteria.add(Restrictions.or(endDateExpr1, endDateExpr2));

		 Map<Integer,Element> channels = new HashMap<Integer, Element>();
		 for( Object to : criteria.list() ) {
			 News news = (News) to;
			 Element channel = channels.get(news.getCategory().getId());
			 if ( channel == null ) {
				 channel = createChannel(root, news.getCategory(), urlPreffix);
				 channels.put(news.getCategory().getId(), channel);
			 }
			 createItem(channel, news);
		 }		 
		 return document;
	}
	
	private static byte[] getData( Document document ) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter sw = new StringWriter();
		XMLWriter writer = new XMLWriter(sw, format );
		writer.write( document );
		writer.close();
		return sw.toString().getBytes(format.getEncoding());		
	}
	
	public static byte[] getRSS( StatelessSession session, String urlPreffix ) throws IOException {
		Document document = createDocument(session, urlPreffix);
		return getData(document);		
	}
	
}