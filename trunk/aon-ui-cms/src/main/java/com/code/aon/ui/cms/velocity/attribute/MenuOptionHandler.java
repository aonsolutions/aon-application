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
import com.code.aon.ui.cms.util.MenuOptionUtil;
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
		PageType pageType = mod.getMenu_option().getType();
		ContentLevel level = mod.getMenu_option().getLevel();
		String url = mod.getUrl();
		return MenuOptionUtil.getMenuOptionLink(ident, pageType, level, url);
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
