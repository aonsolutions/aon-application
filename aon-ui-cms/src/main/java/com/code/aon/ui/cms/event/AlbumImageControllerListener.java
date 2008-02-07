package com.code.aon.ui.cms.event;

import java.io.File;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.AlbumImageController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlbumImageControllerListener extends ControllerAdapter {
	
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
		ai.setPosition(getLastPosition(aic));
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
	
	private int getLastPosition(AlbumImageController aic) {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(aic.getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), "" + aic.getCurrentAlbum().getId());
			criteria.addOrder(aic.getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)aic.getManagerBean().getList(criteria);
			if (list.size() > 0) {
				AlbumImage ai = (AlbumImage)list.get(0);
				position = ai.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}
}