package com.code.aon.ui.cms.event;

import com.code.aon.cms.Album;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.AlbumController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		AlbumController controller = (AlbumController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		AlbumController controller = (AlbumController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Album album = (Album)event.getController().getTo();
		album.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AlbumController ac = (AlbumController)event.getController();
		Album a = (Album)event.getController().getTo();
		a.setAlbumCategory(ac.getCurrentAlbumCategory());
		a.setPosition(ac.orderedControllerSupport.getLastPosition(ac));
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try{
			((AlbumController) event.getController()).onSelectAlbumImages(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try{
			((AlbumController) event.getController()).onSelectAlbumImages(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}
}