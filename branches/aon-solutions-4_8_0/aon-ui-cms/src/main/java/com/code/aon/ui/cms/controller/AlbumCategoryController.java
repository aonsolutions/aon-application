package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.AlbumConfig;
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

public class AlbumCategoryController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@Override
	public void onReset(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(AlbumConfig.class);
		super.onReset(event);
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "album_category_form");
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
		AlbumCategory albumCategory = (AlbumCategory)this.model.getRowData();
		albumCategory.setActive(active);
		getManagerBean().update(albumCategory);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		AlbumCategoryDetail albumCategoryDetail = getCurrentDetail();
		if (albumCategoryDetail != null) label = albumCategoryDetail.getLabel();
		return label;
	}

	private AlbumCategoryDetail getCurrentDetail() throws ManagerBeanException {
		AlbumCategoryDetail albumCategoryDetail = null;
		AlbumCategory albumCategory = (AlbumCategory)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_ALBUM_CATEGORY_ID), albumCategory.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.ALBUM_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			albumCategoryDetail = (AlbumCategoryDetail)list.get(0);
		}
		return albumCategoryDetail;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( AlbumCategory albumCategory, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = albumCategory.getPosition();
		int newPosition = oldPosition + movement;
		albumCategory.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_CATEGORY_ID), ""+albumCategory.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			AlbumCategory albumCat = (AlbumCategory)list.get(0);
			albumCat.setPosition(newPosition);
			getManagerBean().update(albumCat);
		}
    	List<AlbumCategory> listObjects = (List<AlbumCategory>) this.model.getWrappedData();
    	AlbumCategory albumCategoryMoved = listObjects.get( newPosition );
    	albumCategoryMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.ALBUM_CATEGORY_ID), ""+albumCategoryMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			AlbumCategory albumCat = (AlbumCategory)list.get(0);
			albumCat.setPosition(oldPosition);
			getManagerBean().update(albumCat);
		}
		listObjects.set( newPosition, albumCategory);
		listObjects.set( oldPosition, albumCategoryMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((AlbumCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((AlbumCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectAlbums(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		AlbumController ac = (AlbumController)AonUtil.getController("album");
		IManagerBean albumBean = BeanManager.getManagerBean(Album.class);
		AlbumCategory albumCategory = (AlbumCategory) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), "" + albumCategory.getId());
		criteria.addOrder(albumBean.getFieldName(ICMSAlias.ALBUM_POSITION));
		ac.setCurrentAlbumCategory(albumCategory);
		ac.setCriteria(criteria);
		ac.onSearch(event);
		ac.onInit(event);
	}

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.ALBUM_CATEGORY_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			AlbumCategory ac = (AlbumCategory)list.get(i);
			int oldPosition = ac.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				ac.setPosition(newPosition);
				getManagerBean().update(ac);
			}
		}
	}
}