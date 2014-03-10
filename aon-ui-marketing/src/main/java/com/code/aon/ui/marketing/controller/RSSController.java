package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.code.aon.ui.config.controller.ConfigConstants.PUBLISH_PARAMETER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.EscapeTool;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.marketing.News;
import com.code.aon.registry.Category;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.PublishProperties;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.controller.PublishParameterController;
import com.code.aon.ui.config.util.FTPUtil;
import com.code.aon.ui.marketing.servlet.RSSServlet;
import com.code.aon.ui.util.AonUtil;

public class RSSController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(RSSController.class.getName());
	
	public static final String RSS_PREFFIX = "rss";
	public static final String RSS_REGEX = RSS_PREFFIX + "-(\\d+)\\." + MimeType.MIME_XML.getExtension();

	public static final String PREVIEW_PREFFIX = "preview";
	public static final String PREVIEW_REGEX = "\\w+-(\\d+)\\." + MimeType.MIME_HTML.getExtension();
	public static final String PREVIEW_PATH = "/com/code/aon/ui/marketing/facelet/rss/preview/";

	private static final String ESC_TOOL_PARAMETER = "esc";
	private static final String PARAMETERS_PARAMETER = "parameters";
	private static final String URL_SUFFIX_PARAMETER = "URLSuffix";
	private static final String CHANNEL_URL_PARAMETER = "channelURL";
	private static final String CHANNELS_PARAMETER = "channels";
	
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String LINK_ELEMENT = "link";
	private static final String TITLE_ELEMENT = "title";
	private static final String CHANNEL_ELEMENT = "channel";
	private static final String IMAGE_ELEMENT = "image";
	private static final String URL_ELEMENT = "url";
	private static final String ITEM_ELEMENT = "item";
	private static final String RSS_ELEMENT = "rss";
	private static final String PUB_DATE_ELEMENT = "pubDate";
	private static final String LAST_BUILD_DATE_ELEMENT = "lastBuildDate";
	
	private static SimpleDateFormat RFC822DATEFORMAT =
			new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
	
	private Category category;

	private PublishProperties publishProperties;
	
	private LogPanelController log = LogPanelController.getInstance();
	
	private Long numberOfNews;
	
	private Date date;
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
	
	private String createDownloadURL( String preffix, Category category, MimeType mimeType ) {
		boolean addSeparator = false; 
		String url = getURL(preffix, category, mimeType);
		if ( this.numberOfNews != null ) {
			url += "?" + RSSServlet.LIMIT_PARAMETER +  "=" + this.numberOfNews;
			addSeparator = true;
		}
		if ( this.date != null ) {
			url += (addSeparator ? "&" : "?" )+ RSSServlet.DATE_PARAMETER +  "=" + this.date.getTime();
		}
		return url;		
	}
	
	public String getDownloadURL() {
		return createDownloadURL(RSS_PREFFIX, category, MimeType.MIME_XML);
	}
	
	public String getPreviewURL() {
		return createDownloadURL(RSSController.PREVIEW_PREFFIX, category, MimeType.MIME_HTML);
	}	

	private byte[] getRSS() throws IOException {
		Document document = null;
		Session session = null;
		String sfn = HibernateUtil.getSessionFactoryName(News.class.getName());
		try {
			session = HibernateUtil.getSession(sfn);
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			Integer channelId = ( category != null ) ? category.getId() : null;
			document = createDocument(session, ds.getDomainId(), channelId, ds.getDomainURL(), null);			
		} catch ( Throwable e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			if ( session != null ) {
				HibernateUtil.closeSession(sfn);
			}
		}
		if ( document != null ) {
	        return getData(document);			
		}
		return null;
	}
	
	private static String getImageURL( RegistryAttachment ra, String urlPreffix ) {
		if ( ra!=null && ra.getId()!=null ) {
			return urlPreffix + ra.getDownloadURL();
		}
		return null;
	}	
	
	private static Element createChannel( Element root, Category category, String urlPreffix ) {
		Element channel = root.addElement(CHANNEL_ELEMENT);
		channel.addElement(TITLE_ELEMENT).addText( category.getName() );
		channel.addElement(LINK_ELEMENT).addText( category.getUrl() );
		channel.addElement(DESCRIPTION_ELEMENT).addText( category.getDescription() );
		channel.addElement(LAST_BUILD_DATE_ELEMENT).addText( RFC822DATEFORMAT.format(new Date()) );
		String url = getImageURL(category.getRegistryAttachment(), urlPreffix);
		if ( url != null ) {
			Element image = channel.addElement(IMAGE_ELEMENT);
			image.addElement(TITLE_ELEMENT).addText( category.getName() );
			image.addElement(URL_ELEMENT).addText(url);
			image.addElement(LINK_ELEMENT).addText( category.getUrl() );
		}
		return channel;
	}

	private static Element createItem( Element channel, News news, String urlPreffix ) {
		Element item = channel.addElement(ITEM_ELEMENT);
		item.addElement(TITLE_ELEMENT).addText( news.getTitle() );
		item.addElement(LINK_ELEMENT).addText( NewsController.getDownloadURL(news, urlPreffix) );
		item.addElement(DESCRIPTION_ELEMENT).addText( news.getDescription() );
		if ( news.getInitDate() != null ) {
			String date = RFC822DATEFORMAT.format(news.getInitDate());
			item.addElement(PUB_DATE_ELEMENT).addText( date );
		}
		return item;
	}
	
	private static Document createDocument( Session session, Integer domainId, Integer channelId, String urlPreffix, Date date ) {
		 Document document = DocumentHelper.createDocument();
		 Element root = document.addElement( RSS_ELEMENT );
		 root.addAttribute("version", "2.0");
		 
		 Criteria criteria = session.createCriteria(News.class);
		 criteria.add(Restrictions.eq("domain", domainId));
		 criteria.add(Restrictions.eq("active", Boolean.TRUE));
		 criteria.add(Restrictions.eq("rss", Boolean.TRUE));
		 if ( channelId != null ) {
			 criteria.add(Restrictions.eq("category.id", channelId));
		 }
		 Date referenceDate = (date == null) ? new Date() : date;
		 Criterion initDateExpr1 = Restrictions.isNull("initDate");
		 Criterion initDateExpr2 = Restrictions.le("initDate", referenceDate);
		 criteria.add(Restrictions.or(initDateExpr1, initDateExpr2));
		 Criterion endDateExpr1 = Restrictions.isNull("endDate");
		 Criterion endDateExpr2 = Restrictions.ge("endDate", referenceDate);
		 criteria.add(Restrictions.or(endDateExpr1, endDateExpr2));
		 criteria.addOrder( Order.desc("initDate") );

		 Map<Integer,Element> channels = new HashMap<Integer, Element>();
		 for( Object to : criteria.list() ) {
			 News news = (News) to;
			 Element channel = channels.get(news.getCategory().getId());
			 if ( channel == null ) {
				 channel = createChannel(root, news.getCategory(), urlPreffix);
				 channels.put(news.getCategory().getId(), channel);
			 }
			 createItem(channel, news, urlPreffix);
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
	
	public static byte[] getRSS( Session session, Integer domainId, Integer channelId, String urlPreffix, Date date ) throws IOException {
		Document document = createDocument(session, domainId, channelId, urlPreffix, date);
		return getData(document);		
	}
	
	public void onInit(ActionEvent event) {
		setCategory(null);
		setDate(null);
		setNumberOfNews(null);
		PublishParameterController ppc = (PublishParameterController) AonUtil.getRegisteredBean(PUBLISH_PARAMETER);
		this.publishProperties = ppc.getPublishProperties();
	}		

	public void onPublish(ActionEvent event) throws IOException  {
		byte[] data = getRSS();
		if (! ArrayUtils.isEmpty(data) ) {
			InputStream in = new ByteArrayInputStream(data);
			String fileName = getRSSFileName(RSS_PREFFIX, category, MimeType.MIME_XML);
			upload(this.publishProperties.getPublishPath(), fileName, in, data.length);			
		}
	}	
	
	private void upload( String destination, String name, InputStream in, int length ) {
		FTPUtil ftp = new FTPUtil(log);
		try {
			ftp.connect(publishProperties.getFtpProperties());
			if ( ftp.isConnected() ) {
				String path = ftp.getFTPPath(destination, name);
				if ( ftp.upload(in, length, path) ) {
					log.info( AonUtil.getMessage(ICommonMessages.RSS_PUBLISH_OK) );		
				} else {
					log.error( AonUtil.getMessage(ICommonMessages.RSS_PUBLISH_ERROR) );
				}
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th );
			log.error( AonUtil.getMessage(ICommonMessages.RSS_PUBLISH_ERROR) );
		} finally {
			ftp.close();
		}
		log.finish();
	}
	
	private static String getServletURLPreffix() {
		String url = null;
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		try {
			url = ds.getDomainURL() + RSSServlet.SERVLET_PATH;
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
		return url;		
	}

	public static String getURL( String preffix, Category category, MimeType mimeType ) {
		return getServletURLPreffix() + getRSSFileName(preffix, category, mimeType);
	}
		
	private static String getRSSFileName( String preffix, Category category, MimeType mimeType ) {
		StringBuffer url = new StringBuffer();
		url.append(preffix);
		if ( category!=null && category.getId()!=null ) {
			url.append('-').append(category.getId());
		}
		url.append('.').append(mimeType.getExtension());
		return url.toString();
	}

	public static String getPreviewHtml( String value, String urlPreffix, List<Category> channels, String limit, Date date ) {
		try {
			String template = StringUtils.substringBefore(FilenameUtils.getBaseName(value), "-");
			VelocityHelper velocityHelper = new VelocityHelper();
			velocityHelper.init( PREVIEW_PATH );
			TemplateHelper th = velocityHelper.getTemplateHelper();
			th.putInContext(ESC_TOOL_PARAMETER, new EscapeTool());
			if ( date != null ) {
				th.putInContext(URL_SUFFIX_PARAMETER, "?" + RSSServlet.DATE_PARAMETER + "="+date.getTime());
			}
			if (! StringUtils.isEmpty(limit) ) {
				th.putInContext(PARAMETERS_PARAMETER, "limit:" + limit);
			}
			th.putInContext(CHANNEL_URL_PARAMETER, urlPreffix + RSSServlet.SERVLET_PATH);
			th.putInContext(CHANNELS_PARAMETER, channels);
			return th.processTemplate(template);
		} catch (Throwable e) {
			LOGGER.error( e.getMessage(), e);
		}		
		return null;
	}

	public Long getNumberOfNews() {
		return numberOfNews;
	}

	public void setNumberOfNews(Long numberOfNews) {
		this.numberOfNews = numberOfNews;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}	
	
}