package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumCategory;
import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.AlbumConfig;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;

public class AlbumCategoryController extends BasicI18nController {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.ALBUM_CATEGORY_POSITION);

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

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}
