package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.AlbumImage;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class AlbumController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private AlbumCategory currentAlbumCategory;
	
	public void onInit(ActionEvent event){
		((GalleryController)AonUtil.getRegisteredBean("gallery")).onInit(event);
	}
	
	public AlbumCategory getCurrentAlbumCategory() {
		return currentAlbumCategory;
	}

	public void setCurrentAlbumCategory(AlbumCategory currentAlbumCategory) {
		this.currentAlbumCategory = currentAlbumCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "album_form");
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
		Album a = (Album)this.model.getRowData();
		a.setActive(active);
		getManagerBean().update(a);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nTitle() throws ManagerBeanException {
		String title = "";
		AlbumDetail ad = getCurrentDetail();
		if (ad != null) title = ad.getTitle();
		return title;
	}

	private AlbumDetail getCurrentDetail() throws ManagerBeanException {
		AlbumDetail ad = null;
		Album a = (Album)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ALBUM_DETAIL_ALBUM_ID), a.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ALBUM_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			ad = (AlbumDetail)list.get(0);
		}
		return ad;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( Album a, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = a.getPosition();
		int newPosition = oldPosition + movement;
		a.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_ID), ""+a.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Album album = (Album)list.get(0);
			album.setPosition(newPosition);
			getManagerBean().update(album);
		}
    	List<Album> listObjects = (List<Album>) this.model.getWrappedData();
    	Album aMoved = listObjects.get( newPosition );
		aMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_ID), ""+aMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Album album = (Album)list.get(0);
			album.setPosition(oldPosition);
			getManagerBean().update(album);
		}
		listObjects.set( newPosition, a);
		listObjects.set( oldPosition, aMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Album) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Album) this.model.getRowData(), 1);    	
    }

	public void onSelectAlbumImages(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		AlbumImageController aic = (AlbumImageController)AonUtil.getController("albumImage");
		IManagerBean albumImageBean = BeanManager.getManagerBean(AlbumImage.class);
		Album album = (Album) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_ALBUM_ID), "" + album.getId());
		criteria.addOrder(albumImageBean.getFieldName(ICMSAlias.ALBUM_IMAGE_POSITION));
		aic.setCurrentAlbum(album);
		aic.setCriteria(criteria);
		aic.onSearch(event);
	}

	public void reorderObjects() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), "" + getCurrentAlbumCategory().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ALBUM_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Album a = (Album)list.get(i);
			int oldPosition = a.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				a.setPosition(newPosition);
				getManagerBean().update(a);
			}
		}
	}
	
	private boolean imageSelectionVisible;
	
	public void onShowImages(ActionEvent event) {
		imageSelectionVisible = true; 
	}
	
	public void onCloseImages(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public boolean isImageSelectionVisible(){
		return imageSelectionVisible;
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Album current = (Album)getTo();
		current.setImage(image);
	}

}