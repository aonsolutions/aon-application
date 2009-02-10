package com.code.aon.ui.cms.velocity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.mail.Session;
import javax.mail.Transport;

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
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.FooterHandler;
import com.code.aon.ui.cms.velocity.attribute.HeaderHandler;
import com.code.aon.ui.cms.velocity.attribute.LanguageHandler;
import com.code.aon.ui.cms.velocity.attribute.SidebarOptionHandler;

public class CommonGenerator extends Generator {

	private Section previousSection = null;

    static private CommonGenerator singleton = null;

    private CommonGenerator() { 
    }

    static public CommonGenerator getCommonGenerator() {

        if (singleton == null) {
            singleton = new CommonGenerator();
        }
        return singleton;
    }

    public void init(VelocityUtil vu) {
    	this.previousSection = null;
    	
		ConfigDetail configDetail = ControllerUtil.getCurrentConfigDetail();
		
		// $default_css from config
		String css = "";
		if (configDetail != null) css = configDetail.getCss();
		vu.put("default_css", css);
		
		// $default_javascript from config
		String javascript = "";
		if (configDetail != null) javascript = configDetail.getJavascript();
		vu.put("default_javascript", javascript);

		// $default_description from config
		String description = "";
		if (configDetail != null) description = configDetail.getDescription();
		vu.put("default_description", description);
		
		// $default_keywords from config
		String keywords = "";
		if (configDetail != null) keywords = configDetail.getKeywords();
		vu.put("default_keywords", keywords);
		
        vu.put("every_languages", getActiveLanguages());
        vu.put("default_language", getDefaultLanguage());
        vu.put("current_language", getCurrentLanguage());

    }

	public void chargeContext(VelocityUtil vu, Section section) throws ManagerBeanException {
		if (section == null){
			section = GeneratorConfigController.defaultSection();
		}
		
		VelocityUtil.addMessage(". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . ." +
								" . . . . . . . . . . . . . . Cargando sección... ["+section.getAlias()+"]", VelocityUtil.INFO);
		
		if (previousSection==null ||
				section.getId().intValue()!=previousSection.getId().intValue()){
			
			
			// $default_menu from default sidebar menu in database
			vu.put("is_menu", section.isShow_menu());
			if (section.isShow_menu()){
				Menu m = section.getMenuToShow();
				if (m==null){
					vu.put("is_menu", false);
				}else{
					if (previousSection==null ||
							previousSection.getMenuToShow() == null ||
							!previousSection.getMenuToShow().getId().equals(m.getId())){
						vu.put("default_menu", MenuGenerator.getMenuOptionList(m));
					}
				}
			}

			vu.put("is_menu_alt", section.isShow_menu_alt());
			if (section.isShow_menu_alt()){
				Menu m = section.getMenuAltToShow();
				if (m==null){
					vu.put("is_menu_alt", false);
				}else{
					if (previousSection==null ||
							previousSection.getMenuAltToShow() == null ||
							!previousSection.getMenuAltToShow().getId().equals(m.getId())){
						vu.put("default_menu_alt", MenuGenerator.getMenuOptionList(m));
					}
				}
			}

			// $default_header from default header in database
			vu.put("is_header", section.isShow_header());
			if (section.isShow_header()){
				Header h = section.getHeaderToShow();
				if (h==null){
					vu.put("is_header", false);
				}else{
					if (previousSection==null ||
							previousSection.getHeaderToShow() == null ||
							!previousSection.getHeaderToShow().getId().equals(h.getId())){
						vu.put("default_header", getHeaderHandler(h));
					}
				}
			}
			
			// $default_sidebar from default header in database
			vu.put("is_sidebar_left", section.isShow_sidebar_left());
			vu.put("is_sidebar_right", section.isShow_sidebar_right());
			if (section.isShow_sidebar_left() ||
					section.isShow_sidebar_right()){
				if (section.isShow_sidebar_left()){
					Sidebar sb = section.getSidebarToLeftShow();
					if (sb==null){
						vu.put("is_sidebar_left", false);
					}else{
						if (previousSection==null ||
								previousSection.getSidebarToLeftShow() == null ||
								!previousSection.getSidebarToLeftShow().getId().equals(sb.getId())){
							vu.put("default_sidebar_left", getSidebarHandler(sb, SidebarSide.LEFT));
						}
					}
				}
				if (section.isShow_sidebar_right()){
					Sidebar sb = section.getSidebarToRightShow();
					if (sb==null){
						vu.put("is_sidebar_right", false);
					}else{
						if (previousSection==null ||
								previousSection.getSidebarToRightShow() == null ||
								!previousSection.getSidebarToRightShow().getId().equals(sb.getId())){
							vu.put("default_sidebar_right", getSidebarHandler(sb, SidebarSide.RIGHT));
						}
					}
				}
			}
			
			// $default_footer from default footer in database
			vu.put("is_footer", section.isShow_footer());
			if (section.isShow_footer()){
				Footer f = section.getFooterToShow();
				if (f==null){
					vu.put("is_footer", false);
				}else{
					if (previousSection==null ||
							previousSection.getFooterToShow() == null ||
							!previousSection.getFooterToShow().getId().equals(f.getId())){
						vu.put("default_footer", getFooterHandler(f));
					}
				}
			}
			
			previousSection = section;
			

			// $bundle from config
			try {
				ResourceBundle bundle = ResourceBundle.getBundle(Constants.MESSAGES_FILE, ControllerUtil.getCurrentLanguage().getLanguage().getLocale(), new TemplateBundleClassLoader());
		        vu.put("language", ControllerUtil.getCurrentLanguage().getLanguage().getLocale().getLanguage());
				vu.put("bundle", bundle);
			}
			catch (MissingResourceException mre) {
				VelocityUtil.addMessage(" - No se ha encontrado fichero de mensajes para el idioma actual.", VelocityUtil.WARN);
			}
		}
	}

	public void removeContext(VelocityUtil vu){
		vu.remove("is_menu");
		vu.remove("default_menu");
		vu.remove("is_menu_alt");
		vu.remove("default_menu_alt");
		vu.remove("is_header");
		vu.remove("default_header");
		vu.remove("is_sidebar_left");
		vu.remove("is_sidebar_right");
		vu.remove("default_sidebar_left");
		vu.remove("default_sidebar_right");
		vu.remove("is_footer");
		vu.remove("default_footer");
		vu.remove("default_css");
		vu.remove("default_javascript");
		vu.remove("default_description");
		vu.remove("default_keywords");
        vu.remove("language");
		vu.remove("bundle");
        vu.remove("every_languages");
        vu.remove("default_language");
        vu.remove("current_language");
		vu = null;
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		return null;
	}
	
	public void generateLanguagePage() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
        vu.put("every_languages", getActiveLanguages());
        vu.put("default_language", getDefaultLanguage());
        vu.put("current_language", getCurrentLanguage());

		File f = new File(ControllerUtil.getPreviewPath());
		if (!f.exists()) f.mkdirs();
		f = new File(ControllerUtil.getLanguagePreviewPath());
		if (!f.exists()) f.mkdirs();
		generate(vu, Templates.LANGUAGE);
		
		vu.finalize();
		vu = null;
	}

	public void generateEmailSendPage() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		File f = new File(ControllerUtil.getPreviewPath());
		if (!f.exists()) f.mkdirs();
		f = new File(ControllerUtil.getLanguagePreviewPath());
		if (!f.exists()) f.mkdirs();
		try {
			CommonGenerator.getCommonGenerator().chargeContext(vu, null);
		} catch (ManagerBeanException e) {
		}
		vu.put("smtpServer", ControllerUtil.getCurrentConfig().getSmtp_server());
		vu.put("username", ControllerUtil.getCurrentConfig().getSmtp_user());
		vu.put("password", ControllerUtil.getCurrentConfig().getSmtp_password());
		vu.put("from", ControllerUtil.getCurrentConfig().getFrom_email());
		vu.put("name_from", ControllerUtil.getCurrentConfig().getFrom_name());
		generate(vu, Templates.SENDMAIL);
		vu.remove("smtpServer");
		vu.remove("username");
		vu.remove("password");
		vu.remove("from");
		vu.remove("name_from");
		vu.finalize();
		vu = null;
	}

	public void generateSearchPage() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();
		File f = new File(ControllerUtil.getPreviewPath());
		if (!f.exists()) f.mkdirs();
		f = new File(ControllerUtil.getLanguagePreviewPath());
		if (!f.exists()) f.mkdirs();
		try {
			CommonGenerator.getCommonGenerator().chargeContext(vu, null);
		} catch (ManagerBeanException e) {
		}
		generate(vu, Templates.SEARCH);
		vu.finalize();
		vu = null;
	}

	public void generateCaptchaPage() {
		VelocityUtil vu = new VelocityUtil();
		CommonGenerator.getCommonGenerator().init(vu);
		vu.setTemplate_path(ControllerUtil.getCurrentVmTemplatePath());
		vu.initialize();

		File f = new File(ControllerUtil.getPreviewPath());
		if (!f.exists()) f.mkdirs();
		f = new File(ControllerUtil.getLanguagePreviewPath());
		if (!f.exists()) f.mkdirs();
		generate(vu, Templates.CAPTCHA);
		
		vu.finalize();
		vu = null;
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

	private static Object getCurrentLanguage() {
		Language lang = ControllerUtil.getCurrentLanguage();
		return lang.getLanguage().getLocale().getLanguage();
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
			e.printStackTrace();
		}
		return list;
	}

	private class TemplateBundleClassLoader extends ClassLoader {

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
