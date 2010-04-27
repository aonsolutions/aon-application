package com.code.aon.ui.cms.velocity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.ConfigDetail;
import com.code.aon.cms.Footer;
import com.code.aon.cms.FooterDetail;
import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.Language;
import com.code.aon.cms.Menu;
import com.code.aon.cms.Section;
import com.code.aon.cms.Sidebar;
import com.code.aon.cms.SidebarOption;
import com.code.aon.cms.SidebarOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.IGeneratorLogger;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.FooterHandler;
import com.code.aon.ui.cms.velocity.attribute.HeaderHandler;
import com.code.aon.ui.cms.velocity.attribute.LanguageHandler;
import com.code.aon.ui.cms.velocity.attribute.SidebarOptionHandler;

public class GeneratorContext implements IVelocityConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(GeneratorContext.class);
	
	private Section defaultSection;
	
	private Map<Section, Map<String,Object>> sectionContexts;
	
	private VelocityContext defaultContext;
	
	private VelocityUtil velocityUtil;
	
	private IGeneratorLogger logger;
	
	public GeneratorContext() {
		logger = CommonGenerator.getLogger();
		try {
			defaultSection = GeneratorConfigController.defaultSection();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}			
		velocityUtil = new VelocityUtil();
		velocityUtil.setLogger( logger );
		velocityUtil.setTemplatePath(ControllerUtil.getCurrentVmTemplatePath());
		velocityUtil.initialize();		
		defaultContext = getDefaultContext(); 
		sectionContexts = new HashMap<Section, Map<String,Object>>();
	}
	
	public IGeneratorLogger getLogger() {
		return logger;
	}

	private ArrayList<LanguageHandler> getActiveLanguages() {
		ArrayList<LanguageHandler> list = new ArrayList<LanguageHandler>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Language.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(bean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i = 0; i < l.size(); i++) {
				Language lang = (Language)l.get(i);
				LanguageHandler lh = new LanguageHandler(lang);
				list.add(lh);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return list;
	}

	private Object getDefaultLanguage() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Language.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LANGUAGE_DEFAULT_LANGUAGE), true);
			criteria.addOrder(bean.getFieldName(ICMSAlias.LANGUAGE_POSITION));
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.size() > 0) {
				Language lang = (Language)l.get(0);
				return lang.getLanguage().getLocale().getLanguage();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private Object getCurrentLanguage() {
		Language lang = ControllerUtil.getCurrentLanguage();
		return lang.getLanguage().getLocale().getLanguage();
	}
	
	private VelocityContext getDefaultContext() {
		VelocityContext vc = new VelocityContext();
		ConfigDetail configDetail = ControllerUtil.getCurrentConfigDetail();
		String css = null, javascript = null, description = null, keywords = null;
		if ( configDetail != null ) {
			css = configDetail.getCss();
			javascript = configDetail.getJavascript();
			description = configDetail.getDescription();
			keywords = configDetail.getKeywords();
		}
		vc.put(DEFAULT_CSS_KEY, StringUtils.defaultString(css) );
		vc.put(DEFAULT_JAVASCRIPT_KEY, StringUtils.defaultString(javascript) );
		vc.put(DEFAULT_DESCRIPTION_KEY, StringUtils.defaultString(description) );
		vc.put(DEFAULT_KEYWORDS_KEY, StringUtils.defaultString(keywords) );
		
        vc.put(EVERY_LANGUAGES_KEY, getActiveLanguages());
        vc.put(DEFAULT_LANGUAGE_KEY, getDefaultLanguage());
        vc.put(CURRENT_LANGUAGE_KEY, getCurrentLanguage());	
        
        return vc;
	}
	
	public VelocityUtil initVelocityUtil() {
		VelocityContext newContext = new VelocityContext(this.defaultContext);
		velocityUtil.setContext(newContext);
		return velocityUtil;
	}

	public void changeDefaultSection(VelocityUtil vu) {
		changeSection(vu, this.defaultSection);
	}
	
	public void changeSection(VelocityUtil vu, Section section) {
		Section newSection = ( section != null ) ? section : this.defaultSection;
		Map<String, Object> context = sectionContexts.get(newSection);
		if ( context == null ) {
			logger.info(". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . ." +
					" Cargando sección... ["+newSection.getAlias()+"]");
			context = new HashMap<String, Object>();
			addSectionContext( newSection, context );
			sectionContexts.put(newSection, context);
		}
		for( Map.Entry<String, Object> entry : context.entrySet() ) {
			vu.put( entry.getKey(), entry.getValue() );
		}
	}
	
	private HeaderHandler getHeaderHandler(Header h) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(HeaderDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.HEADER_DETAIL_HEADER_ID), h.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.HEADER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.size() > 0) {
				HeaderDetail hd = (HeaderDetail)l.get(0);
				HeaderHandler hh = new HeaderHandler(hd);
				return hh;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	private ArrayList<SidebarOptionHandler> getSidebarHandler(Sidebar s, SidebarSide sidebarSide) {
		ArrayList<SidebarOptionHandler> list = new ArrayList<SidebarOptionHandler>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(SidebarOption.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDEBAR_ID), s.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_ACTIVE), true);
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_SIDE), sidebarSide);
			criteria.addOrder(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_POSITION));
			List<ITransferObject> sidebarOption_lst = (List<ITransferObject>)bean.getList(criteria);
			Iterator<ITransferObject> sidebarOption_iter = sidebarOption_lst.iterator();
			while (sidebarOption_iter.hasNext()) {
				SidebarOption sidebarOption = (SidebarOption)sidebarOption_iter.next(); 
				if ( (sidebarOption.getIdent() == null) && (sidebarOption.getType() != SidebarType.DIARY_CALENDAR) ) {
					getLogger().error("OPCION " + sidebarOption.getAlias() + " DE BARRA LATERAL "+ s.getAlias() + " NO REFERENCIA A NINGUN ELEMENTO");
					continue;
				}
				bean = BeanManager.getManagerBean(SidebarOptionDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_DETAIL_SIDEBAR_OPTION_ID), sidebarOption.getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> sidebarOptionDetail_lst = (List<ITransferObject>)bean.getList(criteria);
				Iterator<ITransferObject> sidebarOptionDetail_iter = sidebarOptionDetail_lst.iterator();
				while (sidebarOptionDetail_iter.hasNext()) {
					SidebarOptionDetail current = (SidebarOptionDetail)sidebarOptionDetail_iter.next();
					SidebarOptionHandler current_h = new SidebarOptionHandler(sidebarOption);
					list.add(current_h);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return list;
	}
	
	private FooterHandler getFooterHandler(Footer f) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(FooterDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FOOTER_DETAIL_FOOTER_ID), f.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FOOTER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			if (l.size() > 0) {
				FooterDetail fd = (FooterDetail)l.get(0);
				FooterHandler fh = new FooterHandler(fd);
				return fh;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}	
		
	private void addSectionContext(Section section, Map<String,Object> vc) {		
		// $default_menu from default sidebar menu in database
		vc.put(IS_MENU_KEY, section.isShow_menu());
		if (section.isShow_menu()){
			Menu m = section.getMenuToShow();
			if ( m==null ) {
				vc.put(IS_MENU_KEY, false);
			} else {
				vc.put(DEFAULT_MENU_KEY, MenuGenerator.getMenuOptionList(m));
			}
		}

		vc.put(IS_MENU_ALT_KEY, section.isShow_menu_alt());
		if (section.isShow_menu_alt()){
			Menu m = section.getMenuAltToShow();
			if (m==null){
				vc.put(IS_MENU_ALT_KEY, false);
			} else {
				vc.put(DEFAULT_MENU_ALT_KEY, MenuGenerator.getMenuOptionList(m));
			}
		}

		// $default_header from default header in database
		vc.put(IS_HEADER_KEY, section.isShow_header());
		if (section.isShow_header()){
			Header h = section.getHeaderToShow();
			if (h==null) {
				vc.put(IS_HEADER_KEY, false);
			} else {
				vc.put(DEFAULT_HEADER_KEY, getHeaderHandler(h));
			}
		}
		
		// $default_sidebar from default header in database
		vc.put(IS_SIDEBAR_LEFT_KEY, section.isShow_sidebar_left());
		vc.put(IS_SIDEBAR_RIGHT_KEY, section.isShow_sidebar_right());
		if (section.isShow_sidebar_left() || section.isShow_sidebar_right()) {
			if (section.isShow_sidebar_left()) {
				Sidebar sb = section.getSidebarToLeftShow();
				if (sb==null) {
					vc.put(IS_SIDEBAR_LEFT_KEY, false);
				} else {
					vc.put(DEFAULT_SIDEBAR_LEFT_KEY, getSidebarHandler(sb, SidebarSide.LEFT));
				}
			}
			if (section.isShow_sidebar_right()) {
				Sidebar sb = section.getSidebarToRightShow();
				if (sb==null) {
					vc.put(IS_SIDEBAR_RIGHT_KEY, false);
				} else {
					vc.put(DEFAULT_SIDEBAR_RIGHT_KEY, getSidebarHandler(sb, SidebarSide.RIGHT));
				}
			}
		}
		
		// $default_footer from default footer in database
		vc.put(IS_FOOTER_KEY, section.isShow_footer());
		if (section.isShow_footer()){
			Footer f = section.getFooterToShow();
			if (f==null) {
				vc.put(IS_FOOTER_KEY, false);
			} else {
				vc.put(DEFAULT_FOOTER_KEY, getFooterHandler(f));
			}
		}
		
		// $bundle from config
		Language currentLanguage = ControllerUtil.getCurrentLanguage();
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(Constants.MESSAGES_FILE, ControllerUtil.getCurrentLanguage().getLanguage().getLocale(), new TemplateBundleClassLoader());
	        vc.put(LANGUAGE_KEY, currentLanguage.getLanguage().getLocale().getLanguage());
			vc.put(BUNDLE_KEY, bundle);
		} catch (MissingResourceException mre) { 
			logger.warning("No se ha encontrado fichero de mensajes para el idioma actual (" + currentLanguage.getDescription() + ")");
		}
	}

	private class TemplateBundleClassLoader extends ClassLoader {

		@Override
		protected URL findResource(String name) {
			String base = FilenameUtils.getName(name);
			File bundleFile = new File( ControllerUtil.getBundlePath(), base);
			try {
				return new URL("file", null, bundleFile.getAbsolutePath()) ;
			} catch (MalformedURLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			return super.findResource(name);
		}

	}
	
}
 