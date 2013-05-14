package com.code.aon.ui.company.controller;

import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_FONT_COLOR;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_SUPPORT_EMAIL;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_SUPPORT_PHONE;
import static com.code.aon.common.enumeration.AppParam.AON_CUSTOMIZE_TITLE;
import static com.code.aon.ui.common.ICommonConstants.FAVICON_NAME;
import static com.code.aon.ui.common.ICommonConstants.HEADER_LOGO_NAME;
import static com.code.aon.ui.common.ICommonConstants.LOGIN_LOGO_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_FAILED_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_START_NAME;
import static com.code.aon.ui.common.ICommonConstants.STATUS_STOP_NAME;
import static com.code.aon.ui.common.ICommonConstants.TOOLBAR_LOGO_NAME;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ui.registry.controller.RegistryAttachController;

public class CompanyCustomizeController extends RegistryAttachController {
	
	private String title;
	
	private String color;
	
	private String supportPhone;
	
	private String supportEmail;

	public void onInit( ActionEvent event ) {
		setTitle( AppParamUtil.getValue(AON_CUSTOMIZE_TITLE) );
		setColor( AppParamUtil.getValue(AON_CUSTOMIZE_FONT_COLOR) );
		setSupportPhone( AppParamUtil.getValue(AON_CUSTOMIZE_SUPPORT_PHONE) );
		setSupportEmail( AppParamUtil.getValue(AON_CUSTOMIZE_SUPPORT_EMAIL) );
		onEditSearch(event);
		onSearch(event);
	}

	public void onSave( ActionEvent event ) {
		AppParamUtil.insertParameter(AON_CUSTOMIZE_TITLE, getTitle());
		AppParamUtil.insertParameter(AON_CUSTOMIZE_FONT_COLOR, getColor());
		AppParamUtil.insertParameter(AON_CUSTOMIZE_SUPPORT_PHONE, getSupportPhone());
		AppParamUtil.insertParameter(AON_CUSTOMIZE_SUPPORT_EMAIL, getSupportEmail());
	}
	
	public boolean isShow() {
		String value = AppParamUtil.getValue(AppParam.AON_CUSTOMIZE_OEM);
		return StringUtils.equals(value, Boolean.TRUE.toString());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	public String getSupportPhone() {
		return supportPhone;
	}

	public void setSupportPhone(String suportPhone) {
		this.supportPhone = suportPhone;
	}

	public String getSupportEmail() {
		return supportEmail;
	}

	public void setSupportEmail(String suportEmail) {
		this.supportEmail = suportEmail;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getCurrentValues() throws ManagerBeanException {
		Set<String> values = new HashSet<String>();
		for( IAttachment attach : (List<IAttachment>) getModel().getWrappedData() ) {
			values.add( attach.getDescription() );
		}
		return values;
	}
	
	public List<SelectItem> getImages() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		list.add( new SelectItem(FAVICON_NAME, FAVICON_NAME + " (16x16)") );
		list.add( new SelectItem(LOGIN_LOGO_NAME, LOGIN_LOGO_NAME + " (130x44)") );
		list.add( new SelectItem(HEADER_LOGO_NAME, HEADER_LOGO_NAME + " (80x27)") );
		list.add( new SelectItem(TOOLBAR_LOGO_NAME, TOOLBAR_LOGO_NAME + " (16x16)") );
		list.add( new SelectItem(STATUS_START_NAME, STATUS_START_NAME + " (114x37)") );
		list.add( new SelectItem(STATUS_STOP_NAME, STATUS_STOP_NAME + " (114x37)") );
		list.add( new SelectItem(STATUS_FAILED_NAME, STATUS_FAILED_NAME + " (114x37)") );
		Set<String> currentValues = getCurrentValues();
		List<SelectItem> finalList = new LinkedList<SelectItem>();
		for( SelectItem item : list ) {
			if (! currentValues.contains(item.getValue())) {
				finalList.add(item);
			}
		}
		return finalList;
	}

	@Override
	public void fileUploaded(UploadEvent event) {
		String value = getAttachment().getDescription();
		super.fileUploaded(event);
		getAttachment().setDescription(value);
	}		
	
}
