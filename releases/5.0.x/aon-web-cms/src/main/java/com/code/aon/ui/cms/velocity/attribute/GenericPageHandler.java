package com.code.aon.ui.cms.velocity.attribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.code.aon.cms.GenericPageDetail;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class GenericPageHandler {

	private String title;
	
	private String content;

	private Date date;
	
	private List<MenuOptionHandler> menu;

	private String description;
	
	private String keywords;
	
	private SimpleDateFormat formatter;

	public GenericPageHandler (GenericPageDetail gpd) {
		title = gpd.getTitle();
		content = gpd.getContent();
		description = gpd.getDescription();
		keywords = gpd.getKeywords();
		date = gpd.getGeneric_page().getCreate_date();
		if (gpd.getGeneric_page().getMenu() > 0) {
			String message = "LA PAGINA GENERICA " + gpd.getGeneric_page().getAlias();
			menu = MenuGenerator.getMenuOptionList(gpd.getGeneric_page().getMenu(), message);
		}
		
		Locale locale = ControllerUtil.getCurrentLanguage().getLanguage().getLocale();
		if ("eu".equals(locale.getLanguage()))
			formatter = new SimpleDateFormat ("dd/MM/yy");
		else
			formatter = (SimpleDateFormat)SimpleDateFormat.getDateInstance(DateFormat.SHORT,locale);
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getDate() {
        return formatter.format(date);
	}

	public List<MenuOptionHandler> getMenu() {
		return menu;
	}

	public String getDescription() {
		return description;
	}

	public String getKeywords() {
		return keywords;
	}
	
}
