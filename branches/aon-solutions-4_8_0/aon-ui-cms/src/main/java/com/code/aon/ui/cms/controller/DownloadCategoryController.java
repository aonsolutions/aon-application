package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
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

public class DownloadCategoryController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	public void onInit(ActionEvent event){
		((GalleryController)AonUtil.getRegisteredBean("gallery")).onInit(event);
	}
	
	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "download_category_form");
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
		DownloadCategory downloadCategory = (DownloadCategory)this.model.getRowData();
		downloadCategory.setActive(active);
		getManagerBean().update(downloadCategory);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		DownloadCategoryDetail downloadCategoryDetail = getCurrentDetail();
		if (downloadCategoryDetail != null) label = downloadCategoryDetail.getLabel();
		return label;
	}

	private DownloadCategoryDetail getCurrentDetail() throws ManagerBeanException {
		DownloadCategoryDetail downloadCategoryDetail = null;
		DownloadCategory downloadCategory = (DownloadCategory)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_DOWNLOAD_CATEGORY_ID), downloadCategory.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			downloadCategoryDetail = (DownloadCategoryDetail)list.get(0);
		}
		return downloadCategoryDetail;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( DownloadCategory downloadCategory, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = downloadCategory.getPosition();
		int newPosition = oldPosition + movement;
		downloadCategory.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ID), ""+downloadCategory.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			DownloadCategory downloadCat = (DownloadCategory)list.get(0);
			downloadCat.setPosition(newPosition);
			getManagerBean().update(downloadCat);
		}
    	List<DownloadCategory> listObjects = (List<DownloadCategory>) this.model.getWrappedData();
    	DownloadCategory downloadCategoryMoved = listObjects.get( newPosition );
    	downloadCategoryMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_ID), ""+downloadCategoryMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			DownloadCategory downloadCat = (DownloadCategory)list.get(0);
			downloadCat.setPosition(oldPosition);
			getManagerBean().update(downloadCat);
		}
		listObjects.set( newPosition, downloadCategory);
		listObjects.set( oldPosition, downloadCategoryMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((DownloadCategory) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((DownloadCategory) this.model.getRowData(), 1);    	
    }
    
	public void onSelectDownloads(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true;
		DownloadController dc = (DownloadController)AonUtil.getController("download");
		IManagerBean downloadBean = BeanManager.getManagerBean(Download.class);
		DownloadCategory downloadCategory = (DownloadCategory) this.getSelectedTO();
		Criteria criteria = new Criteria();
		criteria.addExpression(downloadBean.getFieldName(ICMSAlias.DOWNLOAD_DOWNLOAD_CATEGORY_ID), "" + downloadCategory.getId());
		criteria.addOrder(downloadBean.getFieldName(ICMSAlias.DOWNLOAD_POSITION));
		dc.setCurrentDownloadCategory(downloadCategory);
		dc.setCriteria(criteria);
		dc.onSearch(event);
		dc.onInit(event);
	}

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.DOWNLOAD_CATEGORY_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			DownloadCategory d = (DownloadCategory)list.get(i);
			int oldPosition = d.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				d.setPosition(newPosition);
				getManagerBean().update(d);
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
		DownloadCategory current = (DownloadCategory)getTo();
		current.setImage(image);
	}
}