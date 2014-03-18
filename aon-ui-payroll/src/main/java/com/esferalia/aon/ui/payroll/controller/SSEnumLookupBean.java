package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.ss.SSCodeTables;
import com.esferalia.aon.payroll.enumeration.ss.T54;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;


public class SSEnumLookupBean implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String code;
	private String description;
	private ISSEnum ssEnum;
	private String enumName;
	private boolean showEnumLookupWindow;
	
	private PayrollCodeTablesController handler;
	
	public String getCode() {
		if(code==null && getSsEnum()!=null){
			code = getSsEnum().getCode();
		}
		return code;
	}

	public void setCode(String code) {
		this.code = code;
//		setSsEnum(ssEnum)
	}

	public ISSEnum getSsEnum() {
		return ssEnum;
	}

	public void setSsEnum(ISSEnum ssEnum) {
		this.ssEnum = ssEnum;
		if(ssEnum!=null){
			code = ssEnum.getCode();
		} else {
			code = null;
		}
	}

	public String getEnumName() {
		return enumName;
	}

	public void setEnumName(String enumName) {
		this.enumName = enumName;
	}

	public boolean isShowEnumLookupWindow() {
		return showEnumLookupWindow;
	}

	public void setShowEnumLookupWindow(boolean showEnumLookupWindow) {
		this.showEnumLookupWindow = showEnumLookupWindow;
	}

	public String getCodesFilter(){
		return handler.getCodesFilter();
	}
	
	public void setCodesFilter(String filter){
		handler.setCodesFilter(filter);
	}
	
	public DataModel getCodesModel() {
		return handler.getCodesModel();
	}
	
	public void onInitSSCodesModels(ActionEvent event) {
		handler.onInitSSCodesModels(event);
	}
	
	public void onSearch(ActionEvent event) {
		SSCodeTables codeTable = SSCodeTables.getEnumByValue(getEnumName());
		if(codeTable!=null){
			handler = new PayrollCodeTablesController();
			handler.selectSSTable(codeTable);
			setShowEnumLookupWindow(true);
		}
	}
	
	public void loadEnumByCode(ActionEvent event) {
		ContractController controller = (ContractController) FormUtil.getController("contract");
		try {
			Class<?> clazz = Class.forName(PayrollCodeTablesController.SS_ENUMERATIONS_PACKAGE_NAME + "." + getEnumName().toUpperCase());
			for (Object obj : clazz.getEnumConstants()) {
				ISSEnum enumeration = (ISSEnum) obj;
				if(enumeration.getCode().equals(code)){
					// Add here the required S.S. enums
					if(enumeration.getClass()==T54.class){
						controller.getParams().setCollectivePeculiarityQuote((T54) enumeration);
					}
				}
			}
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
	}
	
	public void onSelect(ActionEvent event) {
		setSsEnum((ISSEnum) handler.getCodesModel().getRowData());
		
		ContractController controller = (ContractController) FormUtil.getController("contract");
		
		// Add here the required S.S. enums
		if(getSsEnum().getClass()==T54.class){
			controller.getParams().setCollectivePeculiarityQuote((T54) getSsEnum());
		}
		
		setShowEnumLookupWindow(false);
	}
	
	
}