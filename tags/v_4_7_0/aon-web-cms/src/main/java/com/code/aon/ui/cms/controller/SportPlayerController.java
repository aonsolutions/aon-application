package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.cms.SportPlayer;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;


public class SportPlayerController extends BasicController implements ICMSConstants {
	
	public void onDelImage(ActionEvent event) {
		SportPlayer current = (SportPlayer)getTo();
		current.setPhoto(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		SportPlayer current = (SportPlayer)getTo();
		current.setPhoto(image);
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		SportPlayer object = (SportPlayer)this.model.getRowData();
		object.setActive(active);
		getManagerBean().update(object);
	}
	
}