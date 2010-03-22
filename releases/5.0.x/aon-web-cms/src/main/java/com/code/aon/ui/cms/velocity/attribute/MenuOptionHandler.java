package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.MenuOptionDetail;
import com.code.aon.cms.enumeration.ContentLevel;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.ui.cms.util.MenuOptionUtil;

public class MenuOptionHandler {

	private boolean separator;
	
	private String label;
	
	private boolean new_;
	
	private String url;

	public MenuOptionHandler (MenuOptionDetail mod) {
		this.label = mod.getLabel();
		this.separator = mod.getMenu_option().isSeparator();
		Integer ident = mod.getMenu_option().getIdent();
		PageType pageType = mod.getMenu_option().getType();
		ContentLevel level = mod.getMenu_option().getLevel();
		this.url = mod.getUrl();
		this.url = MenuOptionUtil.getMenuOptionLink(ident, pageType, level, url);
		this.new_ = MenuOptionUtil.isNewWindow(pageType, level);
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

	public boolean isNew_() {
		return new_;
	}
	
}
