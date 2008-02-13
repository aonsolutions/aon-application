package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.Album;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.MenuOptionUtil;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class DirectAccessController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	private DirectAccessGroup currentGroup;
	
	public void onInit(ActionEvent event){
		((GalleryController)AonUtil.getRegisteredBean("gallery")).onInit(event);
	}
	
	public DirectAccessGroup getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(DirectAccessGroup currentGroup) {
		this.currentGroup = currentGroup;
	}

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "direct_access_form");
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
		DirectAccess da = (DirectAccess)this.model.getRowData();
		da.setActive(active);
		getManagerBean().update(da);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "";
		DirectAccessDetail dad = getCurrentDetail();
		if (dad != null) label = dad.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = "";
		DirectAccessDetail dad = getCurrentDetail();
		if (dad != null) url = dad.getUrl();
		return url;
	}

	private DirectAccessDetail getCurrentDetail() throws ManagerBeanException {
		DirectAccessDetail dad = null;
		DirectAccess da = (DirectAccess)this.model.getRowData();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_DIRECT_ACCESS_ID), da.getId());
		criteria.addEqualExpression(getManagerBeanI18n().getFieldName(ICMSAlias.DIRECT_ACCESS_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
		List<ITransferObject> list = (List<ITransferObject>)getManagerBeanI18n().getList(criteria);
		if (list.size() > 0) {
			dad = (DirectAccessDetail)list.get(0);
		}
		return dad;
	}

	public boolean isVisibleLevel() {
		DirectAccess mo = (DirectAccess)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleLevel(mo.getType());
		}
		return false;
	}

	public boolean isVisibleIdent() {
		DirectAccess mo = (DirectAccess)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleIdent(mo.getType(), mo.getLevel());
		}
		return false;
	}

	public boolean isVisibleUrl() {
		DirectAccess mo = (DirectAccess)getTo();
		if (mo != null) {
			return MenuOptionUtil.isVisibleUrl(mo.getType(),mo.getLevel());
		}
		return false;
	}

	public List<SelectItem> getLevels() throws ManagerBeanException, ExpressionException {
		DirectAccess mo = (DirectAccess)getTo();
		return MenuOptionUtil.getLevels(mo.getType());
	}

	public List<SelectItem> getIdents() throws ManagerBeanException, ExpressionException {
		DirectAccess mo = (DirectAccess)getTo();
		return MenuOptionUtil.getIdents(mo.getType(),mo.getLevel());
	}

	@SuppressWarnings("unchecked")
	private void move( DirectAccess da, int movement ) throws ManagerBeanException, ExpressionException {
		int oldPosition = da.getPosition();
		int newPosition = oldPosition + movement;
		da.setPosition(newPosition);
		Criteria criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_ID), ""+da.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			DirectAccess option = (DirectAccess)list.get(0);
			option.setPosition(newPosition);
			getManagerBean().update(option);
		}
    	List<DirectAccess> listObjects = (List<DirectAccess>) this.model.getWrappedData();
		DirectAccess daMoved = listObjects.get( newPosition );
		daMoved.setPosition( oldPosition );
		criteria = new Criteria();
		criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_ID), ""+daMoved.getId());
		list = getManagerBean().getList(criteria);
		if (list.size() > 0) {
			DirectAccess option = (DirectAccess)list.get(0);
			option.setPosition(oldPosition);
			getManagerBean().update(option);
		}
		listObjects.set( newPosition, da);
		listObjects.set( oldPosition, daMoved );
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((DirectAccess) this.model.getRowData(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
		cancelOnSelect = true; 
    	move((DirectAccess) this.model.getRowData(), 1);    	
    }

	public void reorderObjects() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID),getCurrentGroup().getId());
		criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION));
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (int i = 0; i < list.size(); i++) {
			DirectAccess da = (DirectAccess)list.get(i);
			int oldPosition = da.getPosition();
			int newPosition = i;
			if (oldPosition != newPosition) {
				da.setPosition(newPosition);
				getManagerBean().update(da);
			}
		}
	}

	public int getLastPosition() {
		int position = 0;
		try{
			Criteria criteria = new Criteria();
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), "" + getCurrentGroup().getId());
			criteria.addOrder(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_POSITION), false);
			List<ITransferObject> list = (List<ITransferObject>)getManagerBean().getList(criteria);
			if (list.size() > 0) {
				DirectAccess da = (DirectAccess)list.get(0);
				position = da.getPosition();
				++position;
			}
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
		return position;
	}

	private boolean imageSelectionVisible;
	
	public void onShowImages(ActionEvent event) {
		imageSelectionVisible = true; 
	}
	
	public void onCloseImages(ActionEvent event) {
		imageSelectionVisible = false; 
	}
	
	public void onNoneImage(ActionEvent event) {
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(null);
	}
	
	public boolean isImageSelectionVisible(){
		return imageSelectionVisible;
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(image);
	}

}