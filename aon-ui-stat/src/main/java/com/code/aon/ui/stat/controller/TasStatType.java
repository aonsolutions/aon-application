package com.code.aon.ui.stat.controller;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.stat.IStatMessages;
import com.code.aon.ui.util.AonUtil;

public enum TasStatType implements IResourceable {

	OWNER_STAT(IStatMessages.STAT_MENU_TAS_OWNER),
	TAS_ITEM_STAT(IStatMessages.STAT_MENU_TAS_TASITEM);

	private String title;
	
	private TasStatType(String title) {
		this.title= title;
	}

    public String getName(Locale locale) {
		return AonUtil.getMessage(IStatMessages.BUNDLE_NAME , title);
    }
	
}