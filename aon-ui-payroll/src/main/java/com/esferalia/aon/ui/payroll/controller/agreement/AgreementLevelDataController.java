package com.esferalia.aon.ui.payroll.controller.agreement;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class AgreementLevelDataController extends LinesController {

	private List<String> dataVariables;
	
	
	public List<String> getDataVariables() {
		if (dataVariables == null) {
			dataVariables = new LinkedList<String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(SystemData.class);
				Criteria criteria = new Criteria();
				
				//TODO ¿Utilizar las fechas del pojo activo?
				Date date = new Date();
				
				String alias = bean.getFieldName(IPayrollAlias.SYSTEM_DATA_END_DATE);
				Expression ex1 = ExpressionUtilities.getNullExpression(alias);
				Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
				criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to:list) {
					SystemData sd = (SystemData) to;
					dataVariables.add(sd.getName());					
				}
			} catch (ManagerBeanException e) {
				
			}
		}
		return dataVariables;
	}

	public void setDataVariables(List<String> dataVariables) {
		this.dataVariables = dataVariables;
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
//		for (ContextVariable cv :ContextVariable.values() ){
//			if (cv.getName().startsWith(filter)) {
//				list.add(cv.getName());		
//			}
//		}
		try {
			for(AgreementLevelData ald: (List<AgreementLevelData>)this.getModel().getWrappedData()){
				if (ald.getName().contains(filter.toUpperCase())) {
					list.add(ald.getName());		
				}
			}
		} catch (ManagerBeanException e) {

		}
		Collections.sort(list);
		return list;
	}
	
	
	
}
