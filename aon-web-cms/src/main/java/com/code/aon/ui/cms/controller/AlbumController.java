package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

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
import com.code.aon.ui.util.AonUtil;

public class AlbumController extends BasicI18nController {

	private AlbumCategory currentAlbumCategory;
	
	public AlbumCategory getCurrentAlbumCategory() {
		return currentAlbumCategory;
	}

	public void setCurrentAlbumCategory(AlbumCategory currentAlbumCategory) {
		this.currentAlbumCategory = currentAlbumCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
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
		Album a = (Album)this.model.getRowData();
		a.setActive(active);
		getManagerBean().update(a);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = "- NO VALUE -";
		AlbumDetail ad = (AlbumDetail)getModelRowdataI18n();
		if (ad != null) title = ad.getTitle();
		return title;
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
    	move((Album) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((Album) this.model.getRowData(), 1);    	
    }

	public void onSelectAlbumImages(ActionEvent event) throws ManagerBeanException, ExpressionException {
		AlbumImageController aic = (AlbumImageController)AonUtil.getController("albumImage");
		IManagerBean albumImageBean = BeanManager.getManagerBean(AlbumImage.class);
		Album album = (Album) this.getTo();
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
	
	public void onDelImage(ActionEvent event) {
		Album current = (Album)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Album current = (Album)getTo();
		current.setImage(image);
	}

}