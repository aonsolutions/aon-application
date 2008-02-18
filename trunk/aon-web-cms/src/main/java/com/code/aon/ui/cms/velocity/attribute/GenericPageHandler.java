package com.code.aon.ui.cms.velocity.attribute;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.code.aon.cms.GenericPageDetail;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class GenericPageHandler {

	private String title;
	
	private String content;

	private Date date;
	
	private ArrayList<MenuOptionHandler> menu;

	private String description;
	
	private String keywords;

	public GenericPageHandler (GenericPageDetail gpd) {
		title = gpd.getTitle();
		content = gpd.getContent();
		description = gpd.getDescription();
		keywords = gpd.getKeywords();
		date = gpd.getGeneric_page().getCreate_date();
		menu = null;
		if (gpd.getGeneric_page().getMenu() > 0) menu = MenuGenerator.getMenuOptionList(gpd.getGeneric_page().getMenu());
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
	}

	public ArrayList<MenuOptionHandler> getMenu() {
		return menu;
	}

	public String getDescription() {
		return description;
	}

	public String getKeywords() {
		return keywords;
	}
	
}
