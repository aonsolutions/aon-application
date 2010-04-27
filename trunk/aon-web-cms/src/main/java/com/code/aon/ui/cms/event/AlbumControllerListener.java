package com.code.aon.ui.cms.event;

import com.code.aon.cms.Album;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumControllerListener extends ControllerAdapter implements ICMSConstants {

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		FormUtil.getController(ALBUM_IMAGE).onSearch(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		Album album = (Album) event.getController().getTo();
		if ( album.getThumbnailWidth() == null ) {
			album.setThumbnailWidth(Album.DEFAULT_THUMBNAIL_WIDTH);
		}
	}
	
}