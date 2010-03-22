package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.AlbumImageDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class AlbumImageController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private Album currentAlbum;
	
	public Album getCurrentAlbum() {
		return currentAlbum;
	}

	public void setCurrentAlbum(Album currentAlbum) {
		this.currentAlbum = currentAlbum;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "albumImage_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		AlbumImage ai = (AlbumImage)this.model.getRowData();
		ai.setActive(active);
		getManagerBean().update(ai);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nTitle() throws ManagerBeanException {
		String title = "";
		AlbumImageDetail aid = getCurrentDetail();
		if (aid != null) title = aid.getTitle();
		return title;
	}

	private AlbumImageDetail getCurrentDetail() throws ManagerBeanException {
		AlbumImageDetail aid = null;
		AlbumImage ai = (AlbumImage)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_ALBUM_IMAGE_ID), ai.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ALBUM_IMAGE_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			aid = (AlbumImageDetail)list.get(0);
		}
		return aid;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( AlbumImage ai, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = ai.getPosition();
		int newPosition = oldPosition + movement;
		ai.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_ID), ""+ai.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			AlbumImage albumImage = (AlbumImage)list.get(0);
			albumImage.setPosition(newPosition);
			getManagerBean().update(albumImage);
		}
    	List<AlbumImage> listObjects = (List<AlbumImage>) this.model.getWrappedData();
    	AlbumImage aiMoved = listObjects.get( newPosition );
		aiMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_ID), ""+aiMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			AlbumImage albumImage = (AlbumImage)list.get(0);
			albumImage.setPosition(oldPosition);
			getManagerBean().update(albumImage);
		}
		listObjects.set( newPosition, ai);
		listObjects.set( oldPosition, aiMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((AlbumImage) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((AlbumImage) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), "" + getCurrentAlbum().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ALBUM_IMAGE_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			AlbumImage ai = (AlbumImage)list.get(i);
			int oldPosition = ai.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				ai.setPosition(newPosition);
				getManagerBean().update(ai);
			}
		}
	}
	
	private boolean imageSelectionVisible;
	private int type_;
	
	public void onShowImages(ActionEvent event) {
		imageSelectionVisible = true;
		type_ = 0;
	}
	
	public void onCloseImages(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public void onShowThumbnail(ActionEvent event) {
		imageSelectionVisible = true; 
		type_ = 1;
	}
	
	public void onCloseThumbnail(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public void onDelThumbnail(ActionEvent event) {
		AlbumImage current = (AlbumImage)getTo();
		current.setThumbnail(null);
	}
	
	public boolean isImageSelectionVisible(){
		return imageSelectionVisible && type_== 0;
	}

	public boolean isThumbnailSelectionVisible(){
		return imageSelectionVisible && type_== 1;
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		AlbumImage current = (AlbumImage)getTo();
		if (type_ ==0){
			current.setImage(image);
		}else{
			current.setThumbnail(image);
		}
	}
	
	
}