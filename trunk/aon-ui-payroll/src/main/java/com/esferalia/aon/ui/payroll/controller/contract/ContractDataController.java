package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.payroll.ContractData;

public class ContractDataController extends BasicController {
	
	private boolean showOnlyCurrent;
	
	public boolean isShowOnlyCurrent() {
		return showOnlyCurrent;
	}

	public void setShowOnlyCurrent(boolean showOnlyCurrent) {
		this.showOnlyCurrent = showOnlyCurrent;
	}

	@SuppressWarnings("unchecked")
	public List<?> expressionContext(Object suggest) {
		List<String> list = new LinkedList<String>();
		String filter = (String) suggest;
		//TODO verificar de donde coger las expresiones
//		for (String systemDataVariable :getDataVariables()){
//			if (systemDataVariable.startsWith(filter)) {
//				list.add(systemDataVariable);
//			}
//		}
//		for (ContractVariables cv :ContractVariables.values() ){
//			if (cv.getName().startsWith(filter)) {
//				list.add(cv.getName());		
//			}
//		}
		try {
			for(ContractData data: (List<ContractData>)this.getModel().getWrappedData()){
				if (data.getName().contains(filter.toUpperCase())) {
					list.add(data.getName());		
				}
			}
		} catch (ManagerBeanException e) {

		}
		Collections.sort(list);
		return list;
	}
	
	public void onSearchCurrent(ActionEvent event){
		
	}
	
	public void onSearchAll(ActionEvent event){
		
	}

}
