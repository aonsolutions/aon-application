package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import com.code.aon.cms.Header;
import com.code.aon.cms.HeaderDetail;
import com.code.aon.cms.enumeration.LanguageMenuType;
import com.code.aon.ui.cms.velocity.BannerGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class HeaderHandler {

	private String css;
	
	private String javascript;

	private boolean language;

	private LanguageMenuType languageType;
	
	private List<MenuOptionHandler> menu;

	private BannerCategoryHandler bannerCategory;

	private String sitename;
	
	private String image;
	
	private String content;
	
	public HeaderHandler(HeaderDetail headerDetail) {
		Header header = headerDetail.getHeader();
		css = header.getCss();
		javascript = header.getJavascript();
		language = header.isLanguage_menu();
		languageType = header.getLanguage_menu_type();
		menu = MenuGenerator.getMenuOptionList(header.getMenu());
		if (header.getBannerCategory()!=null) {
			bannerCategory = BannerGenerator.getBannerCategoryHandler(header.getBannerCategory());
		}
		sitename = headerDetail.getSitename();
		image = headerDetail.getImage();
		content = headerDetail.getContent();
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

	public List<MenuOptionHandler> getMenu() {
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

	public BannerCategoryHandler getBannerCategory() {
		return bannerCategory;
	}

}
