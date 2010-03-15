package com.code.aon.ui.cms.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.BannerType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FolderNodeUserObject;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class BannerController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private BannerCategory currentBannerCategory;
	
	public void onInit(ActionEvent event){
		((GalleryController)AonUtil.getRegisteredBean("gallery")).onInit(event);
	}
	
	public BannerCategory getCurrentBannerCategory() {
		return currentBannerCategory;
	}

	public void setCurrentBannerCategory(BannerCategory currentBannerCategory) {
		this.currentBannerCategory = currentBannerCategory;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "banner_form");
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
		Banner b = (Banner)this.model.getRowData();
		b.setActive(active);
		getManagerBean().update(b);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		BannerDetail bd = getCurrentDetail();
		if (bd != null) label = bd.getLabel();
		return label;
	}

	private BannerDetail getCurrentDetail() throws ManagerBeanException {
		BannerDetail bd = null;
		Banner b = (Banner)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.BANNER_DETAIL_BANNER_ID), b.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.BANNER_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			bd = (BannerDetail)list.get(0);
		}
		return bd;
	}

	public void onAccept(ActionEvent event) {
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	private void move( Banner b, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = b.getPosition();
		int newPosition = oldPosition + movement;
		b.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BANNER_ID), ""+b.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Banner banner = (Banner)list.get(0);
			banner.setPosition(newPosition);
			getManagerBean().update(banner);
		}
    	List<Banner> listObjects = (List<Banner>) this.model.getWrappedData();
    	Banner bMoved = listObjects.get( newPosition );
		bMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BANNER_ID), ""+bMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			Banner banner = (Banner)list.get(0);
			banner.setPosition(oldPosition);
			getManagerBean().update(banner);
		}
		listObjects.set( newPosition, b);
		listObjects.set( oldPosition, bMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Banner) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((Banner) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.BANNER_BANNER_CATEGORY_ID), "" + getCurrentBannerCategory().getId());
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.BANNER_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			Banner b = (Banner)list.get(i);
			int oldPosition = b.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				b.setPosition(newPosition);
				getManagerBean().update(b);
			}
		}
	}
	
	public List<SelectItem> getBannerTypes() throws ManagerBeanException {
		List<SelectItem> bannerTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		SelectItem item = new SelectItem();
		for (BannerType bannerType : BannerType.values()) {
			String name = bannerType.getName(locale);
			item = new SelectItem(bannerType, name);
			bannerTypes.add(item);
		}
		return bannerTypes;
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
		BannerDetail current = (BannerDetail)getToI18n();
		current.setImage(image);
	}

}