package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.Menu;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class MenuHandler {

	private String alias;
	
	private ArrayList<MenuOptionHandler> list;
	
	public MenuHandler (Menu menu) {
		alias = menu.getAlias();
		list = MenuGenerator.getMenuOptionList(menu);
	}

	public String getAlias() {
		return alias;
	}

	public ArrayList<MenuOptionHandler> getList() {
		return list;
	}

}
