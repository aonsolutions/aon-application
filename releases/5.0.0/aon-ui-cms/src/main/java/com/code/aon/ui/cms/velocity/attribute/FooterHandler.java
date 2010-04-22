package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.FooterDetail;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class FooterHandler {

	private ArrayList<MenuOptionHandler> menu;

	private String content;
	
	public FooterHandler(FooterDetail footer) {
		content = footer.getContent();
		menu = MenuGenerator.getMenuOptionList(footer.getFooter().getMenu());
	}

	public ArrayList<MenuOptionHandler> getMenu() {
		return menu;
	}

	public String getContent() {
		return content;
	}

}
