package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerCategoryDetail;
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

public class BannerCategoryController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "banner_category_form");
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
		BannerCategory bannerCategory = (BannerCategory)this.model.getRowData();
		bannerCategory.setActive(active);
		getManagerBean().update(bannerCategory);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		BannerCategoryDetail bannerCategoryDetail = getCurrentDetail();
		if (bannerCategoryDetail != null) label = bannerCategoryDetail.getLabel();
		return label;
	}

	private BannerCategoryDetail getCurrentDetail() throws ManagerBeanException {
		BannerCategoryDetail bannerCategoryDetail = null;
		BannerCategory bannerCategory = (BannerCategory)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.BANNER_CATEGORY_DETAIL_BANNER_CATEGORY_ID), bannerCategory.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.BANNER_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			bannerCategoryDetail = (BannerCategoryDetail)list.get(0);
		}
		return bannerCategoryDetail;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( BannerCategory bannerCategory, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = bannerCategory.getPosition();
		int newPosition = oldPosition + movement;
		bannerCategory.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BANNER_CATEGORY_ID), ""+bannerCategory.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			BannerCategory bannerCat = (BannerCategory)list.get(0);
			bannerCat.setPosition(newPosition);
			getManagerBean().update(bannerCat);
		}
    	List<BannerCategory> listObjects = (List<BannerCategory>) this.model.getWrappedData();
    	BannerCategory bannerCategoryMoved = listObjects.get( newPosition );
    	bannerCategoryMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BANNER_CATEGORY_ID), ""+bannerCategoryMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			BannerCategory bannerCat = (BannerCategory)list.get(0);
			bannerCat.setPosition(oldPosition);
			getManagerBean().update(bannerCat);
		}
		listObjects.set( newPosition, bannerCategory);
		listObjects.set( oldPosition, bannerCategoryMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((BannerCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((BannerCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectBanners(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		BannerController bc = (BannerController)AonUtil.getController("banner");
		IManagerBean bannerBean = BeanManager.getManagerBean(Banner.class);
		BannerCategory bannerCategory = (BannerCategory) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(bannerBean.getFieldName(ICMSAlias.BANNER_BANNER_CATEGORY_ID), "" + bannerCategory.getId());
		criteria.addOrder(bannerBean.getFieldName(ICMSAlias.BANNER_POSITION));
		bc.setCurrentBannerCategory(bannerCategory);
		bc.setCriteria(criteria);
		bc.onSearch(event);
		bc.onInit(event);
	}

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.BANNER_CATEGORY_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			BannerCategory b = (BannerCategory)list.get(i);
			int oldPosition = b.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				b.setPosition(newPosition);
				getManagerBean().update(b);
			}
		}
	}
}