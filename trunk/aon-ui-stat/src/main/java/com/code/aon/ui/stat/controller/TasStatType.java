package com.code.aon.ui.stat.controller;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.util.AonUtil;

public enum TasStatType implements IResourceable {

	OWNER_STAT("stat_menu_tas_owner"),
	TAS_ITEM_STAT("stat_menu_tas_tasItem");

	private String title;
	
	private TasStatType(String title) {
		this.title= title;
	}

    public String getName(Locale locale) {
		return AonUtil.getMessage(IStatConstants.STAT_BUNDLE , title);
    }
	
}