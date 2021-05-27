package com.code.aon.ui.marketing.controller;

import static com.code.aon.marketing.enumeration.NewsletterLayout.FULL_WIDTH_IMAGE;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.News;
import com.code.aon.marketing.Template;
import com.code.aon.marketing.enumeration.NewsType;
import com.code.aon.marketing.enumeration.NewsletterLayout;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class HtmlGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlGenerator.class.getName());

	private static final String TABLE_COLUMN_START = "<tr><td>";

	private static final String TABLE_COLUMN_END = "</td></tr>";
	
	private StringBuffer sb;
	
	private String urlPreffix;

	public HtmlGenerator(String urlPreffix, StringBuffer sb) {
		this.urlPreffix = urlPreffix;
		this.sb = sb;
	}
	
	public HtmlGenerator(String urlPreffix) {
		this( urlPreffix, new StringBuffer() );
	}

	public HtmlGenerator(StringBuffer sb) {
		this( getURLPreffix(), sb );
	}
	
	public HtmlGenerator() {
		this( getURLPreffix() );
	}

	public void addSeparator( Template template ) {
		sb.append("<hr");
		if ( template!=null && !StringUtils.isEmpty(template.getTitleColor()) ) {
			sb.append(" style=\"color:");
			sb.append(template.getTitleColor());
			sb.append(";background-color:");
			sb.append(template.getTitleColor());
			sb.append(";\"");
		}
		sb.append(" size=\"5\"/>");		
	}
	
	private void addTitle( News news, Template template ) {
		sb.append("<p align=\"left\" style=\"font-size:18px;font-weight:bold");
		if ( template!=null && !StringUtils.isEmpty(template.getTitleColor()) ) {
			sb.append(";color:");
			sb.append(template.getTitleColor());
		}
		sb.append(";\">");
		sb.append(news.getTitle());
		sb.append("</p>");		
	}

	private void addImage( String url ) {
		sb.append("<img border=\"0\" styles=\"display:block;\" src=\"");
		sb.append( url );
		sb.append("\">");		
	}

	private void addContent( News news ) {
		sb.append("<div align=\"left\">");
		sb.append(news.getContent());
		sb.append("</div>");		
	}
	
	private String getImageURL( RegistryAttachment ra ) {
		if ( ra!=null && ra.getId()!=null ) {
			return urlPreffix + ra.getDownloadURL();
		}
		return null;
	}
	
	private void addFullWidthImage( News news, Template template ) {
		addTitle(news, template);
		sb.append(TABLE_COLUMN_END);
		String url = getImageURL(news.getRegistryAttachment());
		if ( url != null ) {
			sb.append(TABLE_COLUMN_START);
			addImage(url);
			sb.append(TABLE_COLUMN_END);					
		}
		sb.append(TABLE_COLUMN_START);
		addContent(news);
	}

	private void addLeftAlignedImage( News news, Template template ) {
		addTitle(news, template);
		String url = getImageURL(news.getRegistryAttachment());
		if ( url != null ) {
			sb.append("<table  align=\"left\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>" +			
					TABLE_COLUMN_START);
			addImage(url);
			sb.append("</td><td width=\"15\"/></tr>" +
					"<tr><td width=\"15\" height=\"10\"></tr>" +
					"</tbody></table>");
		}
		addContent(news);
	}

	private void addRightAlignedImage( News news, Template template ) {
		addTitle(news, template);
		String url = getImageURL(news.getRegistryAttachment());
		if ( url != null ) {
			sb.append("<table  align=\"right\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody>"
						+ "<tr><td width=\"15\"/><td>");
			addImage(url);
			sb.append(TABLE_COLUMN_END +
					"<tr><td width=\"15\" height=\"10\"></tr>" +
					"</tbody></table>");
		}
		addContent(news);
	}
	
	public void addNews( Template template, News news, NewsletterLayout layout, boolean addBottomPadding ) {
		StringBuffer currentContent = this.sb;
		this.sb = new StringBuffer();
		switch ( layout ) {
			case FULL_WIDTH_IMAGE:
				addFullWidthImage(news, template);
				break;
			case LEFT_ALIGNED_IMAGE:
				addLeftAlignedImage(news, template);
				break;
			case RIGHT_ALIGNED_IMAGE:
				addRightAlignedImage(news, template);
				break;
			case ALTERNATE_ALIGNED_IMAGE:
				break;
		}
		String content = this.sb.toString();
		this.sb = currentContent;
		addContent(template, content, addBottomPadding);
	}

	public void addNews( News news ) {
		if (news.getType() == NewsType.MESSAGE) {
			addContent(news.getTemplate(), news.getContent(), false);
		} else {
			addNews(news.getTemplate(), news, FULL_WIDTH_IMAGE, false);
		}
	}
	
	private void addContent( Template template, String content, boolean addBottomPadding ) {
		sb.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0");
		if ( template != null && !StringUtils.isEmpty(template.getWidth()) ) {
			sb.append("\" style=\"width: ");
			sb.append(template.getWidth());			
		}
		sb.append("\"><tbody><tr><td>");		
		sb.append( content );
		sb.append(TABLE_COLUMN_END);		
		if ( addBottomPadding ) {
			sb.append("<tr><td height=\"10\"></td></tr>");	
		}
		sb.append("</tbody></table>");
	}
	
	public static String getURLPreffix() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		try {
			return ds.getDomainURL();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return null;
	}
	
	public String getString() {
		return sb.toString();
	}
	
}
