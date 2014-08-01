package com.esferalia.aon.ui.payroll.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.payroll.enumeration.ss.SSCodeTables;
import com.esferalia.aon.payroll.enumeration.ss.T53;
import com.esferalia.aon.payroll.enumeration.ss.T54;
import com.esferalia.aon.payroll.enumeration.ss.T55;
import com.esferalia.aon.payroll.sepe.SSCodeTablesWriter.ISSEnum;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;


public class SSEnumLookupBean {
	
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
	}

	public String getDescription() {
		if(description==null && getSsEnum()!=null){
			description = getSsEnum().getDescription();
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ISSEnum getSsEnum() {
		return ssEnum;
	}

	public void setSsEnum(ISSEnum ssEnum) {
		this.ssEnum = ssEnum;
		if(ssEnum!=null){
			code = ssEnum.getCode();
			description = ssEnum.getDescription();
		} else {
			code = null;
			description = null;
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
	
	public void clear(ActionEvent event) {
		code = null;
		description = null;
		ssEnum = null;
	}
	
	public void onSearch(ActionEvent event) {
		String value = getEnumName().contains("-")?getEnumName():StringUtils.replace(getEnumName(), "T", "T-");
		SSCodeTables codeTable = SSCodeTables.getEnumByValue(value);
		if(codeTable!=null){
			handler = new PayrollCodeTablesController();
			handler.selectSSTable(codeTable);
			setShowEnumLookupWindow(true);
		}
	}
	
	public void loadEnumByCode(ActionEvent event) {
		enumName = event.getComponent().getParent().getParent().getId().substring(0, 4);
		ContractController controller = (ContractController) FormUtil.getController("contract");
		try {
			if(code!=null){
				Class<?> clazz = Class.forName(PayrollCodeTablesController.SS_ENUMERATIONS_PACKAGE_NAME + "." + getEnumName().toUpperCase().replaceAll("-", ""));
				for (Object obj : clazz.getEnumConstants()) {
					ISSEnum enumeration = (ISSEnum) obj;
					if(enumeration.getCode().toUpperCase().equals(code.toUpperCase())){
						// Add here the required S.S. enums
						if(enumeration.getClass()==T53.class){
							controller.getParams().setPartialTimeReductionIndicator((T53) enumeration);
						} else if(enumeration.getClass()==T54.class){
							controller.getParams().setCollectivePeculiarityQuote((T54) enumeration);
						} else if(enumeration.getClass()==T55.class){
							controller.getParams().setDisabilityIndicator((T55) enumeration);
						}
					}
				}
			}
		} catch (ClassNotFoundException e1) {
			// nothing to do
		}
		clear(event);
	}
	
	public void onSelect(ActionEvent event) {
		setSsEnum((ISSEnum) handler.getCodesModel().getRowData());
		
		ContractController controller = (ContractController) FormUtil.getController("contract");
		
		// Add here the required S.S. enums
		if(getSsEnum().getClass()==T53.class){
			controller.getParams().setPartialTimeReductionIndicator((T53) getSsEnum());
		} else if(getSsEnum().getClass()==T54.class){
			controller.getParams().setCollectivePeculiarityQuote((T54) getSsEnum());
		} else if(getSsEnum().getClass()==T55.class){
			controller.getParams().setDisabilityIndicator((T55) getSsEnum());
		}
		
		setShowEnumLookupWindow(false);
		clear(null);
	}

	public List<ISSEnum> autocomplete(Object suggest) {
		String pref = (String) suggest;
		ArrayList<ISSEnum> result = new ArrayList<ISSEnum>();
		try {
			Class<?> clazz = Class.forName(PayrollCodeTablesController.SS_ENUMERATIONS_PACKAGE_NAME + "." + getEnumName().toUpperCase().replaceAll("-", ""));
			for (Object obj : clazz.getEnumConstants()) {
				ISSEnum enumeration = (ISSEnum) obj;
				if ( ((enumeration.getDescription() != null 
						&& (enumeration.getDescription().toLowerCase().contains(pref.toLowerCase()) 
						|| enumeration.getCode().toLowerCase().contains(pref.toLowerCase())))
						|| "".equals(pref)) ) {
					result.add(enumeration);
				}
			}
		} catch (ClassNotFoundException e) {
			// nothing to do
		}
		return result;
	}
	
	
}