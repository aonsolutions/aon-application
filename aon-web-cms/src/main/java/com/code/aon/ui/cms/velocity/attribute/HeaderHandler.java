package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.enumeration.LanguageMenuType;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class HeaderHandler {

	private String css;
	
	private String javascript;

	private boolean language;

	private LanguageMenuType languageType;
	
	private ArrayList<MenuOptionHandler> menu;

	private String sitename;
	
	private String image;
	
	private String content;
	
	public HeaderHandler(HeaderDetail header) {
		css = header.getHeader().getCss();
		javascript = header.getHeader().getJavascript();
		language = header.getHeader().isLanguage_menu();
		languageType = header.getHeader().getLanguage_menu_type();
		menu = MenuGenerator.getMenuOptionList(header.getHeader().getMenu());
		sitename = header.getSitename();
		image = header.getImage();
		content = header.getContent();
	}

	public String getCss() {
		return css;
	}

	public String getJavascript() {
		return javascript;
	}

	public boolean isLanguage() {
		return language;
	}

	public boolean isLanguageList() {
		return languageType == LanguageMenuType.LIST;
	}

	public boolean isLanguageDrop() {
		return languageType == LanguageMenuType.DROP;
	}

	public boolean isLanguageFlags() {
		return languageType == LanguageMenuType.FLAGS;
	}

	public ArrayList<MenuOptionHandler> getMenu() {
		return menu;
	}

	public String getSitename() {
		return sitename;
	}

	public String getImage() {
		return image;
	}

	public String getContent() {
		return content;
	}

	public LanguageMenuType getLanguageType() {
		return languageType;
	}


}
