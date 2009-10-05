package com.code.aon.ui.ebackoffice.event;

import java.util.logging.Logger;

import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.ebackoffice.controller.EccatalogueController;
import com.code.aon.ui.ebackoffice.controller.EcconfigController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;

public class EcCatalogueControllerListener extends ControllerAdapter implements
		IItemConstants {

	private static final Logger LOGGER = Logger
			.getLogger(EcCatalogueControllerListener.class.getName());

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EccatalogueController controller = (EccatalogueController) event
				.getController();
		AonFile img = controller.getImage();
		if (img!=null) {		
			((Eccatalogue)controller.getTo()).setCatalogueImg(img.getData());
		}
		AonFile ico = controller.getIcon();
		if (ico!=null) {		
			((Eccatalogue)controller.getTo()).setCatalogueIcon(ico.getData());
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EccatalogueController controller = (EccatalogueController) event
				.getController();
		AonFile img = controller.getImage();
		if (img!=null) {		
			((Eccatalogue)controller.getTo()).setCatalogueImg(img.getData());
		}
		AonFile ico = controller.getIcon();
		if (ico!=null) {		
			((Eccatalogue)controller.getTo()).setCatalogueIcon(ico.getData());
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {		
		
		EccatalogueController controller  = (EccatalogueController) event.getController();
		Eccatalogue cat = (Eccatalogue) getController().getTo();
		
		if (cat.getCatalogueImg() != null) {
			AonFile image = new AonFile();
			image.setData(cat.getCatalogueImg());
			controller.setImage(image);
		}
		
		if (cat.getCatalogueIcon() != null) {
			AonFile icon = new AonFile();
			icon.setData(cat.getCatalogueIcon());
			controller.setIcon(icon);
		}
		
	/*	
		AonFile image = new AonFile();
		AonFile icon = new AonFile();
		image.setData(cat.getCatalogueImg());
		icon.setData(cat.getCatalogueIcon());
		controller.setImage(image);
		controller.setIcon(icon);*/
	}
	
	
}