package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.Image;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.controller.support.OrderedControllerSupport;
import com.code.aon.ui.cms.controller.support.IOrderedControllerListener;
import com.code.aon.ui.cms.util.MenuOptionUtil;
import com.code.aon.ui.util.AonUtil;


public class DirectAccessController extends BasicI18nController  implements IOrderedControllerListener {

	public OrderedControllerSupport orderedControllerSupport = new OrderedControllerSupport(ICMSAlias.DIRECT_ACCESS_POSITION);

	private DirectAccessGroup currentGroup;
	
	public DirectAccessGroup getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(DirectAccessGroup currentGroup) {
		this.currentGroup = currentGroup;
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
		DirectAccess da = (DirectAccess)this.model.getRowData();
		da.setActive(active);
		getManagerBean().update(da);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		DirectAccessDetail dad = (DirectAccessDetail)getModelRowdataI18n();
		if (dad != null) label = dad.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = "- NO VALUE -";
		DirectAccessDetail dad = (DirectAccessDetail)getModelRowdataI18n();
		if (dad != null) url = dad.getUrl();
		return url;
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

	public void onDelImage(ActionEvent event) {
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(image);
	}

    public void onMoveUp(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveUp(this);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException, ExpressionException {
    	orderedControllerSupport.onMoveDown(this);
    }

	public void fireBeforeUseCriteria(Criteria criteria) {
		try {
			criteria.addExpression(getManagerBean().getFieldName(ICMSAlias.DIRECT_ACCESS_DIRECT_ACCESS_GROUP_ID), "" + getCurrentGroup().getId());
		} catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}

	protected void afterRemoveSelected(){
		try {
			orderedControllerSupport.reorderObjects(this);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}

}