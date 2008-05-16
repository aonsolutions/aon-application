package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

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
import com.code.aon.ui.util.AonUtil;

public class AlbumCategoryController extends BasicI18nController {

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(AlbumConfig.class);
		super.onSearch(event);
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
		AlbumCategory albumCategory = (AlbumCategory)this.model.getRowData();
		albumCategory.setActive(active);
		getManagerBean().update(albumCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		AlbumCategoryDetail albumCategoryDetail = (AlbumCategoryDetail)getModelRowdataI18n();
		if (albumCategoryDetail != null) label = albumCategoryDetail.getLabel();
		return label;
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
    	move((AlbumCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	move((AlbumCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectAlbums(ActionEvent event) throws ManagerBeanException, ExpressionException {
		AlbumController ac = (AlbumController)AonUtil.getController("album");
		IManagerBean albumBean = BeanManager.getManagerBean(Album.class);
		AlbumCategory albumCategory = (AlbumCategory) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(albumBean.getFieldName(ICMSAlias.ALBUM_ALBUM_CATEGORY_ID), "" + albumCategory.getId());
		criteria.addOrder(albumBean.getFieldName(ICMSAlias.ALBUM_POSITION));
		ac.setCurrentAlbumCategory(albumCategory);
		ac.setCriteria(criteria);
		ac.onSearch(event);
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