package com.code.aon.ui.cms.event;

import java.io.File;

import com.code.aon.cms.AlbumImage;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.AlbumImageController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumImageControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		AlbumImageController controller = (AlbumImageController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	

	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		AlbumImageController controller = (AlbumImageController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		AlbumImage albumImage = (AlbumImage)event.getController().getTo();
		albumImage.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AlbumImageController aic = (AlbumImageController)event.getController();
		AlbumImage ai = (AlbumImage)event.getController().getTo();
		ai.setAlbum(aic.getCurrentAlbum());
		ai.setPosition(aic.orderedControllerSupport.getLastPosition(aic));
		generateThumbnail(ai);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AlbumImage ai = (AlbumImage)event.getController().getTo();
		generateThumbnail(ai);
	}

	private void generateThumbnail(AlbumImage ai){
		if (ai.getThumbnail()==null ||
				ai.getThumbnail().trim().isEmpty()){
			if (ai.getImage()!=null && !ai.getImage().trim().isEmpty()){
				String thumb = ImageUtil.resize(ControllerUtil.getImagesPath()+ai.getImage(),ImageUtil.DEF_MAX_SIZE);
				thumb = thumb.substring(ControllerUtil.getImagesPath().length(), thumb.length());
				try{
					thumb = thumb.replaceAll(File.separator, "/");
				}catch(Exception e){
					thumb = thumb.replaceAll(File.separator+File.separator, "/");
				}
				ai.setThumbnail(thumb);
			}
		}
	}
	
}