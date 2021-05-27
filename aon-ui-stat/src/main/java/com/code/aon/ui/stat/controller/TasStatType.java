package com.code.aon.ui.stat.controller;

import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_TAS_OWNER;
import static com.code.aon.ui.common.ICommonMessages.STAT_MENU_TAS_TASITEM;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.ui.util.AonUtil;

public enum TasStatType implements IResourceable {

	OWNER_STAT(STAT_MENU_TAS_OWNER),
	TAS_ITEM_STAT(STAT_MENU_TAS_TASITEM);

	private String title;
	
	private TasStatType(String title) {
		this.title= title;
	}

    public String getName(Locale locale) {
		return AonUtil.getMessage(title);
    }
	
}