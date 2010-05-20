package com.code.aon.ui.cms.event;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumImageControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(ICMSAlias.ALBUM_IMAGE_IMAGE));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AlbumImage ai = (AlbumImage)event.getController().getTo();
		generateThumbnail(ai);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AlbumImage ai = (AlbumImage)event.getController().getTo();
		generateThumbnail(ai);
	}

	private void generateThumbnail(AlbumImage ai){
		if ( StringUtils.isBlank(ai.getThumbnail()) ) {
			if (! StringUtils.isBlank(ai.getImage()) ) {
				File file = ControllerUtil.getImagePath(ai.getImage());
				File thumb = ImageUtil.resize(file, Album.DEFAULT_THUMBNAIL_WIDTH);
				String path = ControllerUtil.getRelativePath(ControllerUtil.getImagesPath(), thumb);
				ai.setThumbnail( path );
			}
		}
	}
	
}