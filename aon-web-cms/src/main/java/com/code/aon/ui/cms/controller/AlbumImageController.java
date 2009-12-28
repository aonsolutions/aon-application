package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.util.AonUtil;

public class AlbumImageController extends BasicI18nController implements ICMSConstants, Constants {

	public String getI18nTitle() throws ManagerBeanException {
		String title = NO_VALUE_LABEL;
		AlbumImageDetail aid = (AlbumImageDetail)getModelRowdataI18n();
		if (aid != null) title = aid.getTitle();
		return title;
	}

	public void onDelImage(ActionEvent event) {
		AlbumImage current = (AlbumImage)getTo();
		current.setImage(null);
	}
	
	public void onDelThumbnail(ActionEvent event) {
		AlbumImage current = (AlbumImage)getTo();
		current.setThumbnail(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		AlbumImage current = (AlbumImage)getTo();
		current.setImage(image);
	}
	
	public void onSelectThumbnail(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		AlbumImage current = (AlbumImage)getTo();
		current.setThumbnail(image);
	}

	public void onAlbumCriteria(ActionEvent event) throws ManagerBeanException {
		AlbumController albumController = (AlbumController)AonUtil.getRegisteredBean(ALBUM);
		Album albumTo = (Album)albumController.getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID),albumTo.getId());
		setCriteria(criteria);
	}

}