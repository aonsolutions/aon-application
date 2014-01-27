package com.code.aon.ui.marketing.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.Category;
import com.code.aon.registry.enumeration.CategoryType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.marketing.servlet.RSSServlet;
import com.code.aon.ui.util.AonUtil;

public class ChannelController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChannelController.class.getName());
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Category category = (Category) getTo();
		category.setType(CategoryType.ARTICLE);		
	}
	
	public String getDownloadURL() {
		Category category = (Category) getTo();
		return getURL(category);
	}

	public String getCurrentDownloadURL() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Category category = (Category) getModel().getRowData();
			return getURL(category);			
		}
		return null;
	}
	
	public static String getURL( Category category ) {
		String url = null;
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		try {
			url = ds.getDomainURL() + RSSServlet.SERVLET_PATH + getRSSFileName(category);
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
		return url;
	}
	
	public static String getRSSFileName( Category category ) {
		StringBuffer url = new StringBuffer();
		url.append(RSSController.RSS_PREFFIX);
		if ( category!=null && category.getId()!=null ) {
			url.append('-').append(category.getId());
		}
		url.append('.').append(MimeType.MIME_XML.getExtension());
		return url.toString();
	}
	
}
