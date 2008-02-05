package com.code.aon.ui.cms.velocity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

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
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.MenuType;
import com.code.aon.cms.enumeration.SidebarSide;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.FooterHandler;
import com.code.aon.ui.cms.velocity.attribute.HeaderHandler;
import com.code.aon.ui.cms.velocity.attribute.LanguageHandler;
import com.code.aon.ui.cms.velocity.attribute.SidebarOptionHandler;

public class CommonGenerator extends Generator {

	private Section previousSection = null;
	
    static private CommonGenerator singleton = null;

    private CommonGenerator() { }

    static public CommonGenerator getCommonGenerator() {

        if (singleton == null) {
            singleton = new CommonGenerator();
        }
        return singleton;
    }
	
	public void chargeContext(VelocityUtil vu, Section section) {
		
		if (previousSection==null ||
				section.getId().intValue()!=previousSection.getId().intValue()){
			
			previousSection = section; 
			
			// $default_menu from default sidebar menu in database
			vu.put("is_menu", section.isShow_menu());
			if (section.isShow_menu()){
				vu.addMessage(" - Menu por defecto", VelocityUtil.INFO);
				Menu m = section.getMenu();
				vu.put("default_menu", MenuGenerator.getMenuOptionList(m));
				vu.addMessage(" - Menu " + m.getAlias() + " cargado.", VelocityUtil.INFO);
			}

			vu.put("is_menu_alt", section.isShow_menu_alt());
			if (section.isShow_menu()){
				vu.addMessage(" - Menu alternativo", VelocityUtil.INFO);
				Menu m = section.getMenu_alt();
				vu.put("default_menu_alt", MenuGenerator.getMenuOptionList(m));
				vu.addMessage(" - Menu alt " + m.getAlias() + " cargado.", VelocityUtil.INFO);
			}

			// $default_header from default header in database
			vu.put("is_header", section.isShow_header());
			if (section.isShow_header()){
				vu.addMessage(" - Cabecera por defecto", VelocityUtil.INFO);
				Header h = section.getHeader();
				vu.put("default_header", getHeaderHandler(h));
				vu.addMessage(" - Cabecera " + h.getAlias() + " cargada.", VelocityUtil.INFO);
			}
			
			// $default_sidebar from default header in database
			vu.put("is_sidebar_left", section.isShow_sidebar_left());
			vu.put("is_sidebar_right", section.isShow_sidebar_right());
			if (section.isShow_sidebar_left() ||
					section.isShow_sidebar_right()){
				vu.addMessage(" - Sidebar por defecto", VelocityUtil.INFO);
				Sidebar sb = section.getSidebar();
				if (section.isShow_sidebar_left()){
					vu.put("default_sidebar_left", getSidebarHandler(sb, SidebarSide.LEFT));
					vu.addMessage(" - Sidebar left " + sb.getAlias() + " cargada.", VelocityUtil.INFO);
				}
				if (section.isShow_sidebar_right()){
					vu.put("default_sidebar_right", getSidebarHandler(sb, SidebarSide.RIGHT));
					vu.addMessage(" - Sidebar right " + sb.getAlias() + " cargada.", VelocityUtil.INFO);
				}
			}
			
			// $default_footer from default footer in database
			vu.put("is_footer", section.isShow_footer());
			if (section.isShow_footer()){
				vu.addMessage(" - Pie de página por defecto", VelocityUtil.INFO);
				Footer f = section.getFooter();
				vu.put("default_footer", getFooterHandler(f));
				vu.addMessage(" - Pie de página " + f.getAlias() + " cargado.", VelocityUtil.INFO);
			}
			
			// $default_css from config
			vu.addMessage(" - CSS de página por defecto", VelocityUtil.INFO);
			String css = "";
			if (ControllerUtil.getCurrentConfigDetail() != null) css = ControllerUtil.getCurrentConfigDetail().getCss();
			vu.put("default_css", css);
			
			// $default_javascript from config
			vu.addMessage(" - JavaScript de página por defecto", VelocityUtil.INFO);
			String javascript = "";
			if (ControllerUtil.getCurrentConfigDetail() != null) javascript = ControllerUtil.getCurrentConfigDetail().getJavascript();
			vu.put("default_javascript", javascript);

			// $default_description from config
			vu.addMessage(" - Descripcion de página por defecto", VelocityUtil.INFO);
			String description = "";
			if (ControllerUtil.getCurrentConfigDetail() != null) description = ControllerUtil.getCurrentConfigDetail().getDescription();
			vu.put("default_description", description);
			
			// $default_keywords from config
			vu.addMessage(" - KeyWords de página por defecto", VelocityUtil.INFO);
			String keywords = "";
			if (ControllerUtil.getCurrentConfigDetail() != null) keywords = ControllerUtil.getCurrentConfigDetail().getKeywords();
			vu.put("default_keywords", keywords);

			// $bundle from config
			vu.addMessage(" - Bundle de página", VelocityUtil.INFO);
			try {
				ResourceBundle bundle = ResourceBundle.getBundle(Constants.MESSAGES_FILE, ControllerUtil.getCurrentLanguage().getLanguage().getLocale(), new TemplateBundleClassLoader());
		        vu.put("language", ControllerUtil.getCurrentLanguage().getLanguage().getLocale().getLanguage());
				vu.put("bundle", bundle);
			}
			catch (MissingResourceException mre) {
				mre.printStackTrace();
				vu.addMessage(" - No se ha encontrado fichero de mensajes para el idioma actual.", VelocityUtil.WARN);
			}
		}
	}

	private static HeaderHandler getHeaderHandler(Header h) {
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
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<SidebarOptionHandler> getSidebarHandler(Sidebar s, SidebarSide sidebarSide) {
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
				
				bean = BeanManager.getManagerBean(SidebarOptionDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_DETAIL_SIDEBAR_OPTION_ID), sidebarOption.getId());
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SIDEBAR_OPTION_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> sidebarOptionDetail_lst = (List<ITransferObject>)bean.getList(criteria);
				Iterator<ITransferObject> sidebarOptionDetail_iter = sidebarOptionDetail_lst.iterator();
				while (sidebarOptionDetail_iter.hasNext()) {
					SidebarOptionDetail current = (SidebarOptionDetail)sidebarOptionDetail_iter.next();
					SidebarOptionHandler current_h = new SidebarOptionHandler(current);
					list.add(current_h);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return list;
	}

	private static FooterHandler getFooterHandler(Footer f) {
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
			e.printStackTrace();
		}
		return null;
	}
	
	public static void generateLanguagePage(VelocityUtil vu) {
        vu.put("every_languages", getActiveLanguages());
        vu.put("default_language", getDefaultLanguage());

		File f = new File(ControllerUtil.getPreviewPath());
		if (!f.exists()) f.mkdirs();
		f = new File(ControllerUtil.getLanguagePreviewPath());
		if (!f.exists()) f.mkdirs();
		generate(vu, Templates.LANGUAGE);
	}

	private static Object getDefaultLanguage() {
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
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<LanguageHandler> getActiveLanguages() {
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
			e.printStackTrace();
		}
		return list;
	}

	private static class TemplateBundleClassLoader extends ClassLoader {

		@Override
		protected URL findResource(String name) {
			File f = new File(name);
			String bundle_file = ControllerUtil.getBundlePath() + "/" + f.getName();
			f = new File(bundle_file);
			try {
				return new URL("file", null, f.getAbsolutePath()) ;
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return super.findResource(name);
		}

	}
}
