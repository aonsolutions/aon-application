package com.code.aon.ui.cms.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.cms.DirectAccess;
import com.code.aon.cms.DirectAccessDetail;
import com.code.aon.cms.Image;
import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.MenuOptionUtil;
import com.code.aon.ui.cms.util.ReferenceChecker;
import com.code.aon.ui.util.AonUtil;

public class DirectAccessController extends BasicI18nController implements ICMSConstants, Constants {
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		DirectAccessDetail dad = (DirectAccessDetail)getModelRowdataI18n();
		if (dad != null) label = dad.getLabel();
		return label;
	}

	public String getI18nUrl() throws ManagerBeanException {
		String url = NO_VALUE_LABEL;
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
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DirectAccess current = (DirectAccess)getTo();
		current.setImage(image);
	}

	public boolean isUsed() throws ManagerBeanException {
		Integer id = ((DirectAccess) getTo()).getId();
		return ReferenceChecker.isInModulaPage(id, ModularPageOptionType.DIRECT_ACCESS);
	}	
	
}