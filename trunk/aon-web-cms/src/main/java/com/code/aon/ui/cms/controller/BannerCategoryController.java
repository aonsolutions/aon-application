package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;

public class BannerCategoryController extends BasicI18nController {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.BANNER_CATEGORY_POSITION);

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
		BannerCategory bannerCategory = (BannerCategory)this.model.getRowData();
		bannerCategory.setActive(active);
		getManagerBean().update(bannerCategory);
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		BannerCategoryDetail bannerCategoryDetail = (BannerCategoryDetail)getModelRowdataI18n();
		if (bannerCategoryDetail != null) label = bannerCategoryDetail.getLabel();
		return label;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}
    
	public void onSelectBanners(ActionEvent event) throws ManagerBeanException, ExpressionException {
		BannerController bc = (BannerController)AonUtil.getController("banner");
		IManagerBean bannerBean = BeanManager.getManagerBean(Banner.class);
		BannerCategory bannerCategory = (BannerCategory) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(bannerBean.getFieldName(ICMSAlias.BANNER_BANNER_CATEGORY_ID), "" + bannerCategory.getId());
		criteria.addOrder(bannerBean.getFieldName(ICMSAlias.BANNER_POSITION));
		bc.setCurrentBannerCategory(bannerCategory);
		bc.setCriteria(criteria);
		bc.onSearch(event);
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