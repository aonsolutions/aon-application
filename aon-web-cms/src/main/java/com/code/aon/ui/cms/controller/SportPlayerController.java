package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.cms.SportPlayer;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.util.AonUtil;


public class SportPlayerController extends GridController {
	
	public void onDelImage(ActionEvent event) {
		SportPlayer current = (SportPlayer)getTo();
		current.setPhoto(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		SportPlayer current = (SportPlayer)getTo();
		current.setPhoto(image);
	}

}