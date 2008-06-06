package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.util.AonUtil;

public class BannerController extends BasicI18nController implements IOrderedControllerListener {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.BANNER_POSITION);

	private BannerCategory currentBannerCategory;
	
	public BannerCategory getCurrentBannerCategory() {
		return currentBannerCategory;
	}

	public void setCurrentBannerCategory(BannerCategory currentBannerCategory) {
		this.currentBannerCategory = currentBannerCategory;
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
		Banner b = (Banner)this.model.getRowData();
		b.setActive(active);
		getManagerBean().update(b);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		BannerDetail bd = (BannerDetail)getModelRowdataI18n();
		if (bd != null) label = bd.getLabel();
		return label;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}
	
	public void onDelImage(ActionEvent event) {
		BannerDetail current = (BannerDetail)getToI18n();
		current.setImage(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		BannerDetail current = (BannerDetail)getToI18n();
		current.setImage(image);
	}

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BANNER_BANNER_CATEGORY_ID), "" + getCurrentBannerCategory().getId());
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
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