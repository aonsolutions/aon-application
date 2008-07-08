package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.util.AonUtil;

public class AlbumImageController extends BasicI18nController  implements IOrderedControllerListener {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.ARTICLE_POSITION);


	private Album currentAlbum;
	
	public Album getCurrentAlbum() {
		return currentAlbum;
	}

	public void setCurrentAlbum(Album currentAlbum) {
		this.currentAlbum = currentAlbum;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		AlbumImage ai = (AlbumImage)this.model.getRowData();
		ai.setActive(active);
		getManagerBean().update(ai);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = "- NO VALUE -";
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
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		AlbumImage current = (AlbumImage)getTo();
		current.setImage(image);
	}
	
	public void onSelectThumbnail(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		AlbumImage current = (AlbumImage)getTo();
		current.setThumbnail(image);
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), "" + getCurrentAlbum().getId());
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}