package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.MenuOption;
import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;

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
