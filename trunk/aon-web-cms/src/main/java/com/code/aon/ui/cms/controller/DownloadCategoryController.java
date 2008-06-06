package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.DownloadConfig;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.util.AonUtil;

public class DownloadCategoryController extends BasicI18nController {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.DOWNLOAD_CATEGORY_POSITION);

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(DownloadConfig.class);
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
		DownloadCategory downloadCategory = (DownloadCategory)this.model.getRowData();
		downloadCategory.setActive(active);
		getManagerBean().update(downloadCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		DownloadCategoryDetail downloadCategoryDetail = (DownloadCategoryDetail)getModelRowdataI18n();
		if (downloadCategoryDetail != null) label = downloadCategoryDetail.getLabel();
		return label;
	}


	public void onSelectDownloads(ActionEvent event) throws ManagerBeanException, ExpressionException {
		DownloadController dc = (DownloadController)AonUtil.getController("download");
		IManagerBean downloadBean = BeanManager.getManagerBean(Download.class);
		DownloadCategory downloadCategory = (DownloadCategory) this.getTo();
		Criteria criteria = new Criteria();
		criteria.addExpression(downloadBean.getFieldName(ICMSAlias.DOWNLOAD_DOWNLOAD_CATEGORY_ID), "" + downloadCategory.getId());
		criteria.addOrder(downloadBean.getFieldName(ICMSAlias.DOWNLOAD_POSITION));
		dc.setCurrentDownloadCategory(downloadCategory);
		dc.setCriteria(criteria);
		dc.onSearch(event);
	}

	public void onDelImage(ActionEvent event) {
		DownloadCategory current = (DownloadCategory)getTo();
		current.setImage(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DownloadCategory current = (DownloadCategory)getTo();
		current.setImage(image);
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