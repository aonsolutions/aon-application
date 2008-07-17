package com.code.aon.ui.cms.controller;

import com.code.aon.cms.Bulletin;
import com.code.aon.ui.form.BasicController;

public class BulletinArticleController extends BasicController{

	private Bulletin currentBulletin;
	
	public Bulletin getCurrentBulletin() {
		return currentBulletin;
	}

	public void setCurrentBulletin(Bulletin currentBulletin) {
		this.currentBulletin = currentBulletin;
	}

	@Override
	public void initializeModel() {
		super.initializeModel();
	}

}