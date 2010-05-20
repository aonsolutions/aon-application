package com.code.aon.ui.cms.controller;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.code.aon.cms.Bulletin;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class BulletinArticleController extends BasicController{

	private Bulletin currentBulletin;
	
	public Bulletin getCurrentBulletin() {
		return currentBulletin;
	}

	public void setCurrentBulletin(Bulletin currentBulletin) {
		this.currentBulletin = currentBulletin;
	}

}