package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import com.code.aon.cms.Menu;
import com.code.aon.ui.cms.velocity.MenuGenerator;

public class MenuHandler {

	private String alias;
	
	private List<MenuOptionHandler> list;
	
	public MenuHandler (Menu menu) {
		alias = menu.getAlias();
		list = MenuGenerator.getMenuOptionList(menu);
	}

	public String getAlias() {
		return alias;
	}

	public List<MenuOptionHandler> getList() {
		return list;
	}

}
