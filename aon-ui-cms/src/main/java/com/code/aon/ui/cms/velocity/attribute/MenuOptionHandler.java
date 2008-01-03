package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.Link;
import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.ModularPage;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.FaqGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;

public class MenuOptionHandler {

	private boolean separator;
	
	private String label;
	
	private String url;

	public MenuOptionHandler (MenuOptionDetail mod) {
		label = mod.getLabel();
		separator = mod.getMenu_option().isSeparator();
		url = getMenuLink(mod);
	}
	
	private String getMenuLink(MenuOptionDetail mod) {
		Integer ident = mod.getMenu_option().getIdent();
		if (mod.getMenu_option().getType() == PageType.EXTERNAL) {
			if (mod.getUrl() != null && !mod.getUrl().trim().equals("")) return mod.getUrl();
			return null;
		}
		if (mod.getMenu_option().getType() == PageType.GENERIC) {
			try {
				IManagerBean genericBean = BeanManager.getManagerBean(GenericPageDetail.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(genericBean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_GENERIC_PAGE_ID), ident);
				criteria_detail.addEqualExpression(genericBean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
				List<ITransferObject> ld = (List<ITransferObject>)genericBean.getList(criteria_detail);
				if (ld.size() > 0) {
					GenericPageDetail gpd = (GenericPageDetail)ld.get(0);
					String link = Templates.GENERIC.getHtmlName();
					link = link.replaceAll("%NAME%", gpd.getGeneric_page().getAlias());
					return link;
				}
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			return null;
		}
		if (mod.getMenu_option().getType() == PageType.MENU) {
			try {
				IManagerBean moBean = BeanManager.getManagerBean(MenuOption.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_MENU_ID), ident);
				criteria_detail.addEqualExpression(moBean.getFieldName(ICMSAlias.MENU_OPTION_ACTIVE), true);
				List<ITransferObject> ld = (List<ITransferObject>)moBean.getList(criteria_detail);
				if (ld.size() > 0) {
					MenuOption mo = (MenuOption)ld.get(0);
					String link = Templates.MENU.getHtmlName();
					link = link.replaceAll("%NAME%", mo.getMenu().getAlias());
					return link;
				}
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			return null;
		}
		if (mod.getMenu_option().getType() == PageType.LINK) {
			try {
				if (mod.getMenu_option().getLevel().equals(ContentLevel.SECTION)){
					String linkCategory = Templates.LINK.getHtmlName();
					linkCategory = linkCategory.replaceAll("%NAME%", LinkGenerator.LINK_CATEGORY_LIST_PAGE);
					return linkCategory;
				}else if (mod.getMenu_option().getLevel().equals(ContentLevel.CATEGORY)){
					IManagerBean bean = BeanManager.getManagerBean(LinkCategory.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ID), ident);
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.LINK_CATEGORY_ACTIVE), true);
					List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
					if (l.size() > 0) {
						LinkCategory lc = (LinkCategory)l.get(0);
						String link = Templates.LINK.getHtmlName();
						link = link.replaceAll("%NAME%", lc.getAlias());
						return link;
					}
				}
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			return null;
		}
		if (mod.getMenu_option().getType() == PageType.FAQ) {
			try {
				if (mod.getMenu_option().getLevel().equals(ContentLevel.SECTION)){
					String faqCategory = Templates.FAQ.getHtmlName();
					faqCategory = faqCategory.replaceAll("%NAME%", FaqGenerator.FAQ_CATEGORY_LIST_PAGE);
					return faqCategory;
				}else if (mod.getMenu_option().getLevel().equals(ContentLevel.CATEGORY)){
					IManagerBean bean = BeanManager.getManagerBean(FaqCategory.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_ID), ident);
					criteria.addEqualExpression(bean.getFieldName(ICMSAlias.FAQ_CATEGORY_ACTIVE), true);
					List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
					if (l.size() > 0) {
						FaqCategory fc = (FaqCategory)l.get(0);
						String faq = Templates.FAQ.getHtmlName();
						faq = faq.replaceAll("%NAME%", fc.getAlias());
						return faq;
					}
				}
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			return null;
		}
		if (mod.getMenu_option().getType() == PageType.MODULAR) {
			try {
				IManagerBean modularBean = BeanManager.getManagerBean(ModularPage.class);
				Criteria criteria_detail = new Criteria();
				criteria_detail.addEqualExpression(modularBean.getFieldName(ICMSAlias.MODULAR_PAGE_ID), ident);
				List<ITransferObject> ld = (List<ITransferObject>)modularBean.getList(criteria_detail);
				if (ld.size() > 0) {
					ModularPage mp = (ModularPage)ld.get(0);
					String link = Templates.MODULAR.getHtmlName();
					link = link.replaceAll("%NAME%", mp.getAlias());
					return link;
				}
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}
	
	public boolean isSeparator() {
		return separator;
	}

	public String getLabel() {
		return label;
	}

	public String getUrl() {
		return url;
	}
	
}
